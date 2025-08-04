# Lorenzo Tosi

Nel progetto mi sono occupato dello sviluppo del **motore di simulazione** e dello **stato della simulazione**, degli 
**eventi** partendo dalla definizione dello stesso fino all'implementazione
dell'evento di infezione, di morte e di avanzamento della giornata, tutti con la propria logica compresa,
ho sviluppato la parte relativa al calendario e un semplice scheduler.
Inoltre, mi sono occupato dell'integrazione tra i vari componenti del progetto, sviluppando la logica necessaria per il
**collegamento tra il model e la view**, così da permettere la visualizzazione dell'evoluzione della simulazione. Ho
implementato il sistema per l'esecuzione del progetto attraverso **DSL**, curando il flusso di avvio, l'inizializzazione
dei moduli principali e l'esecuzione automatica del programma.
Per quanto riguarda la view, ho progettato e implementato un’**interfaccia di comunicazione tra model e view**, pensata
per essere facilmente utilizzabile anche dai miei compagni: permette a tutti i componenti della view di aggiornarsi
automaticamente ogni volta che una parte del model viene modificata, garantendo così una sincronizzazione continua tra
lo stato interno della simulazione e la sua rappresentazione grafica.
Mi sono infine occupato di implementare un piccolo engine prolog ed ho gestito le **GitHub Actions** creando workflow
per l’esecuzione automatica di build e test a ogni pull request.

I file completamente sviluppati da me sono: `SimulationEngine`, `SimulationState`, `Event`, `InfectionEvent`,
`AdvanceDayEvent`, `DeathTypes`, `InfectionAndDeathPopulation`, `InfectionTypes`, `PopulationEffect`,
`PopulationEffectComposer`, `Probability`, `TemperatureAdjuster`, `Scheduler`, `BasicYear`, `Time`, `TimeTypes`,
`UpdatableView`, `Controller`, `ExecutionMode`, `DSL`, `SetupBuilderAndRunner`, `CureBuilder`, `CureBuilderProxy`,
`CureDSL`, `DiseaseBuilder`, `DiseaseBuilderProxy`, `DiseaseDSL`, `SimulationStateBuilder`,
`SimulationStateBuilderProxy`, `SimStateDSL`, `WorldBuilder`, `WorldBuilderProxy`, `WorldDSL`, `PrologEngine`.

Le parti più importanti sono:

- SimulationEngine
- Event
- PopulationEffect
- DSL

## SimulationEngine

Ho progettato e implementato un motore di simulazione funzionale, basato sulla State monad di Cats. Questo approccio
consente di modellare l’evoluzione dello stato della simulazione in modo immutabile.

Per rappresentare le operazioni che modificano lo stato della simulazione, ho introdotto un type alias:
```scala
type Simulation[A] = State[SimulationState, A]
```

Questo ha reso possibile la modellazione di ogni evento della simulazione come una computazione pura che trasforma uno
SimulationState in un nuovo stato, restituendo anche un eventuale valore di tipo `A`.

Il metodo `runStandardSimulation` rappresenta un singolo step completo della simulazione. E' definito come una
composizione sequenziale di trasformazioni pure sullo stato, sfruttando la monade `State` della libreria `Cats`.
All'interno viene definita una composizione monadica che rappresenta una esecuzione completa della simulazione, definita
tramite `for-comprehension`. Per ogni evento di trasformazione dello stato, viene chiamata la
`executeEvent(event: Event[A])` in modo tale da delegare l'esecuzione dell'evento ad un punto centrale, lasciando agli
eventi stessi l'unico compito di definire cosa deve essere svolto.

## Event

Il trait `Event[A]` definisce l’astrazione fondamentale per modellare le trasformazioni di stato all’interno del
`SimulationEngine`. Ogni evento rappresenta un’operazione atomica sul `SimulationState` e produce un valore di tipo
generico `A`, che può essere utilizzato come risultato intermedio o ignorato.

`Event[A]` presenta 2 metodi: `execute()`, che fornisce una definizione standard per l’applicazione dell’effetto, ed
`modifyFunction(state: SimulationState): A` che invece incapsula la logica specifica dell'evento che dovrà invece essere
definita.

