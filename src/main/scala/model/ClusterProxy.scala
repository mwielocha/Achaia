package model

import _root_.util.{NamedTreeNode, SequentialTreeNode}
import javax.swing.tree.{TreeNode, DefaultTreeModel, TreeModel}
import java.util
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
trait ClusterProxy extends SequentialTreeNode {

  def name: String

  def keyspaces: Seq[KeyspaceProxy]

  override lazy val nodeSequence: Seq[NamedTreeNode] = keyspaces

}
