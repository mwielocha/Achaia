package view

import _root_.model.ClusterProxy
import swing._
import model._
import javax.swing.JDesktopPane

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
class ClusterView(val cluster: ClusterProxy) extends MainFrame {

  title = cluster.name
  contents = new BorderPanel {
    add(new SplitPane(Orientation.Vertical) {
      dividerSize = 2
      leftComponent = new KeyspacesView(cluster)
      rightComponent = Component.wrap(new JDesktopPane)
    }, BorderPanel.Position.Center)
  }
}
