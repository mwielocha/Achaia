import controller.ConnectionController
import javax.swing.{JDesktopPane, JFileChooser, UIManager}
import swing._
import view.ConnectionView

object Main extends App {

  UIManager.setLookAndFeel(
    ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel()
  )

  new ConnectionController

}