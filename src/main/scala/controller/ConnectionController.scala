package controller

import view.ConnectionView
import scala.swing.event.ButtonClicked
import util.Logging
import model.ConnectionModel

/**
 * author mikwie
 *
 */
class ConnectionController extends Logging {

  val view = new ConnectionView
  var connectionModel = ConnectionModel()

  view.listenTo(view.connect)
  view.reactions += {
    case ButtonClicked(view.connect) => {
      connectionModel = getData
      logger.info("Connecting to: " + connectionModel)
      new ClusterController
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
