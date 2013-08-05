package components

import _root_.util.Logging
import scala.swing.{Publisher, Component}
import org.jdesktop.swingx.JXTreeTable
import org.jdesktop.swingx.treetable.{DefaultTreeTableModel, TreeTableNode, TreeTableModel}
import javax.swing.tree.{DefaultTreeCellRenderer, TreeCellRenderer, TreeNode, TreePath}
import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener, TreeModelListener}
import java.{awt, util}
import scala.collection.JavaConversions._
import scala.swing.event.Event
import scala.swing.Table.AutoResizeMode
import org.jdesktop.swingx.decorator.Highlighter
import tools.TableColumnAdjuster
import scalaswingcontrib.tree.{TreeModel, Tree}
import javax.swing.{Icon, JTree}

/**
 * author mikwie
 *
 */
class XTreeTable(var rootNode: TreeTableNode) extends Component {

  override lazy val peer = new JXTreeTable()

  val adjuster = new TableColumnAdjuster(peer)

  object selection extends Publisher {

    peer.addTreeSelectionListener(new TreeSelectionListener {
      def valueChanged(event: TreeSelectionEvent) {
        publish(TreeSelectionChanged(
          event.getPath,
          event.getPaths,
          event.getNewLeadSelectionPath,
          event.getOldLeadSelectionPath
        ))
      }
    })
  }

  def renderer = peer.getTreeCellRenderer

  def renderer_=(renderer: TreeCellRenderer) = {
    peer.setTreeCellRenderer(renderer)
  }

  def adjustColumn(index: Int) = adjuster.adjustColumn(index)

  def rowHeight = peer.getRowHeight

  def rowHeight_=(value: Int) = peer.setRowHeight(value)

  def autoResizeMode = peer.getAutoResizeMode

  def autoResizeMode_=(mode: AutoResizeMode.Value) = {
    peer.setAutoResizeMode(mode.id)
  }

  def highlighters = peer.getHighlighters

  def highlighters_=(highlighters: Highlighter*) = peer.setHighlighters(highlighters: _*)

  def model = peer.getTreeTableModel

  def model_=(model: TreeTableModel) = peer.setTreeTableModel(model)

  def root = rootNode

  def root_=(root: TreeTableNode) = {
    rootNode = root
    peer.setTreeTableModel(new DefaultTreeTableModel(root))
  }
}

class SimpleCellRenderer[A](val render: A => (String, Icon)) extends DefaultTreeCellRenderer {
  override def getTreeCellRendererComponent(p1: JTree, p2: Any, p3: Boolean,
                                            p4: Boolean, p5: Boolean, p6: Int,
                                            p7: Boolean): awt.Component =  {

    val comp = super.getTreeCellRendererComponent(p1, p2, p3, p4, p5, p6, p7)
    val value = p2.asInstanceOf[A]
    val rendered = render(value)
    setIcon(rendered._2)

    comp
  }
}

case class TreeSelectionChanged(val path: TreePath,
                                val paths: Seq[TreePath],
                                val newLeadSelectionPath: TreePath,
                                val oldLeadSelectionPath: TreePath) extends Event

trait SimpleTreeTableNode[T] extends TreeTableNode {

  def childrenStream: Stream[_ <: TreeTableNode]

  def valueAt(index: Int): T

  def columnCount: Int

  def parent: Option[_ <: SimpleTreeTableNode[T]]

  /* =-=-=-=-=-=-=-=-=-= */

  def children(): util.Enumeration[_ <: TreeTableNode] = childrenStream.iterator

  def getValueAt(column: Int): AnyRef = valueAt(column).asInstanceOf[AnyRef]

  def getChildAt(childIndex: Int): TreeTableNode = childrenStream(childIndex)

  def getColumnCount: Int = columnCount

  def getParent: TreeTableNode = parent.getOrElse(null.asInstanceOf[TreeTableNode])

  def isEditable(column: Int): Boolean = false

  def setValueAt(aValue: Any, column: Int) {}

  def getUserObject: AnyRef = null

  def setUserObject(userObject: Any) {}

  def getChildCount: Int = childrenStream.size

  def getIndex(node: TreeNode): Int = childrenStream.indexOf(node)

  def getAllowsChildren: Boolean = !childrenStream.isEmpty

  def isLeaf: Boolean = childrenStream.isEmpty
}
