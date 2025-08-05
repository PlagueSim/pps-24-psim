Durante lo sviluppo del progetto, mi sono dedicato principalmente all'implementazione del sistema di cura e delle reazioni globali, due componenti fondamentali per la simulazione epidemiologica. Ho adottato un approccio funzionale puro sfruttando i principi avanzati di Scala, con particolare attenzione all'immutabilità, alla composizione e alla type safety.

Mi sono uccupato dei seguenti file:
`Cure`, `CureModifier`, `ModifierId`, `CureEvents`, `GlobalCureResearchEvent`, `ActiveReaction`, `ReactionAction`, `ReactionCondition`, 
`ReactionRule`, `Reactions`, `ApplyReactionEvent`, `RevertExpiredEvent`, `UpdateActiveReactionsEvent`, `CureProgressBar`

Ho partecipato anche allo sviluppo di:
`GlobalLogic`, `DnaPointsCalculator`, `InfectionTypes`, `DeathTypes`

## Sistema di ricerca della cura
Il sistema di cura rappresenta lo stato della ricerca scientifica per contrastare il diffondersi della malattia. È progettato come un ADT (Abstract Data Type) immutabile con operazioni pure.

### Architettura immutabile
Ho implementato la Cura come una strutta immutabile che tiene traccia di:
- Progresso della ricerca corrente
- Velocità base di ricerca
- Collezione di modificatori attivi

```scala
final case class Cure private (
    progress: Double = 0.0,
    baseSpeed: Double = 0.0,
    modifiers: CureModifiers = CureModifiers.empty
):
  /** Calcola la velocità effettiva applicando tutti i modificatori persistenti */
  def effectiveSpeed: Double = ...
  
  /** Avanza la ricerca di un ciclo */
  def advance(): Cure = ...
```

### Modificatori della cura
I modificatori rappresentano effetti temporanei o permanenti sulla ricerca. Ogni modificatore ha un identificatore unico che garantisce anche tracciabilità degli effetti applicati.

#### ModifierId
Ho implementato `ModifierId` come una struttura chiave che combina due dimensioni:
- **Source**: Origine dell'effetto
    - **Node**: Modificatore proveniente/causato da un nodo specifico
    - **Mutation**: Modificatore causato da una mutazione del virus (Solitamente rallentano la ricerca)
    - **Global**: Modificatore non legato a entità specifiche
- **Kind**: Tipo di modificatore
    - **Multiplier**: Modificatore moltiplicativo
    - **Additive**: Modificatore additivo
    - **Progress**: Modificatore che influisce direttamente sul progresso della cura

```scala
final case class ModifierId(
    source: ModifierSource,
    kind: ModifierKind
)
```

##### Scelte implementative e design

**Opaque Types per type safety**: Ho utilizzato opaque types per `NodeId` e `MutationId` per garantire type safety a compile time, impedendo la confusione tra identificatori di tipo diverso pur mantenendo l'efficienza di String a runtime.

```scala
opaque type NodeId = String
opaque type MutationId = String
```

**Override di equals e hashCode**: Ho ridefinito esplicitamente questi metodi per garantire che l'uguaglianza sia basata sui valori semantici e per ottimizzare l'uso in collezioni come `Map` e `Set`.

#### Modificatori persistenti
Questi modificatori vengono applicati ad ogni ciclo di simulazione e influenzano la velocità di ricerca. Possono essere Additivi o Moltiplicativi.

#### Modificatori One-Time
Questi modificatori hanno un effetto immediato e non persistono oltre il ciclo corrente. Vengono applicati al momento dell'aggiunta e servono per modificare il progresso attuale della cura.

```scala
sealed trait CureModifier:
  def id: ModifierId

sealed trait PersistentModifier extends CureModifier:
  def apply(baseSpeed: Double): Double

sealed trait OneTimeModifier extends CureModifier:
  def apply(progress: Double): Double
```

