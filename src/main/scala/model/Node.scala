package model

/**
 * author mikwie
 *
 */
case class Node(val name: String, val nodeType: Node.Type, val children: Seq[Node] = Nil)

object Node {
  sealed trait Type

  object Type {
    case object Keyspace extends Type
    case object ColumnFamily extends Type
  }
}