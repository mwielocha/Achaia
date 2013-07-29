package controller

import view.ConnectionView
import scala.swing.event.ButtonClicked
import util.{ExceptionHandling, Logging}
import model.ConnectionModel
import cassandra.{CassandraService, CassandraAware}

/**
 * author mikwie
 *
 */
class ConnectionController extends Logging with ExceptionHandling {

  val view = new ConnectionView
  var connectionModel = ConnectionModel()

  view.listenTo(view.connect)
  view.reactions += {
    case ButtonClicked(view.connect) => {
      connectionModel = getData
      logger.info("Connecting to: " + connectionModel)
      withExceptionHandling {
        new ClusterController with CassandraAware {
          override val cassandraService = new CassandraService(
            connectionModel.name,
            connectionModel.host,
            connectionModel.port
          )
        }
      }
      view.dispose()
    }
  }

  def setData = {
    view.nameTextField.text = connectionModel.name
    view.hostTestField.text = connectionModel.host
    view.portTextField.text = connectionModel.port.toString
  }

  def getData = {
    ConnectionModel(
      view.nameTextField.text,
      view.hostTestField.text,
      view.portTextField.text.toInt
    )
  }

  setData
}
