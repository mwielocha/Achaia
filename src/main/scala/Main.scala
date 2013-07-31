import controller.ConnectionController
import javax.swing.UIManager.LookAndFeelInfo
import javax.swing.{JOptionPane, JDesktopPane, JFileChooser, UIManager}
import org.simplericity.macify.eawt.{DefaultApplication, ApplicationEvent, ApplicationListener}
import swing._
import view.ConnectionView
import scala.collection.JavaConversions._

object Main extends App with ApplicationListener {

//  UIManager.setLookAndFeel(
//    ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel()
//  )
//  UIManager.getInstalledLookAndFeels().find(_.getName == "Nimbus") match {
//    case Some(laf) => UIManager.setLookAndFeel(laf.getClassName());
//    case None =>
//  }

  System.setProperty("com.apple.macos.useScreenMenuBar", "true")
  System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Achaia");

  new ConnectionController

  val defaultApplication = new DefaultApplication()
  defaultApplication.addApplicationListener(this)

  def handleAbout(event: ApplicationEvent) {
    JOptionPane.showMessageDialog(null, "OS X told us to open " + event.getFilename());
    event.setHandled(true)
  }

  def handleOpenApplication(event: ApplicationEvent) {}

  def handleOpenFile(event: ApplicationEvent) {
    JOptionPane.showMessageDialog(null, "OS X told us to open " + event.getFilename());
  }

  def handlePreferences(event: ApplicationEvent) {

  }

  def handlePrintFile(event: ApplicationEvent) {}

  def handleQuit(event: ApplicationEvent) {}

  def handleReopenApplication(event: ApplicationEvent) {}
}