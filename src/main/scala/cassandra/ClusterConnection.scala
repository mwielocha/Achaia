package cassandra

import com.netflix.astyanax.Cluster
import com.netflix.astyanax.thrift.ThriftFamilyFactory

/**
 * author mikwie
 *
 */
class ClusterConnection(val clusterName: String, val clusterHost: String, val clusterPort: Int) extends Connection[Cluster] {

  def connect: Cluster = {
    val cassandraContext = cassandraContextBuilder.buildCluster(ThriftFamilyFactory.getInstance())
    cassandraContext.start()
    cassandraContext.getClient
  }
}
