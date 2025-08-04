# Alessandro Stefani

Mi sono occupato della creazione della malattia e dei suoi tratti, degli eventi relativi
ad essa, e della loro rappresentazione all'utente


I file completamente sviluppati da me sono: 
- eventi
  - `DiseaseEvents`
  - `DnaPointsCalculator`
  - `EventBuffer`
- malattia
  - `Disease`
  - `Trait`
  - `TraitContainer`
  - `Abilities`
  - `Symptoms`
  - `Transmissions`
- view
  - `IntroPopUp`
  - `PlagueView`
  - `TraitsView`
  - `TraitInfoPanel`
  - `TraitList`

Mentre collaborato con Tosi ad `App` e con tutto il team a `MainView`

## Traits
Viste le numerose sta

## EventBuffer
È stato necessario implementare una tipologia di `Event` speciale per consentire di inserire dei nuovi eventi al `SimulaitonEngine`
allo scopo di permettere al giocatore di evolvere o involvere `Trait` durante la partita.
La soluzione che ho raggiunto è stata quella di estendere `Event` per creare un nuovo trait `EventBuffer` che permette di 
creare un oggetto all'interno di `SimulationEngine` che mantenga una lista di `Event` specifici
(`Event[Disease]` e `Event[Cure]` nel mio caso) aggiornabile durante l'esecuzione del gioco. 

```scala
/**
 *  A generic event buffer that needs to be extended to have a specified return type
 *  in [[modifyFunction]]
 * @param eventType default [[SimulationState]], needs to be specified between one of
 *                  its own parameters
 * @tparam A
 *   The type of result produced when the event is executed.
 */
sealed trait EventBuffer[A](eventType: SimulationState => A) extends Event[A]:
  private var eventList: List[Event[A]] = List.empty

  /**
   * adds an event to the queue
   * @param event the [[Event]] to add to the queue
   */
  def newEvent(event: Event[A]): Unit = this.synchronized:
    eventList = eventList :+ event

  /**
   * called by the engine to update the [[SimulationState]]
   */
  override def modifyFunction(state: SimulationState): A = this.synchronized:
    eventList.foreach(e => e.modifyFunction(state))
    eventList match
      case l if l.nonEmpty =>
        val event = l.last
        eventList = eventList.dropRight(1)
        execute()
        event.modifyFunction(state)
      case _ => eventType(state)
```

I metodi di `EventBuffer` sono `synchronized` per evitare race conditions tra l'inserimento di `Event` nella lista 
da parte del thread della view e la loro esecuzione da parte del thread che esegue la computazione degli eventi.

La lista si trova all'interno di un `object`, scelta che mi consente di ottenere un Singleton.
```scala
/**
 * The specific buffer only for [[Event]] that update [[Disease]]
 */
object DiseaseEventBuffer extends EventBuffer[Disease](_.disease)

/**
 * The specific buffer only for [[Event]] that update [[Cure]]
 */
object CureEventBuffer extends EventBuffer[Cure](_.cure)
```

Esecuzione degli `Event` presenti nel buffer in `SimulationEngine`:
```scala
  def runStandardSimulation(state: SimulationState): SimulationState =
    val tick = for
      _     <- executeEvent(DiseaseEventBuffer)
      _     <- executeEvent(CureEventBuffer)
      [...]
    yield ()
    tick.runS(state).value
```

Aggiunta di `Event` ai buffer in `TraitInfoPanel`:
```scala
  private val evolveButton = StdButton("Evolve"):
    newEvent(Evolution(tr))

  private val involveButton = StdButton("Involve"):
    newEvent(Involution(tr))
```


[Back to index](../../index.md) |
[Back to implementation](../../5-implementation/impl.md)