package model

import _root_.util.{NamedTreeNode, SequentialTreeNode}
import javax.swing.tree.TreeNode
import java.util
import com.google.common.collect.Lists
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
trait KeyspaceProxy extends SequentialTreeNode {

  val parent: ClusterProxy

  def name: String

  def columnFamilies: Seq[ColumnFamilyProxy]

  val nodeSequence: Seq[NamedTreeNode] = columnFamilies
}