#### Esempi di modificatori
```scala
private[CureModifier] case class Multiplier(id: ModifierId, factor: Double)
    extends PersistentModifier:
  require(factor >= 0.0, "Factor must be non-negative")
  def apply(baseSpeed: Double): Double = baseSpeed * factor

private[CureModifier] case class Additive(id: ModifierId, amount: Double)
    extends PersistentModifier:
  def apply(baseSpeed: Double): Double = clampToUnitInterval(
    baseSpeed + amount
  )

private[CureModifier] case class ProgressModifier(
    id: ModifierId,
    amount: Double
) extends OneTimeModifier:
  require(amount >= -1.0 && amount <= 1.0, "Amount must be in [-1.0, 1.0]")
  def apply(progress: Double): Double = clampToUnitInterval(progress + amount)
```
Per garantire la creazione sicura dei modificatori, ho implementato factory methods che validano gli input e garantiscono che i modificatori siano sempre validi.

```scala
def multiplier(id: ModifierId, factor: Double): Option[Multiplier] =
  if factor >= 0.0 then Some(Multiplier(id, factor)) else None

def additive(id: ModifierId, amount: Double): Option[Additive] =
  if amount >= -1.0 && amount <= 1.0 then Some(Additive(id, amount)) else None

def progressModifier(
    id: ModifierId,
    amount: Double
): Option[ProgressModifier] =
  if amount >= -1.0 && amount <= 1.0 then Some(ProgressModifier(id, amount))
  else None
```

### CureModifiers

Il componente `CureModifiers` rappresenta il sistema centrale per la gestione degli effetti attivi sulla cura. Ho progettato questa struttura come una collezione immutabile type-safe che garantisce manipolazione sicura dei modificatori.

```scala
final case class CureModifiers(
    modifiers: Map[ModifierId, CureModifier] = Map.empty
):
  def factors: Iterable[Double => Double] = 
    modifiers.values.collect:
      case mod: PersistentModifier => mod.apply
```

#### Caratteristiche principali:

**Gestione attraverso Map**: Ho utilizzato una `Map[ModifierId, CureModifier]` per garantire unicità degli identificatori e accesso efficiente O(1) ai modificatori. Questo design previene duplicazioni accidentali e facilita operazioni di rimozione selettiva.

**Pattern Matching per applicazione selettiva**: Il metodo `factors` utilizza `collect` con pattern matching per estrarre solo i modificatori persistenti, applicando il principio di separazione delle responsabilità tra modificatori one-time e persistenti.

**Operazioni immutabili**: Tutte le operazioni di modifica (`add`, `removeById`, `removeBySource`) restituiscono nuove istanze, mantenendo l'immutabilità.

**Builder Pattern per costruzione incrementale**: Ho implementato un `CureModifiersBuilder` che permette costruzione incrementale type-safe con validazione automatica:

```scala
CureModifiers.builder
  .addMultiplier(ModifierId(Global, Multiplier), 1.5)
  .addAdditive(ModifierId(Node("research_lab"), Additive), 0.1)
  .build
```

**Metodi di rimozione specializzati**: Ho fornito tre strategie di rimozione per diversi casi d'uso:
- `removeById`: Rimozione puntuale per modificatori specifici
- `removeBySource`: Rimozione in batch per source (utile quando un nodo viene distrutto)
- `removeIfId`: Rimozione condizionale con predicato personalizzato

**Incapsulamento attraverso visibilità**: I metodi di manipolazione sono marcati `private[cure]` per garantire che le modifiche avvengano solo attraverso l'API pubblica di `Cure`, mantenendo l'invariante di consistenza.

### Gestione dei modificatori nella Cure

**Aggiunta di modificatori**: Il metodo `addModifier` distingue tra modificatori one-time (applicati immediatamente al progresso) e persistenti (aggiunti alla collezione), prevenendo duplicati attraverso controllo dell'ID:

```scala
def addModifier(mod: CureModifier): Cure =
  mod match
    case oneTime: OneTimeModifier if !modifiers.modifiers.contains(oneTime.id) =>
      Cure(oneTime.apply(progress).min(1.0).max(0.0), baseSpeed, modifiers.add(oneTime))
    case persistent: PersistentModifier =>
      Cure(progress, baseSpeed, modifiers.add(persistent))
    case _ => this // Duplicati ignorati
```

