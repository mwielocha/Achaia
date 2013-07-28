package model

import _root_.util.{NamedTreeNode, SequentialTreeNode}
import javax.swing.tree.TreeNode
import java.util
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 18:08
 * To change this template use File | Settings | File Templates.
 */
trait ColumnFamilyProxy extends SequentialTreeNode {

  def name: String

  def parent: KeyspaceProxy

  val nodeSequence: Seq[NamedTreeNode] = Nil

}