```scala
def execute(): Simulation[A] =
  for
    s <- State.get[SimulationState]
    newFieldValue = modifyFunction(s)
    updatedState = s.replace(newFieldValue)
    _ <- State.set(updatedState)
  yield newFieldValue

def modifyFunction(state: SimulationState): A
```

`execute()` sfrutta la monade `State` di Cats per accedere allo stato attuale, applicare la `modifyFunction` sullo
stato, generare un risultato che andrà poi ad essere sostituito a quello presente nel `SimulationState`, aggiornare lo
`State` della monade e restituire il valore calcolato.

Questo design ed implementazione separa in modo chiaro il *cosa* e il *come*. L’evento descrive **cosa deve essere fatto
** tramite la `modifyFunction`, mentre la logica generica di **come aggiornare lo stato** viene centralizzata nel
`execute()` definito nel trait stesso.

Il design e la implementazione di `Event[A]` consente al `SimulationEngine` di eseguire qualsiasi evento tramite una
semplice chiamata a `executeEvent`, senza conoscere i dettagli della sua implementazione. Inoltre, il fatto che ogni
evento sia un oggetto puro e indipendente rende il sistema altamente estendibile: per introdurre nuovi comportamenti, è
sufficiente creare una nuova istanza di `Event[A]` e implementare la trasformazione desiderata.

## PopulationEffect

```scala
trait PopulationEffect:
  def applyToPopulation(node: Node, disease: Disease): Node
```

`PopulationEffect` è stato progettato come **astrazione generica** per modellare tutte quelle trasformazioni che
modificano lo stato demografico di un nodo (`Node`) in funzione delle caratteristiche di una malattia (`Disease`). In
particolare, **unifica la logica comune** tra gli effetti di **infezione** e **morte**, due logiche concettualmente
simili ma differenziate solo dal tipo di popolazione coinvolta (*i sani* per l’infezione, *gli infetti* per la morte).

E' stato sviluppato seguendo questa logica: determinare se l’effetto è applicabile, calcolare una probabilità a partire
da un parametro della malattia, selezionare un sottoinsieme della popolazione, calcolare un cambiamento atteso, e infine
applicarlo. L'implementazione basata su una sequenza di funzioni parametriche ha permesso di isolare la logica comune
all'interno di una struttura unica, completamente configurabile attraverso le funzioni.

Questo design ha permesso di evitare duplicazione di codice ed ha reso il sistema più modulare, riusabile e facilmente
estendibile. L’uso di un’interfaccia unica e di un costruttore compositivo (`PopulationEffectComposer`) consente di
definire nuovi comportamenti semplicemente definendo funzioni, senza introdurre nuove classi o modificare la logica
esistente.

La struttura interna prevede una classe privata `FunctionalPopulationEffect[A]`, che implementa l’interfaccia
`PopulationEffect`.

```scala
private case class FunctionalPopulationEffect[A](
                                                  canApply: (Node, Disease) => Boolean,
                                                  extractParameter: Disease => Double,
                                                  populationSelector: Node => A,
                                                  adjustParameter: Double => Probability,
                                                  calculateChange: (A, Probability) => Int,
                                                  applyChange: (Node, Int) => Node
                                                ) extends PopulationEffect
  ```

Il parametro di tipo generico `A` permette di avere completa libertà nello scegliere tutte le informazioni relative alla
popolazione. Si può selezionare una caratteristica, oppure piu di una, in modo tale da poterle usare durante
`calculateChange`.

Le sei funzioni da definire sono:

* `canApply: (Node, Disease) => Boolean` decide se l’effetto deve essere applicato al nodo dato il contesto della
  malattia
* `extractParameter: Disease => Double` estrae un parametro numerico dalla malattia (es. tasso di infezione o mortalità)
* `populationSelector: Node => A` seleziona dal nodo i dati necessari per la trasformazione, parametrizzati come tipo
  `A`
* `adjustParameter: Double => Probability` trasforma il parametro numerico in una probabilità (es. da percentuale a
  valore normalizzato)
