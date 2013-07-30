import controller.ConnectionController
import javax.swing.UIManager.LookAndFeelInfo
import javax.swing.{JDesktopPane, JFileChooser, UIManager}
import swing._
import view.ConnectionView
import scala.collection.JavaConversions._

object Main extends App {

//  UIManager.setLookAndFeel(
//    ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel()
//  )
//  UIManager.getInstalledLookAndFeels().find(_.getName == "Nimbus") match {
//    case Some(laf) => UIManager.setLookAndFeel(laf.getClassName());
//    case None =>
//  }

      new ConnectionController

}