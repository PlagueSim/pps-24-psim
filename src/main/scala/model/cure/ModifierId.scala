package model.cure

/** Represents the source of a cure modifier. */
sealed trait ModifierSource
object ModifierSource:
  /** Modifier originating from a specific node in the world */
  case class Node(nodeId: NodeId)             extends ModifierSource

  /** Modifier originating from a specific mutation */
  case class Mutation(mutationId: MutationId) extends ModifierSource

  /** Modifier that is applied globally, not tied to any specific node or mutation */
  case object Global                          extends ModifierSource

/** Represents the kind of the modifier effect. */
sealed trait ModifierKind
object ModifierKind:
  /** Multiplies the base cure speed */
  case object Multiplier       extends ModifierKind

  /** Adds a fixed value to the base cure speed */
  case object Additive         extends ModifierKind

  /** Directly adjust the progress of the cure */
  case object ProgressModifier extends ModifierKind

  /** Generic modifier that does not fit into the other categories */
  case object Generic          extends ModifierKind

opaque type NodeId = String
object NodeId:
  /** Creates a NodeId from a string
   *
   * @param name Unique identifier for the node
   */
  def apply(name: String): NodeId = name

  /** Extracts the string identifier from a NodeId */
  extension (id: NodeId) def unwrap: String = id

/** Identifier for a disease mutation
 *
 * @param name Unique identifier of the mutation
 */
opaque type MutationId = String
object MutationId:
  /** Creates a MutationId from a string
   * @param name Unique identifier for the mutation
   */
  def apply(name: String): MutationId = name

  /** Extracts the string identifier from a MutationId */
  extension (id: MutationId) def unwrap: String = id

/** Unique identifier for a cure modifier
 *
 * @param source Origin of the modifier (node, mutation, or global)
 * @param kind   Type of modifier effect
 */
final case class ModifierId(
    source: ModifierSource,
    kind: ModifierKind
):
  override def equals(obj: Any): Boolean = obj match
    case that: ModifierId =>
      this.source == that.source && this.kind == that.kind
    case _ => false

  override def hashCode(): Int = (source, kind).##
