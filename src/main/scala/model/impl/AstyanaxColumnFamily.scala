package model.impl

import model.{KeyspaceProxy, ColumnFamilyProxy}
import javax.swing.tree.TreeNode
import java.util
import com.netflix.astyanax.ddl.{ColumnFamilyDefinition, ColumnDefinition}
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
class AstyanaxColumnFamily(val parent: KeyspaceProxy, val definition: ColumnFamilyDefinition) extends ColumnFamilyProxy {

  def name: String = definition.getName

  override def toString: String = name
}


