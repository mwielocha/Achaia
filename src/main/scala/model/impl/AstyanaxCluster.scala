package model.impl

import _root_.util.Logging
import model._
import com.netflix.astyanax._
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl
import com.netflix.astyanax.connectionpool.NodeDiscoveryType
import com.netflix.astyanax.connectionpool.impl.{CountingConnectionPoolMonitor, ConnectionPoolConfigurationImpl}
import com.netflix.astyanax.thrift.ThriftFamilyFactory
import scala.collection.JavaConversions._
import javax.swing.tree.TreeNode

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
class AstyanaxCluster(val clusterName: String, val clusterHost: String, val clusterPort: Int) extends ClusterProxy with Logging {

  logger.info("Connecting to cluster: %s:%s:%s".format(clusterName, clusterHost, clusterPort))

  lazy val cassandraContext: AstyanaxContext[Cluster] = new AstyanaxContext.Builder()
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
  ).buildCluster(ThriftFamilyFactory.getInstance())

  val name = clusterName

  cassandraContext.start()

  def parent: TreeNode = null

  val keyspaces: Seq[AstyanaxKeyspace] = cassandraContext.getClient.describeKeyspaces().map(definition => {
    new AstyanaxKeyspace(this, definition)
  })

  override def toString: String = name
}
