package cassandra

import com.netflix.astyanax.{Keyspace, Cluster}
import com.netflix.astyanax.thrift.ThriftFamilyFactory

/**
 * author mikwie
 *
 */
class KeyspaceConnection (val keyspaceName: String,
                          val clusterName: String,
                          val clusterHost: String,
                          val clusterPort: Int) extends Connection[Keyspace] {

  def connect: Keyspace = {
    val cassandraContext = cassandraContextBuilder
      .forKeyspace(keyspaceName)
      .buildKeyspace(ThriftFamilyFactory.getInstance())
    cassandraContext.start()
    cassandraContext.getClient
  }
}
