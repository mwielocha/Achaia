package controller

import view.ConnectionView
import scala.swing.event.{EditDone, KeyReleased, ButtonClicked}
import util.{ExceptionHandling, Logging}
import model.ConnectionModel
import cassandra.{CassandraService, CassandraAware}
import scala.swing.event.Key._
import scala.io.{Codec, Source}
import java.io.File
import org.jdesktop.swingx.combobox.ListComboBoxModel
import scala.swing.ComboBox
import com.google.common.io.Files
import com.google.common.base.Charsets

/**
 * author mikwie
 *
 */
class ConnectionController extends Logging with ExceptionHandling {

  val view = new ConnectionView
  var connectionModel = loadSavedConnections

  view.listenTo(view.connect)
  view.listenTo(view.hostComboBox)

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

      saveConnection(connectionModel.name)
      view.dispose()
    }
  }
  

  def setData = {
    view.hostComboBox.item = s"${connectionModel.host}:${connectionModel.port.toString}"
  }

  def getData = {
    val hostName = view.hostComboBox.item
    ConnectionModel(
      hostName,
      hostName.split(":")(0),
      hostName.split(":")(1).toInt
    )
  }

  def loadSavedConnections: ConnectionModel = {
    val file = new File("connections.txt")
    if(file.exists()) {
      val savedConnections = Source.fromFile(file)(Codec.UTF8).getLines().toSeq
      view.hostComboBox.peer.setModel {
        ComboBox.newConstantModel(savedConnections)
      }

      savedConnections.headOption.map(lastConnection => {
        val array = lastConnection.split(":")
        ConnectionModel(lastConnection, array(0), array(1).toInt)
      }).getOrElse(ConnectionModel())
    } else {
      ConnectionModel()
    }
  }

  def saveConnection(connection: String) = {
    val file = new File("connections.txt")
    val saved = file.exists() match {
      case true => Source.fromFile(file)(Codec.UTF8).getLines().toSeq
      case false => Nil
    }

    val udpated = (connection +: saved).distinct.take(5).mkString("\n")
    Files.write(udpated, file, Charsets.UTF_8)
  }

  setData
}
