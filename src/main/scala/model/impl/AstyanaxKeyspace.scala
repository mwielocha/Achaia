package model.impl

import model.{ColumnFamilyProxy, ClusterProxy, KeyspaceProxy}
import com.netflix.astyanax.ddl.KeyspaceDefinition
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: mwielocha
 * Date: 27.07.2013
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
class AstyanaxKeyspace(val parent: ClusterProxy,  definition: KeyspaceDefinition) extends KeyspaceProxy {

  def name: String = definition.getName

  override def toString: String = name

  def columnFamilies: Seq[ColumnFamilyProxy] = {
    definition.getColumnFamilyList.map(definition => {
      new AstyanaxColumnFamily(this, definition)
    })
  }
}
