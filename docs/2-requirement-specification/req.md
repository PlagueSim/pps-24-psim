# Requisiti

## Business
**Plague-sim** è un gioco-simulazione sviluppato in Scala ispirato a **Plague-inc**. Consente al giocatore di simulare
una malattia che si diffonde nel mondo e di evolverla per infettare e uccidere tutta la popolazione prima 
che questa sia in grado di curarla.

## Modello di dominio


## Requisiti funzionali

### Utente
- Selezionare il nodo dal quale avviare la simulazione
- Visualizzare lo scorrere del tempo giorno per giorno
- Visualizzare l'evoluzione del mondo:
  - Spostamento della popolazione da un nodo a un altro
  - Variazione nel numero di infetti e morti
  - Chiusura dei collegamenti tra nodi e relativo cessare dello spostamento della popolazione
  - Variazione del colore dei collegamenti tra nodi in caso di chiusura
- Progresso della cura mostrato tramite barra di progresso
- Guadagno di punti da spendere per evolvere la malattia durante l'infezione
- Evoluzione della malattia da parte dell'utente nell'apposita interfaccia
- De-evoluzione della malattia da parte dell'utente nell'apposita interfaccia
- Visualizzazione delle statistiche della malattia

### Sistema
- Tutta la popolazione del mondo dovrà essere inizialmente sana
- Il nodo selezionato come inizio dovrà avere un infetto
- Le persone presenti in un nodo possono spostarsi in un altro solo se presente un collegamento aperto
- Ogni collegamento ha un numero massimo di persone trasportabili per giorno
- I collegamenti tra nodi iniziano a chiudersi se la malattia raggiunge un certo livello di **severity** e/o una 
determinata soglia di infetti
- Ogni nodo inizia a contribuire alla cura se la malattia raggiunge un certo livello di **severity** e una determinata
soglia di infetti
- Ogni nodo contribuisce alla cura proporzionalmente alla sua popolazione rispetto a quella globale
- Se un nodo perde popolazione a causa della malattia perderà proporzionalmente la sua capacità di ricerca della cura
- Il gioco termina se la cura raggiunge il 100% o tutta la popolazione è deceduta
- I **tratti** della malattia possono essere acquistati solo se:
  - L'utente ha abbastanza **punti dna**
  - L'utente ha già acquistato i **tratti** considerati prerequisito
- Un **tratto** della malattia non può essere de-evoluto se è stato già evoluto un altro **tratto** che lo richiede 
come prerequisito
- De-evolvere un **tratto** restituisce un po di **punti dna**

## Requisiti non funzionali
- La realizzazione di algoritmi e strutture dati efficienti per aspetti riguardanti la popolazione visti 
i potenzialmente grandi numeri da utilizzare 
- Interfaccia grafica intuitiva

### opzionale
- Realizzare un dsl per impostare la simulazione in maniera rapida e semplice
  
## Requisiti di implementazione
- Utilizzo di Scala 3.x
- Utilizzo di JDK 17+
### opzionale
- Utilizzo di TuProlog

[Back to index](../index.md) |
[Previous Chapter](../1-development-process/dev-process) |
[Next Chapter](../3-architectural-design/design.md)