# Andrea Zavatta

Nel progetto mi sono occupato della costruzione del modello del `World` e tutte le componenti interne:
`MovementStrategy`, `Edge` e `Node`, puntando su immutabilità, funzioni pure e testabilità.
Per quanto riguarda la parte grafica mi sono occupato di tutta la parte di rendering del `World`, il rendering dei `Node`
e del colore di questi ultimi in base agli infetti e ai morti, il drag and drop dei `Node`, il rendering degli edge e del colore in base
alla tipologia di edge.
In aggiunta a tutto quanto già descritto, ho progettato e implementato l’intero sistema di movimento delle persone all’interno del world.




i file di cui mi sono occupato sono:
`Edge`, `EdgeExtensions`, `MovementComputation`, `MovementStrategy`, `Node`, `Types`, `World`, `WorldFactory`, `WorldValidator`, `WorldConnectivity`,
`EdgeConfigurationFactory`, `EdgeMovementConfig`, `GlobalLogic`, `LocalPercentageLogic`, `MovementEvent`, `ChangeNodesInWorldEvent`, `MovementLogic`, `MovementLogicWithEdgeCapacityAndPercentage`,
`MovementStrategyDispatcher`, `MovementStrategyLogic`, `StaticLogic`, `CircularLayout`, `DefaultNodeViewFactory`, `EdgeLayer`, `EdgeUpdater`, `GraphLayout`, `LivePosition`, 
`NodeLayer`, `NodeView`, `NodeViewFactory`, `UpdatableWorldView`, `WorldRenderer`, `WorldView`, `ConsoleSimulationView`


## Il modello del mondo
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
   - Come case class offre subito immutabilità, copy, equals e pattern matching “gratis”, semplificando la gestione dello stato e il testing.
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

## Movimento della popolazione
Ho esteso il modello con un vero e proprio **sottosistema di movimento**.

[Back to index](../../index.md) |
[Back to implementation](../../5-implementation/impl.md)