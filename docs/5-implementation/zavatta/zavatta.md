# Andrea Zavatta

Nel progetto mi sono occupato della costruzione del modello del `World` e tutte le componenti interne:
`MovementStrategy`, `Edge` e `Node`, puntando su immutabilità, funzioni pure e testabilità.
Per quanto riguarda la parte grafica mi sono occupato di tutta la parte di rendering del `World`, che comprende il rendering dei `Node`
e del colore di questi ultimi in base agli infetti e ai morti, il rendering degli `Edge` e del colore in base
alla tipologia.

Ho curato l'integrazione tra modello e interfaccia, assicurandomi che ogni aggiornamento dello stato di `World` si
riflettesse con precisione e fluidità sul rendering della view.
Per ottimizzare il sistema di rendering, ho limitato il ridisegno dell'interfaccia ai soli elementi modificati, evitando ridisegni completi e migliorando le performance.


In aggiunta a tutto quanto già descritto, ho progettato e implementato l’intero sistema di movimento delle persone all’interno del world.

i file di cui mi sono occupato sono:
`Edge`, `EdgeExtensions`, `MovementComputation`, `MovementStrategy`, `Node`, `Types`, `World`, `WorldFactory`, `WorldValidator`, `WorldConnectivity`,
`EdgeConfigurationFactory`, `EdgeMovementConfig`, `GlobalLogic`, `LocalPercentageLogic`, `MovementEvent`, `ChangeNodesInWorldEvent`, `MovementLogic`, `MovementLogicWithEdgeCapacityAndPercentage`,
`MovementStrategyDispatcher`, `MovementStrategyLogic`, `StaticLogic`, `CircularLayout`, `DefaultNodeViewFactory`, `EdgeLayer`, `EdgeUpdater`, `GraphLayout`, `LivePosition`,
`NodeLayer`, `NodeView`, `NodeViewFactory`, `UpdatableWorldView`, `WorldRenderer`, `WorldView`, `ConsoleSimulationView`

Le parti più importanti del mio lavoro sono:
- `World`
- `MovementStrategy`, `MovementLogic`

