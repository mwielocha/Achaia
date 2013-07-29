package controller

import view.ClusterView
import cassandra.CassandraAware

/**
 * author mikwie
 *
 */
class ClusterController {

  self: CassandraAware =>

  val view = new ClusterView(cassandraService.cassandraUri)

}
