package cassandra

import com.netflix.astyanax.{Keyspace, Cluster}
import scala.collection.mutable
import scala.collection.JavaConversions._
import util.Logging
import com.netflix.astyanax.model.{Composite, Column, Rows, ColumnFamily}
import com.netflix.astyanax.serializers.{CompositeSerializer, LongSerializer, TimeUUIDSerializer, StringSerializer}
import com.netflix.astyanax.Serializer
import java.util
import java.nio.ByteBuffer

/**
 * author mikwie
 *
 */

trait CassandraAware {

  val cassandraService: CassandraService

}

class CassandraService(val clusterName: String, val clusterHost: String, val clusterPort: Int) extends Logging {

  private val keyspaces: mutable.HashMap[String, Keyspace] = new mutable.HashMap

  val clusterConnection = new ClusterConnection(clusterName, clusterHost, clusterPort)
  logger.info("Connecting to cluster: " + clusterHost + ":" + clusterHost + "/" + clusterPort)
  val cluster = clusterConnection.connect

  cluster.describeKeyspaces().foreach(keyspaceDefinition => {
    logger.info("Connecting to keyspace: " + keyspaceDefinition.getName)
    keyspaces += (keyspaceDefinition.getName -> cluster.getKeyspace(keyspaceDefinition.getName))
  })

  def apply(keyspaceName: String): Keyspace = {
    keyspaces(keyspaceName)
  }

  def getKeyspaces: Seq[String] = cluster.describeKeyspaces().map(_.getName)

  def getColumnFamilies(keyspace: String): Seq[String] = cluster.describeKeyspaces()
    .find(_.getName == keyspace).map(_.getColumnFamilyList.map(_.getName)).getOrElse(Seq("Error"))

  def cassandraUri = clusterName + ":" + clusterHost + ":" + clusterPort

  def query(keyspace: String, columnFamily: String): Rows[AnyRef, AnyRef] = {
    val comparatorType = cluster.describeKeyspace(keyspace).getColumnFamily(columnFamily).getComparatorType
    val validatorType = cluster.describeKeyspace(keyspace).getColumnFamily(columnFamily).getKeyValidationClass
    logger.debug("Comparator: " + comparatorType + ", validator: " + validatorType)

    this(keyspace).prepareQuery(createColumnfamily(keyspace, columnFamily))
      .getAllRows.setRowLimit(20).withColumnRange(getNull[AnyRef], getNull[AnyRef], false, 20)
      .execute().getResult
  }

  def getNull[C]: C = null.asInstanceOf[C]

  def queryWithRowKey(keyspace: String, columnFamily: String, rowKey: String): Rows[AnyRef, AnyRef] = {
    this(keyspace).prepareQuery(createColumnfamily(keyspace, columnFamily))
      .getRowSlice(serializeRowKey(keyspace, columnFamily, rowKey))
      .withColumnRange(getNull[AnyRef], getNull[AnyRef], false, 61)
      .execute().getResult
  }
  
  def queryWithBothKeys(keyspace: String, columnFamily: String, rowKey: String, columnKey: String): Rows[AnyRef, AnyRef] = {
    this(keyspace).prepareQuery(createColumnfamily(keyspace, columnFamily))
      .getRowSlice(serializeRowKey(keyspace, columnFamily, rowKey))
      .withColumnSlice(serializeColumnKey(keyspace, columnFamily, columnKey))
      .execute().getResult
  }

  def serializeRowKey(keyspace: String, columnFamily: String, rowKey: String): AnyRef = {
    val validator = cluster.describeKeyspace(keyspace).getColumnFamily(columnFamily).getKeyValidationClass
    val serializer = matchSerializer(validator)
    logger.debug(s"Serializer matched: $serializer")
    serializer.fromByteBuffer(serializer.fromString(rowKey))
  }

  def serializeColumnKey(keyspace: String, columnFamily: String, columnKey: String): AnyRef = {
    val comparator = cluster.describeKeyspace(keyspace).getColumnFamily(columnFamily).getComparatorType
    val serializer = matchSerializer(comparator)
    logger.debug(s"Serializer matched: $serializer")
    serializer.fromByteBuffer(serializer.fromString(columnKey))
  }

  def createColumnfamily(keyspace: String, columnFamily: String): ColumnFamily[AnyRef, AnyRef] = {
    val comparator = cluster.describeKeyspace(keyspace).getColumnFamily(columnFamily).getComparatorType
    val validator = cluster.describeKeyspace(keyspace).getColumnFamily(columnFamily).getKeyValidationClass
    val instance = new ColumnFamily[AnyRef, AnyRef](columnFamily,
      matchSerializer(validator), matchSerializer(comparator))
    instance.asInstanceOf[ColumnFamily[AnyRef, AnyRef]]
  }

  val CompositeTypeMatcher = """org.apache.cassandra.db.marshal.CompositeType\((.*)\)""".r
  val ReverseTypeMatcher = """org.apache.cassandra.db.marshal.ReversedType\((.*)\)""".r

  private def nameOfType(className: String) = {
    className.reverse.takeWhile(_ != '.').reverse.replace(")", "")
  }

  case class CustomCompositeSerializer(val serializers: Seq[String]) extends CompositeSerializer {

    override def getComparators: util.List[String] = serializers.map(nameOfType).toList

    val CompositeKey = "\\[(.*)\\]".r

    override def fromString(key: String): ByteBuffer = {
      key match {
        case CompositeKey(keys) => {
          val objects = keys.split(",").map(_.trim).zip(serializers).map {
            case (value, className) => {
              val serializer = matchSerializer(className)
              serializer.fromByteBuffer(serializer.fromString(value))
            }
          }

          new Composite(objects: _*).serialize()
        }
        case key => throw new IllegalArgumentException()
      }
    }
  }

  private def matchSerializer(className: String): Serializer[AnyRef] = {
    logger.debug(s"Matching serializer for: $className")
    (className match {
      case CompositeTypeMatcher(inner) => {
        logger.debug(s"Inner serializers: $inner")
        CustomCompositeSerializer(inner.split(","))
      }
      case className => {
        (nameOfType(className) match {
          case "TimeUUIDType" => TimeUUIDSerializer.get()
          case "CounterColumnType" | "LongType" => LongSerializer.get()
          case "CompositeType" => CompositeSerializer.get()
          case other => logger.debug(s"serializer: $other"); StringSerializer.get()
        })
      }
    }).asInstanceOf[Serializer[AnyRef]]
  }
}
