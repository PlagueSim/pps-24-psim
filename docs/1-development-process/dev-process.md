# Processo di sviluppo
Modalità di divisione in itinere dei task,
meeting/interazioni pianificate,
modalità di revisione in itinere dei task,
scelta degli strumenti di test/build/continuous integration

## SCRUM
Abbiamo seguito uno stile SCRUM inspired come consigliato nel punto P8 delle regole d’esame.
Ogni membro ha avuto ruolo di developer e nello specifico:
- Matteo Susca ha svolto il ruolo di committente ed esperto di dominio
- Andrea Zavatta ha svolto il ruolo di SCRUM master
- Lorenzo Tosi ha svolto il ruolo di product owner

Il lavoro è stato suddiviso in Sprint settimanali con il team che ogni lunedì effettuava un meeting per:
- redigere un breve documento nel quale si riportava quanto fatto nello sprint appena concluso
- discutere di eventuali problemi riscontrati
- pianificare gli obiettivi da portare a termine lo sprint seguente

### Jira
La gestione degli sprint e la suddivisione e l'assegnamento dei task è stata effettuata tramite Jira, un tool
che ha consentito al team di avere una board virtuale nella quale "appendere" i propri task e tracciarne il progresso.
Ogni task veniva, tramite l'utilizzo di un plug-in, sincronizzato con il relativo branch e commit per essere aggiornato
in tempo reale delle operazioni effettuate tramite git.

Lo scrum master ha definito una board con i relativi stati di avanzamento dei task:
- to do: Il task è stato concepito ma non è ancora in fase di sviluppo
- in progress: Il task è in fase di sviluppo
- test technical: Il task è in fase di testing relativo agli aspetti tecnici
- test business: Il task è in fase di testing relativo alle funzionalità
- deploy: Il task è concluso e in attesa della fine dello sprint
- done: Lo sprint è terminato e ogni task è inserito nella branch di produzione

### Confluence
Sincronizzato con Jira, il team ha sfruttato Confluence per mantenere in condivisione gli sprint log e 
i documenti relativi allo sviluppo del progetto.

## Branching Strategy
Il team ha sfruttato una branching strategy fortemente ispirata a git-flow. La repository è stata suddivisa in due 
branch stabili:
- `main`: destinato alle release stabili e aggiornato al termine di ogni sprint
- `develop`: utilizzato per lo sviluppo continuo e sufficientemente testato

Il team sviluppava ogni task su un branch `feature/ID-Jira_nome-task` che, una volta terminato lo sviluppo, veniva
incorporato tramite pull request in `develop`. Nel caso in cui il team identificava un bug, questo veniva risolto su 
un branch `bugfix/ID-Jira_nome-task` se identificato in `develop`; `hotfix/ID-Jira_nome-bug` se identificato in `main`.

## CI/CD
Sono state utilizzate le GitHub Actions per automatizzare la verifica del codice tramite test su pull request.




[Back to index](../index.md) |
[Next Chapter](../2-requirement-specification/req.md)