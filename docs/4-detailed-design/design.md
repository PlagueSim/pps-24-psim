# Design di dettaglio
Scelte rilevanti di design,
pattern di progettazione,
organizzazione del codice -- corredato da pochi ma efficaci diagrammi

Il design di dettaglio "esplode" (dettaglia) l'architettura,
ma viene concettualmente prima dell'implementazione, quindi non metteteci diagrammi
ultra-dettagliati estratti dal codice, quelli vanno nella parte di implementazione eventualmente.

<img src="package.drawio.png" alt="drawing" style="max-height:600px;"/>

Nota: 
- con cosa è stato realizzato (tecnologie, linguaggi, framework, librerie, ecc.)
- diagramma delle classi
- descrizione generale di cos'è, cosa fa, con chi lo fa


Il sistema è suddiviso in quattro moduli principali:
- **Controller**
- **Engine**
- **Model**
- **View**


## Tecnologie utilizzate
Le tecnologie utilizzate, oltre a quelle obbligatorie, sono:
- **ScalaFX**: framework d’interfaccia grafica per sviluppare schermate in maniera facile.
- **TuProlog**: framework per la programmazione logica, al fine d’implementare alcune regole tramite Prolog e utilizzarle all’interno dell’applicativo.
- **ScalaTest**: tool per lo sviluppo di test in Scala.
- **Cats**: libreria per la programmazione funzionale in Scala.
- **Apache commons math**: libreria usata per la generazione di numeri casuali e per il calcolo di distribuzioni statistiche.

## Core del progetto
Il sistema é sviluppato come un sistema di simulazione a eventi dove ogni evento rappresenta un cambiamento dello stato della simulazione.
É strutturato in diverse parti:
- Engine
- Malattia
- Mondo
- Cura
- Reazioni

### Engine
SimulationEngine rappresenta la parte centrale del sistema: ha il puro compito di eseguire uno step della
simulazione e internamente mantiene lo stato della simulazione in modo **immutabile**, aggiornandolo ogni volta che viene eseguito uno step.
Esegue in cascata tutti gli eventi, uno dopo l'altro, e alla fine ritorna il nuovo stato della simulazione.
Non contiene logiche specifiche ma si limita a invocare gli eventi, rispettando un flusso di simulazione prestabilito.
Questa impostazione riflette un'architettura event-driven, dove ogni evento è un’unità indipendente che incapsula una modifica ben definita sul sistema.
Inoltre, questo approccio assicura un’alta coesione interna e una bassa dipendenza da altre parti del progetto,
facilitando manutenzione, testing e riutilizzo.


## Avvio della Simulazione
La simulazione viene inizializzata e avviata tramite dsl con il comando `setup` fornendo i seguenti elementi:

- `SimulationState`
  - `Disease`
  - `Cure`
  - `World`
  - `InfectionLogic`
  - `DeathLogic`
  - `Reactions`
  - `Time`
- `Condizioni di fine simulazione`
- `Scheduler`
- `UpdatableView`
- `ExecutionMode`

Quando viene avviata l'applicazione, viene chiesto al giocatore di selezionare il `Node`
da dove vuole fare iniziare la sua `Disease`.
Dopo la selezione, questa informazione viene passata al `SimulationState` tramite dsl.


## Simulation loop
Il `Controller` esegue ricorsivamente la catena di `Event` dichiarati all'interno del `SimulationEngine` su un thread separato
finché la simulazione non termina raggiungendo le condizioni di fine simulazione. Durante il loop, una volta che 
tutti gli eventi sono stati eseguiti, il `Controller` si occupa anche di richiamare l'aggiornamento della `UpdatableView`
passando il nuovo stato della simulazione, ottenuto dopo l'esecuzione della catena di eventi.

## Collegamento Model-View
Il collegamento tra il Model e la View avviene durante il setup della simulazione: la DSL contiene la keyword `binding`
alla quale bisogna passare un oggetto che implementa l'interfaccia `UpdatableView` e che contenga tutte le parti di view che si vogliono aggiornare.
Questo design si ispira al pattern Observer: la *subscribe* é mascherata dalla keyword `binding` e il *notify* 
é lasciato alla responsabilità del `Controller` che, una volta ottenuto il nuovo stato della simulazione,
lo passa all'oggetto che implementa l'interfaccia `UpdatableView` chiamandone l'aggiornamento.
Il `Controller` fa quindi come da collante tra il Model e la View, permettendo di mantenere separati i due moduli.

[Back to index](../index.md) |
[Previous Chapter](../3-architectural-design/design.md) |
[Next Chapter](../5-implementation/impl.md)