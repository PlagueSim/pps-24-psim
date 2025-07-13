package model.cure

final case class ModifierId(node: NodeId, kind: ModifierKind)

final case class NodeId(name: String)

sealed trait ModifierKind

object ModifierKind:
  case object Threshold extends ModifierKind
