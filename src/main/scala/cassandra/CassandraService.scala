package cassandra

import com.netflix.astyanax.{Keyspace, Cluster}
import scala.collection.mutable
import scala.collection.JavaConversions._
import util.Logging
import com.netflix.astyanax.model.{Rows, ColumnFamily}
import com.netflix.astyanax.serializers.StringSerializer

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
    val keyspaceConnection = new KeyspaceConnection(keyspaceDefinition.getName,
      clusterName: String, clusterHost: String, clusterPort: Int)
    logger.info("Connecting to keyspace: " + keyspaceDefinition.getName)
    keyspaces += (keyspaceDefinition.getName -> keyspaceConnection.connect)
  })

  def apply(keyspaceName: String): Keyspace = {
    keyspaces(keyspaceName)
  }

  def getKeyspaces: Seq[String] = cluster.describeKeyspaces().map(_.getName)

  def getColumnFamilies(keyspace: String): Seq[String] = cluster.describeKeyspaces()
    .find(_.getName == keyspace).map(_.getColumnFamilyList.map(_.getName)).getOrElse(Seq("Error"))

  def cassandraUri = clusterName + ":" + clusterHost + ":" + clusterPort

  def query(keyspace: String, columnFamily: String): Rows[String, String] = {
    this(keyspace).prepareQuery(new ColumnFamily[String, String](columnFamily,
      StringSerializer.get(), StringSerializer.get()
    )).getAllRows.withColumnRange(null: String, null: String, false, 6)
      .execute().getResult
  }
}
