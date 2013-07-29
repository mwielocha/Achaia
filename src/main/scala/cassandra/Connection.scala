package cassandra

import com.netflix.astyanax.{Keyspace, AstyanaxContext}
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl
import com.netflix.astyanax.connectionpool.NodeDiscoveryType
import com.netflix.astyanax.connectionpool.impl.{CountingConnectionPoolMonitor, ConnectionPoolConfigurationImpl}
import com.netflix.astyanax.thrift.ThriftFamilyFactory
import com.netflix.astyanax.AstyanaxContext.Builder

/**
 * author mikwie
 *
 */
trait Connection[Instance] {

  val clusterName: String
  val clusterHost: String
  val clusterPort: Int

  lazy val cassandraContextBuilder: Builder = new AstyanaxContext.Builder()
    .forCluster(clusterName)
    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
    .setDiscoveryType(NodeDiscoveryType.NONE)
  )
    .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("ConnectionPool_" + clusterName)
    .setConnectTimeout(30000)
    .setSocketTimeout(30000)
    .setMaxTimeoutWhenExhausted(30000)
    .setBlockedThreadThreshold(20)
    .setMaxBlockedThreadsPerHost(50)
    .setMaxConnsPerHost(30)
    .setInitConnsPerHost(10)
    .setPort(clusterPort)
    .setSeeds(clusterHost + ":" + clusterPort)
  )
    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())

  def connect: Instance


}
