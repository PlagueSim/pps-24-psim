# Andrea Zavatta

Nel progetto mi sono occupato della costruzione del modello del `World` e tutte le componenti interne:
`MovementStrategy`, `Edge` e `Node`, puntando su immutabilità, funzioni pure e testabilità.
Per quanto riguarda la parte grafica mi sono occupato di tutta la parte di rendering del `World`, il rendering dei `Node`
e del colore di questi ultimi in base agli infetti e ai morti, il drag and drop dei `Node`, il rendering degli edge e del colore in base
alla tipologia di edge.

Ho curato l'integrazione tra modello e interfaccia, assicurandomi che ogni aggiornamento dello stato di `World` si
riflettesse con precisione e fluidità sul rendering della vista.
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
- `Node`: con builder e validazioni(popolazione, infetti, morti)
- `Edge`: con ordinamento lessicografico (per consentire l'uguaglianza di edge unidirezionali) e tipologie (`Air`, `Land`, `Sea`)
- `MovementStrategy`: un trait per definire le strategie di movimento.
  `Edge` e `Node` sono corredate di comodi extension methods (infectedPercentage, increasePopulation, edgeId, getMapEdges, ecc.) per semplificare ogni operazione nel Mondo.

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
   - La copia immutabile con copy(nodes = .., edges = .., movements = ..) rende triviali aggiornamenti puntuali (ad es. modifyNodes, modifyEdges).
2. **Centralizzazione delle validazioni**
   - Il costruttore è privato: non è possibile creare un’istanza invalida bypassando i controlli.
   - Il companion apply(nodes, edges, movements) contentente le validazioni del mondo e la sua creazione tramite costruttore privato.
   - In questo modo ogni nuovo World viene garantito “sano” fin dalla creazione, e la logica di validazione resta centralizzata in un solo punto.

Quindi per garantire un modello coerente ho introdotto `WorldValidator` che si occupa di validare la creazione del `World` nel metodo `apply` del companion object.
Questo approccio consente di mantenere il codice pulito e facilmente testabile, poiché tutte le regole di validazione sono concentrate in un'unica classe.

```scala
object World:
  /* Creates a World instance after validating edges and movement strategies */
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
- **Separazione tra logica e distribuzione**
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

## Movimento della popolazione
Ho esteso il modello con un vero e proprio **sottosistema di movimento**.
Il sottosistema di movimento è stato progettato per gestire lo spostamento della popolazione tra i `Nodes` in maniera modulare, testabile e coerente con la modellazione immutabile del `World`
Per ogni tick di simulazione, il sistema determina quali individui si spostano, in che quantità e verso quali nodi, aggiornando lo stato del mondo in modo puro.
Alla base di questo sistema vi sono due componenti ben distinti: `MovementStrategy` e `MovementLogic`.
Il primo, MovementStrategy, è un trait sigillato che rappresenta le tipologie di comportamento possibili,
come `Static`, `LocalPercentageMovement` e `GlobalLogicMovement`.

```scala
sealed trait MovementStrategy

case object Static extends MovementStrategy

case object LocalPercentageMovement extends MovementStrategy

case object GlobalLogicMovement extends MovementStrategy
```

Ogni MovementStrategy è associata, all'interno del World, a una percentuale della popolazione totale che può muoversi secondo quella logica.


Il secondo, `MovementLogic`, definisce invece l’algoritmo vero e proprio, cioè la modalità concreta con cui i movimenti vengono generati.
Ogni logica implementa il metodo compute, che a partire dallo stato attuale del World e da un generatore casuale,
restituisce una lista di PeopleMovement, ognuno rappresentante uno spostamento di una certa quantità di persone da un nodo a un altro.

```scala
trait MovementLogic:
  def compute(
  world: World,
  percent: Percentage,
  rng: scala.util.Random
  ): Iterable[PeopleMovement]
```
L’intero processo è orchestrato dal metodo MovementComputation.computeAllMovements, che:

- itera su tutte le strategie definite nel World,
- per ciascuna strategia richiama il relativo MovementLogic tramite MovementStrategyLogic,
- accumula i movimenti proposti in una lista globale,
- applica i movimenti aggiornando i Node coinvolti.

Durante l’applicazione dei movimenti (applyMovements), viene utilizzata una distribuzione ipergeometrica per
determinare quanti degli individui che si spostano sono infetti.
In termini semplici, se da un nodo partono k persone e nel nodo ci sono N abitanti totali di cui I infetti,
la distribuzione ipergeometrica consente di stimare quanti dei k siano infetti, simulando un’estrazione casuale
senza rimpiazzo da un’urna con I palline rosse (infetti) e N−I bianche (sani).
Questo introduce un realismo statistico nella simulazione, mantenendo la proporzione tra infetti e sani durante gli spostamenti.

**Nota**:  questa gestione basata sulla distribuzione ipergeometrica è stata realizzata in collaborazione con il collega Matteo Susca.


Ogni PeopleMovement viene quindi applicato creando nuove istanze di Node,
con conteggi aggiornati per popolazione e infetti, senza mai modificare gli oggetti originali.
Questo approccio garantisce un’elevata affidabilità e facilità di test, in linea con i principi di immutabilità e programmazione funzionale adottati nel progetto.






[Back to index](../../index.md) |
[Back to implementation](../../5-implementation/impl.md)