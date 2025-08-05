# Requisiti

## Business
**Plague-sim** è un gioco-simulazione sviluppato in Scala ispirato a **Plague-inc**. Consente al giocatore di simulare
una malattia che si diffonde nel mondo e di evolverla per infettare e uccidere tutta la popolazione prima 
che questa sia in grado di curarla.

## Modello di dominio
Il **Mondo** è l'insieme dei **Nodi** e i loro **Collegamenti** che possono essere di **Terra** di **Mare** o di **Aria**.
Ogni nodo contiene della **Popolazione** che può spostarsi tra un nodo e l'altro tramite i relativi collegamenti.
La popolazione è suddivisa in **Sani** e **Infetti**, mentre i **Morti** non ne fanno parte.
La **Malattia** infetta la popolazione in base alla sua **Infettività** e la uccide in base all sua **Letalità**.
La malattia può **Evolvere** nuovi **Tratti** secondo la volontà del giocatore spendendo **punti dna**, oppure
gratuitamente nel caso in cui avvenga una **Mutazione** casuale determinata dalla **Probabilità di mutazione**
della malattia. L'infettività, la letalità e la probabilità di mutazione sono **Statistiche** della malattia,
calcolate come la somma delle statistiche di ogni tratto, insieme alla **Gravità** che simula la visibilità
dei tratti evoluti.
La gravità determina quanto i diversi nodi lavoreranno allo sviluppo di una **Cura**.
La cura ha una **Velocità** di **Progresso** determinata dalla somma del contributo dei vari nodi.

Esistono tre diverse categorie di tratti:
- **Trasmissioni**: si concentrano sull'incremento dell'infettività
- **Sintomi**: possono aumentare infettività, letalità, gravità, probabilità di mutazione e ridurre il progresso della cura
- **Abilità**: possono avere effetti diversi tra cui rallentare o ridurre il progresso della cura

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
- I collegamenti tra nodi iniziano a chiudersi se la malattia raggiunge un certo livello di severità e/o una 
determinata soglia di infetti
- Ogni nodo inizia a contribuire alla cura se la malattia raggiunge un certo livello di gravità e una determinata
soglia di infetti
- Ogni nodo contribuisce alla cura proporzionalmente alla sua popolazione rispetto a quella globale
- Se un nodo perde popolazione a causa della malattia perderà proporzionalmente la sua capacità di ricerca della cura
- Il gioco termina se la cura raggiunge il 100% o tutta la popolazione è deceduta
- I tratti della malattia possono essere acquistati solo se:
  - L'utente ha abbastanza punti dna
  - L'utente ha già acquistato i tratti considerati prerequisito
- Un tratto della malattia non può essere de-evoluto se è stato già evoluto un altro tratto che lo richiede 
come prerequisito
- De-evolvere un tratto restituisce un po di punti dna

### Opzionale
  - Ogni nodo del mondo ha diverse caratteristiche come temperatura, ricchezza, densità di popolazione, che
  alterano la diffusione della malattia:
    - Un nodo troppo freddo o caldo, ricco, o a bassa densità di popolazione rallenta la diffusione della malattia
    - Un nodo povero, densamente popolato o tiepido aumenta la diffusione della malattia

## Requisiti non funzionali
- La realizzazione di algoritmi e strutture dati efficienti per aspetti riguardanti la popolazione visti 
i potenzialmente grandi numeri da utilizzare 

### Opzionale
- Realizzare un dsl per impostare la simulazione in maniera rapida e semplice
  
## Requisiti di implementazione
- Utilizzo di Scala 3.x
- Utilizzo di JDK 17+
### Opzionale
- Utilizzo di TuProlog

[Back to index](../index.md) |
[Previous Chapter](../1-development-process/dev-process) |
[Next Chapter](../3-architectural-design/design.md)