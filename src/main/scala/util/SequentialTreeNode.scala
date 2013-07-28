package util

import javax.swing.tree.TreeNode
import java.util
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */

trait NamedTreeNode extends TreeNode {

  def name: String

  override def toString: String = name
}

trait SequentialTreeNode extends NamedTreeNode {

  def nodeSequence: Seq[NamedTreeNode]

  def parent: TreeNode

  def getChildAt(index: Int): TreeNode = nodeSequence(index)

  def getChildCount: Int = nodeSequence.size

  def getParent: TreeNode = parent

  def getIndex(node: TreeNode): Int = nodeSequence.indexOf(node)

  def getAllowsChildren: Boolean = !nodeSequence.isEmpty

  def isLeaf: Boolean = nodeSequence.isEmpty

  def children(): util.Enumeration[_] = nodeSequence.toIterator
}