* `calculateChange: (A, Probability) => Int` calcola quanti individui devono essere modificati, a partire dalla porzione
  selezionata della popolazione e dalla probabilità calcolata
* `applyChange: (Node, Int) => Node` applica concretamente la modifica al nodo, restituendone una nuova versione
  aggiornata

L’implementazione del metodo `applyToPopulation` esegue la trasformazione in modo **lazy e puro**.

```scala
override def applyToPopulation(node: Node, disease: Disease): Node =
  if canApply(node, disease) then
  lazy val rawParam = extractParameter(disease)
  lazy val probability = adjustParameter(rawParam)
  lazy val basePopulation = populationSelector(node)
  val change = calculateChange(basePopulation, probability)
  applyChange(node, change)
else node
```

Solo se la condizione `canApply` è soddisfatta, allora verranno calcolati i valori intermedi necessari (`rawParam`,
`probability`, `basePopulation`) e si determinerà la quantità di cambiamento e si applica al nodo. Altrimenti, il nodo
viene restituito immutato.

Infine, il metodo pubblico `apply[A](...)` dell’oggetto `PopulationEffectComposer` consente di creare un
`PopulationEffect` parametrizzando direttamente le sei funzioni che descrivono il comportamento desiderato. Questo
approccio elimina la necessità di definire numerose sottoclassi concrete favorendo uno stile **dichiarativo e
configurabile**.

Di seguito è mostrato un esempio di implementazione di un effetto di infezione e di morte:
```scala
  val StandardInfection: PopulationEffect =
  PopulationEffectComposer.apply(
    canApply = STANDARD_CAN_APPLY,
    parameterExtractor = _.infectivity,
    populationSelector = node => node.population - node.infected,
    changeCalculator = (healthy, prob) => (healthy * prob.value).toInt,
    changeApplier = (node, infected) => node.increaseInfection(infected)
  )

  val StandardDeath: PopulationEffect =
    PopulationEffectComposer.apply(
      canApply = STANDARD_CAN_APPLY,
      parameterExtractor = _.lethality,
      populationSelector = _.infected,
      changeCalculator = (infected, prob) => (infected * prob.value).toInt,
      changeApplier = (node, deaths) => node.updateDied(deaths)
    )
```
Le uniche differenze tra le due implementazioni sono il parametro passato a `parameterExtractor`, il 
`populationSelector` e il metodo del nodo chiamato nella `changeApplier`.
## DSL

Mi sono occupato di implementare un DSL per poter definire il setup della simulazione in modo semplice e veloce. Le
keywords presenti sono:
- `setup`
  - `simulationState`
    - `world`
      - `worldNodes`
      - `worldEdges`
      - `worldMovenents`
    - `disease`
      - `diseaseName`
      - `diseaseTraits`
      - `diseaseCure`
    - `cure`
      - `cureProgress`
      - `cureBaseSpeed`
      - `cureModifiers`
    - `time`
    - `infectionLogic`
    - `deathLogic`
    - `reactions`
  - `conditions`
  - `scheduler`
  - `binding`
  - `runMode`

Se non definite, il setup viene eseguito partendo da parametri standard preimpostati.

L'intero modello è stato scritto in ottica immutabile, dove ogni modifica restituisce una copia aggiornata tramite
`copy(…)`.
Per i builder piu complessi, come il `SimulationStateBuilder`, è stato necessario introdurre dei proxy.
```scala
class SimulationStateBuilderProxy(
    get: () => SimulationStateBuilder,
    set: SimulationStateBuilder => Unit
) extends SimulationStateBuilder:
  override def withWorld(world: World): SimulationStateBuilder =
    val updated = get().withWorld(world)
    set(updated)
    updated
```
Questo pattern ha consentito di modificare lo stato in modo immutabile e controllato e mantenendo la type-safety
e la purezza dei builder. Questa filosofia è stata adottata anche nel `WorldBuilder`, `CureBuilder` e `DiseaseBuilder`.

[Back to index](../../index.md) |
[Back to implementation](../../5-implementation/impl.md)