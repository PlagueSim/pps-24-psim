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

## 



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
- Condizioni di fine simulazione
- `Scheduler`
- `UpdatableView`
- `ExecutionMode`

Quando viene avviata l'applicazione, viene chiesto al giocatore di selezionare il `Node` 
da dove vuole fare iniziare la sua `Disease`.
Dopo la selezione, questa informazione viene passata al `SimulationState` tramite dsl.

Il comando `setup` 

Gli `Event` sono classi responsabili di modificare lo stato della simulazione.
Per ogni sottosezione del `SimulationState` esiste un evento che ne modifica lo stato.


## game loop
`setup` serve per creare il `Controller` iniziale
tramite il quale viene calcolato lo stato successivo della simulazione mediante la monade del `SimulationEngine`.
Con questa monade vengono eseguiti in cascata tutti gli `Event` che modificano lo stato della simulazione e, alla fine
vengono aggiornate le viste `UpdatableView`.





[Back to index](../index.md) |
[Previous Chapter](../3-architectural-design/design.md) |
[Next Chapter](../5-implementation/impl.md)