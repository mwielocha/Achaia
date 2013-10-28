package cassandra

import com.netflix.astyanax.{Keyspace, Cluster}
import scala.collection.mutable
import scala.collection.JavaConversions._
import util.Logging
import com.netflix.astyanax.model.{Column, Rows, ColumnFamily}
import com.netflix.astyanax.serializers.{LongSerializer, TimeUUIDSerializer, StringSerializer}
import com.netflix.astyanax.Serializer

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
    serializer.fromByteBuffer(serializer.fromString(rowKey))
  }

  def serializeColumnKey(keyspace: String, columnFamily: String, columnKey: String): AnyRef = {
    val comparator = cluster.describeKeyspace(keyspace).getColumnFamily(columnFamily).getComparatorType
    val serializer = matchSerializer(comparator)
    serializer.fromByteBuffer(serializer.fromString(columnKey))
  }

  def createColumnfamily(keyspace: String, columnFamily: String): ColumnFamily[AnyRef, AnyRef] = {
    val comparator = cluster.describeKeyspace(keyspace).getColumnFamily(columnFamily).getComparatorType
    val validator = cluster.describeKeyspace(keyspace).getColumnFamily(columnFamily).getKeyValidationClass
    val instance = new ColumnFamily[AnyRef, AnyRef](columnFamily,
      matchSerializer(validator), matchSerializer(comparator))
    instance.asInstanceOf[ColumnFamily[AnyRef, AnyRef]]
  }

  private def matchSerializer(className: String): Serializer[AnyRef] = {
    (className.reverse.takeWhile(_ != '.').reverse.replace(")", "") match {
      case "TimeUUIDType" => TimeUUIDSerializer.get()
      case "CounterType" | "LongType" => LongSerializer.get()
      case other => logger.debug(s"serializer: $other"); StringSerializer.get()
    }).asInstanceOf[Serializer[AnyRef]]
  }
}