### Evento di ricerca della cura


## Sistema di Reazioni Globali
Ho progettato il sistema di reazioni globali per modellare le contromisure che i paesi attuano in risposta alla diffusione della malattia. L'architettura si basa su quattro componenti principali:
- ReactionCondition
- ReactionAction
- ReactionRule
- ActiveReaction

### ReactionCondition
Le condizioni determinano quando una reazione deve attivarsi. Ho implementato un sistema flessibile che supporta operatori logici per la composizione di condizioni complesse.

```scala
trait ReactionCondition:
  def isSatisfied(state: SimulationState, nodeId: String): Boolean

object ReactionCondition:
  implicit class ConditionOps(condition: ReactionCondition):
    def and(other: ReactionCondition): ReactionCondition
    def or(other: ReactionCondition): ReactionCondition
    def unary_! : ReactionCondition
	
// Esempio di condizione
case class InfectedCondition(threshold: Double) extends ReactionCondition:
  def isSatisfied(state: SimulationState, nodeId: String): Boolean =
    state.world.nodes
      .get(nodeId)
      .exists(node =>
        node.population > 0 && node.infected.toDouble / node.population >= threshold
      )
```

### ReactionAction
Le azioni definiscono cosa accade quando una reazione viene attivata.
```scala
trait ReactionAction:
  def apply: World => World
  def reverse: World => World

// Esempio: Chiusura confini
case class CloseEdges(edgeType: EdgeType, nodeId: String) 
  extends ReactionAction:

  def apply: World => World = 
    world => updateEdges(world, _.close)
  
  def reverse: World => World = 
    world => updateEdges(world, _.open)
```
- Azioni reversibili (per gestire la scadenza delle reazioni)
- Composizione tramite CompositeAction per azioni complesse

### Reactions
la classe `Reactions` funge da contenitore centrale per gestire lo stato delle reazioni:
```scala
final case class Reactions(
    rules: List[ReactionRule] = List.empty,
    activeReactions: Set[ActiveReaction] = Set.empty
):
  // Aggiunta nuove reazioni attive
  def addActive(newReactions: Set[ActiveReaction]): Reactions
  
  // Rimozione reazioni scadute
  def removeExpired(currentDay: Time): Reactions
```
#### ReactionRule
Le regole collegano condizioni ad azioni:
```scala
final case class ReactionRule(
    condition: ReactionCondition,
    actionFactory: String => ReactionAction,
    duration: Option[Int] = None
):
  // Verifica se la regola deve attivarsi per un nodo
  def shouldTrigger(state: SimulationState, nodeId: String): Boolean = 
    condition.isSatisfied(state, nodeId)
```
### ActiveReaction
Rappresentano un'istanza attiva di un regola:
```scala
final case class ActiveReaction(
    rule: ReactionRule,
    nodeId: String,
    startDay: Time
):
  // Verifica se la reazione è ancora attiva
  def isActive(currentDay: Time): Boolean = 
    rule.duration match
      case Some(dur) => currentDay < startDay + dur
      case None => true
```

## Ulteriori participazioni
### InfectionType
Ho sfruttato l'architettura messa a disposizione dal mio collega Lorenzo Tosi per implementare il modello epidemiologico 
SIR (Susceptible-Infected-Recovered) permettendo di simulare la diffusione della malattia in modo realistico e 
garantendo scalabilità anche con popolazioni molto grandi o molto piccole.
$$
\Delta I = \max\left(1,\; \beta \cdot \frac{I \cdot (N - I)}{N} \right)
$$

dove:

- \( $\Delta I$ \): nuovi infetti nel tick corrente
- \( $\beta$ \): valore di infettività (probabilità di trasmissione)
- \( $I$ \): numero attuale di infetti
- \( $N$ \): popolazione totale
- \( $N - I$ \): persone sane

Se \( $\beta \leq 0$ \), allora \( $\Delta I = 0$ \)


[Back to index](../../index.md) |
[Back to implementation](../../5-implementation/impl.md)