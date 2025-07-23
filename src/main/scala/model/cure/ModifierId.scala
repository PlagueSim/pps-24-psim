package model.cure

sealed trait ModifierSource
object ModifierSource:
  case class Node(nodeId: NodeId)             extends ModifierSource
  case class Mutation(mutationId: MutationId) extends ModifierSource
  case object Global                          extends ModifierSource

sealed trait ModifierKind
object ModifierKind:
  case object Multiplier       extends ModifierKind
  case object Additive         extends ModifierKind
  case object ProgressModifier extends ModifierKind
  case object Generic          extends ModifierKind

final case class NodeId(name: String)
final case class MutationId(name: String)

final case class ModifierId(
    source: ModifierSource,
    kind: ModifierKind
):
  override def equals(obj: Any): Boolean = obj match
    case that: ModifierId =>
      this.source == that.source && this.kind == that.kind
    case _ => false

  override def hashCode(): Int = (source, kind).##