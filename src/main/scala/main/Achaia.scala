package main

import controller.ConnectionController
import java.awt.EventQueue
import javax.swing.UIManager.LookAndFeelInfo
import javax.swing._
import org.simplericity.macify.eawt.{DefaultApplication, ApplicationEvent, ApplicationListener}
import swing._
import view.ConnectionView
import scala.collection.JavaConversions._

object Achaia extends App with ApplicationListener {

  System.setProperty("apple.laf.useScreenMenuBar", "true")
  System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Achaia");

  SwingUtilities.invokeLater(new Runnable {
    def run() {
      val defaultApplication = new DefaultApplication()
      defaultApplication.addApplicationListener(Achaia.this)
      new ConnectionController
    }
  })

  def handleAbout(event: ApplicationEvent) {
    JOptionPane.showMessageDialog(null, "Achaia 1.0.2-SNAPSHOT");
    event.setHandled(true)
  }

  def handleOpenApplication(event: ApplicationEvent) {}

  def handleOpenFile(event: ApplicationEvent) {
    JOptionPane.showMessageDialog(null, "OS X told us to open " + event.getFilename());
  }

  def handlePreferences(event: ApplicationEvent) {

  }

  def handlePrintFile(event: ApplicationEvent) {}

  def handleQuit(event: ApplicationEvent) {
    System.exit(0)
  }

  def handleReopenApplication(event: ApplicationEvent) {}
}