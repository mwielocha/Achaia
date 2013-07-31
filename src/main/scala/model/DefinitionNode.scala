package model

/**
 * author mikwie
 *
 */
case class DefinitionNode(val name: String, val nodeType: DefinitionNode.Type, val children: Seq[DefinitionNode] = Nil)

object DefinitionNode {
  sealed trait Type

  object Type {
    case object Keyspace extends Type
    case object ColumnFamily extends Type
  }
}