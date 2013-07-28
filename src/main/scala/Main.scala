import javax.swing.{JDesktopPane, JFileChooser, UIManager}
import swing._
import view.ConnectionView

object Main extends App {

//  System.setProperty(
//    "Quaqua.design","tiger"
//  );

//  UIManager.setLookAndFeel(
//    ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel()
//  )

  new ConnectionView(null).visible = true

}