package model.cure

sealed trait ModifierSource
object ModifierSource:
  case class Node(nodeId: NodeId)             extends ModifierSource
  case class Mutation(mutationId: MutationId) extends ModifierSource
  case object Global                          extends ModifierSource

sealed trait ModifierKind
object ModifierKind:
  case object Threshold extends ModifierKind

final case class NodeId(name: String)
final case class MutationId(name: String)

final case class ModifierId(
    source: ModifierSource,
    kind: ModifierKind
)
