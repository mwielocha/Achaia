package view

import _root_.model.ClusterProxy
import scala.swing._
import javax.swing.JTree
import java.awt.BorderLayout

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
class KeyspacesView(cluster: ClusterProxy) extends BorderPanel {

     add(new ScrollPane {
        contents = Component.wrap(new JTree(cluster))
     }, BorderPanel.Position.Center)
}
