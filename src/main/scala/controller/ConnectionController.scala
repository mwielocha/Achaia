package controller

import view.ConnectionView
import scala.swing.event.{EditDone, KeyReleased, ButtonClicked}
import util.{ExceptionHandling, Logging}
import model.ConnectionModel
import cassandra.{CassandraService, CassandraAware}
import scala.swing.event.Key._

/**
 * author mikwie
 *
 */
class ConnectionController extends Logging with ExceptionHandling {

  val view = new ConnectionView
  var connectionModel = ConnectionModel()

  view.listenTo(view.connect)
  view.listenTo(view.nameTextField)
  view.listenTo(view.hostTextField)
  view.listenTo(view.portTextField)

  view.reactions += {
    case ButtonClicked(view.connect) => {
      connect()
    }
  }

  view.reactions += {
    case KeyReleased(view.connect, Enter, _, _) => connect()
      // TODO: connect on enter
//    case EditDone(view.nameTextField) => connect()
//    case EditDone(view.hostTextField) => connect()
//    case EditDone(view.portTextField) => connect()
    case any => //logger.debug(s"Event cought: $any")
  }
  
  def connect() {
    connectionModel = getData
    logger.info("Connecting to: " + connectionModel)
    withExceptionHandling {
      new ClusterController with CassandraAware {
        override lazy val cassandraService = new CassandraService(
          connectionModel.name,
          connectionModel.host,
          connectionModel.port
        )
      }
    }
    view.dispose()
  }
  

  def setData = {
    view.nameTextField.text = connectionModel.name
    view.hostTextField.text = connectionModel.host
    view.portTextField.text = connectionModel.port.toString
  }

  def getData = {
    ConnectionModel(
      view.nameTextField.text,
      view.hostTextField.text,
      view.portTextField.text.toInt
    )
  }

  setData
}