## World
Ho definito le entità fondamentali:
- `Node`: con builder e validazioni (popolazione, infetti, morti)
- `Edge`: con ordinamento lessicografico (per consentire l'uguaglianza di edge unidirezionali) e tipologie (`Air`, `Land`, `Sea`)
- `MovementStrategy`: un trait per definire le strategie di movimento.
  `Edge` e `Node` sono corredate di extension methods (infectedPercentage, increasePopulation, edgeId, getMapEdges, ecc.) per semplificare ogni operazione nel Mondo.

Il `World` è l'insieme di queste tre Entità:

```scala
case class World private (
                           nodes: Map[NodeId, Node],
                           edges: Map[EdgeId, Edge],
                           movements: Map[MovementStrategy, Percentage]
                         ):

```

Ho modellato il `World` come una private case class con costruttore privato e un metodo apply nel companion object per due ragioni principali:
1. **Immutabilità e utilities automatiche**
  - Come case class offre immutabilità, copy, equals e pattern matching “gratis”, semplificando la gestione dello stato e il testing.
2. **Centralizzazione delle validazioni**
  - Il costruttore è privato: non è possibile creare un’istanza invalida bypassando i controlli.
  - Il companion apply(nodes, edges, movements) contentente le validazioni del mondo e la sua creazione tramite costruttore privato.
  - In questo modo ogni nuovo World viene garantito “sano” fin dalla creazione, e la logica di validazione resta centralizzata in un solo punto.

Quindi per garantire un modello coerente ho introdotto `WorldValidator` che si occupa di validare la creazione del `World` nel metodo `apply` del companion object.
Questo approccio consente di mantenere il codice pulito e facilmente testabile, poiché tutte le regole di validazione sono concentrate in un'unica classe.

```scala
object World:
def apply(
           nodes: Map[NodeId, Node],
           edges: Map[EdgeId, Edge],
           movements: Map[MovementStrategy, Percentage]
         ): World =
  WorldValidator.validateEdges(nodes, edges)
WorldValidator.validateMovements(movements)
new World(nodes, edges, movements)
  ```

Gestire nodes, edges e movements nella World usando mappe (con ID come chiavi) offre diversi vantaggi, soprattutto in un'ottica di programmazione funzionale e gestione di stati immutabili:

- **Separazione tra identità e dati**
- **Accesso diretto e performante (lookup in O(1))**

Per quanto riguarda movements i vantaggi di gestirlo come mappa `Strategy` -> `Percentage` sono:
- **Separazione tra modalità e distribuzione**
- **Facilità di validazione e controllo**
- **Estendibilità semplice**

```scala
  extension (edge: Edge)
def edgeId: EdgeId =
  if edge.nodeA < edge.nodeB then s"${edge.nodeA}-${edge.nodeB}-${edge.typology}" else s"${edge.nodeB}-${edge.nodeA}-${edge.typology}"
```

```scala
  extension (edges: Iterable[Edge])
def getMapEdges: Map[EdgeId, Edge] =
  edges.map(edge => edge.edgeId -> edge).toMap
```

Sono stati implementati questi due extension methods per aiutare l'utilizzatore di `World` a creare la mappa di `Edge` passando al costruttore di `World` la Lista di `Edge`
delegando la computazione degli ID e la creazione della mappa a questi metodi.

## Componenti principali del movimento
Mi sono occupato poi del sistema di movimento, progettato per gestire lo spostamento delle persone tra i Node in maniera modulare,
testabile e perfettamente aderente ai principi della programmazione funzionale.
Il sistema è responsabile di determinare, a ogni tick di simulazione, quali individui si spostano,
in che quantità, e verso quali destinazioni, aggiornando immutabilmente lo stato del World.

Il cuore architetturale di questo sottosistema è costituito da tre componenti principali:

1. **MovementStrategy** - Tipologie di comportamento

È un **sealed trait** che rappresenta le diverse strategie di movimento disponibili, ovvero i comportamenti astratti che la popolazione può adottare.
```scala
sealed trait MovementStrategy

case object Static extends MovementStrategy

case object LocalPercentageMovement extends MovementStrategy

case object GlobalLogicMovement extends MovementStrategy
```

Il World conosce esclusivamente queste strategie come intenzioni astratte di comportamento: non ha alcuna visibilità sull’implementazione delle logiche che le realizzano.

```scala
  movements: Map[MovementStrategy, Percentage]
```

Questa rappresenta la dichiarazione delle intenzioni del sistema:
il World sa quali comportamenti usare e in che proporzione, ma non conosce le implementazioni operative di questi comportamenti.




2. **MovementLogic** definisce la logica concreta di ogni strategia di movimento, tramite il metodo `compute`.
   Le classi concrete che lo implementano (`StaticLogic`, `LocalPercentageLogic`, `GlobalLogic`)
   definiscono come una strategia genera effettivamente movimenti tra nodi.

Sono queste classi che:
- analizzano il mondo corrente
- calcolano chi si muove, dove e quanti infetti viaggiano
- restituiscono una lista di `PeopleMovement`

**Importante**: il World non conosce queste logiche. Solo gli eventi e i moduli operativi (es. MovementComputation) ne sono a conoscenza e le invocano quando serve.

Per ogni strategia di movemento viene passato come parametro al metodo compute il generatore casuale Random.
Questo approccio, basato sull’iniezione delle dipendenze, consente di controllare esattamente il comportamento nei test,
ad esempio usando un generatore Random inizializzato con un seed noto (new Random(42)), oppure mockando nextDouble() per ottenere valori deterministici.
In questo modo, ogni MovementLogic può essere testata in maniera riproducibile e priva di effetti collaterali.

```scala
trait MovementLogic:
def compute(
             world: World,
             percent: Percentage,
             rng: scala.util.Random
           ): Iterable[PeopleMovement]
```
Esempio di test in cui ho mockato il generatore casuale per ottenere un comportamento prevedibile:
```scala
val fixedRandom: Random = new Random:
override def nextDouble(): Double = 0.1

val result: Seq[PeopleMovement] = GlobalLogic.compute(world, 1.0, fixedRandom).toList
```



3. **MovementStrategyDispatcher** - Collegamento tra strategia e logica
   Per mantenere disaccoppiamento e apertura all’estensione, ho introdotto un dispatcher centralizzato:


```scala
object MovementStrategyDispatcher:
def logicFor(strategy: MovementStrategy): MovementLogic = strategy match
case LocalPercentageMovement => LocalPercentageLogic
case GlobalLogicMovement     => GlobalLogic
case Static                  => StaticLogic
```

A questo si affianca un modulo `MovementStrategyLogic`:
```scala
object MovementStrategyLogic:
def compute(
             world: World,
             strategy: MovementStrategy,
             percentage: Percentage,
             rng: scala.util.Random
           ): Iterable[PeopleMovement] =

  MovementStrategyDispatcher.logicFor(strategy).compute(world, percentage, rng)
```

Questo permette al sistema di passare da una dichiarazione astratta di strategia a una logica concreta da eseguire.

L’intero processo è orchestrato dal metodo `MovementComputation.computeAllMovements`, che:

- itera su tutte le strategie definite nel World,
- per ciascuna strategia richiama il relativo MovementLogic tramite MovementStrategyLogic,
- accumula i movimenti proposti in una lista globale,
- applica i movimenti aggiornando i Node coinvolti.

```scala
  def computeAllMovements(world: World, rng: scala.util.Random): MovementResult =
  world.movements.foldLeft(MovementResult(world.nodes, List.empty)) {
    case (MovementResult(currentNodes, accMoves), (strategy, percent)) =>
      val newMoves = MovementStrategyLogic.compute(world, strategy, percent, rng)
      val updatedNodes = applyMovements(world.modifyNodes(currentNodes), newMoves).nodes
      MovementResult(updatedNodes, accMoves ++ newMoves)
  }

```

## Strategia vs Logica di movimento: separazione delle responsabilità
Il `World` dichiara che movimento deve accadere (strategie + percentuali), e solo gli eventi e i moduli operativi determinano come avviene il movimento.

Questa scelta progettuale consente:
- Al World di essere dichiarativo, stabile e testabile, modellando solo l’intento del movimento
- Agli eventi di essere l’unico punto in cui le logiche vengono risolte ed eseguite,
- Una chiara separazione delle responsabilità e maggiore modularità.

Per aggiungere una nuova strategia è sufficiente dichiarare la nuova `MovementStrategy`, fornire un’implementazione di `MovementLogic`, e registrarla nel `dispatcher`. Il World resta completamente isolato da questo processo.

Durante l’applicazione dei movimenti (applyMovements), viene utilizzata una distribuzione ipergeometrica per stimare, in modo realistico, quanti degli individui in movimento siano infetti.
Questo modello simula un’estrazione casuale senza rimpiazzo da una popolazione composta da individui sani e infetti, mantenendo la proporzione di partenza.


```scala
private def sampleInfected(node: Node, amount: Int): Int =
val hgd = new HypergeometricDistribution(
  node.population,
  node.infected,
  amount
)
hgd.sample()
```

**Nota**:  questa gestione basata sulla distribuzione ipergeometrica è stata realizzata in collaborazione con il collega Matteo Susca.

## GlobalLogic

Come detto in precedenza, sono state implementate due differenti strategie di movimento:
`GlobalLogic` e `LocalPercentageLogic`.

Qui mi concentrerò sulla prima, che è quella più complessa e interessante.
Questa logica considera l'intera struttura del World e la configurazione degli edge per determinare in modo intelligente e probabilistico dove e quanta popolazione spostare.

Per ogni nodo con popolazione maggiore di zero, `GlobalLogic` esamina tutti gli edge aperti che lo connettono ad altri nodi.
La quantità di persone da spostare viene calcolata come percentuale della popolazione del nodo, in base al parametro ricevuto.
Tuttavia, non tutti i movimenti vengono generati indiscriminatamente: entra in gioco una logica di filtro basata su capacità e probabilità.

Ogni edge può avere una capacità massima di transito e una probabilità base di movimento definite per tipologia (Air, Land, Sea). La decisione finale se spostare o meno le persone viene presa confrontando un valore casuale con una probabilità calcolata dinamicamente. Questa probabilità finale è ottenuta moltiplicando la probabilità base dell’edge per il rapporto tra il numero di persone da spostare e la popolazione media dei nodi nel mondo. In questo modo, i nodi con una popolazione sopra la media sono più propensi a generare movimento, mentre quelli più piccoli lo fanno meno frequentemente.

```scala
  private def getFinalProbability(
                                   edgeTypology: EdgeType,
                                   toMove: Int,
                                   avgPopulation: Int
                                 ): Double =
  edgeMovementConfig.probability.getOrElse(
    edgeTypology,
    0.0
  ) * (toMove.toDouble / avgPopulation)
```

```scala
  private def shouldMove(
                          edge: Edge,
                          nodeId: NodeId,
                          rng: scala.util.Random,
                          avgPopulation: Int,
                          toMove: Int
                        ): Boolean =
val finalProbability = getFinalProbability(
  edge.typology,
  toMove,
  avgPopulation
)
edge.other(nodeId).isDefined && !edge.isClose && rng.nextDouble() < finalProbability
```

Questo comportamento rispecchia fenomeni reali: ad esempio, città densamente popolate generano più traffico di persone, mentre le aree isolate o scarsamente abitate rimangono più statiche.

**Nota**:  Le logiche della GlobalLogic sono state realizzate in collaborazione con il collega Matteo Susca.


[Back to index](../../index.md) |
[Back to implementation](../../5-implementation/impl.md)