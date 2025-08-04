# Testing

Per testare il codice del progetto abbiamo sfruttato `ScalaTest` insieme a `AnyFlatSpec`
e `Matchers` per definire dei test maggiormente chiari e leggibili. 
Sfruttando l'IDE IntelliJ è possibile lanciare i test *with coverage* ottenendo
informazione sul grado di copertura dei test relativamente alle classi, metodi
e linee di codice.

È risultato un grado di copertura delle linee di codice del 51% su tutto il progetto,
ma è da considerare che il package `view`, contenente gli elementi grafici, il package
`dsl` e `controller` non hanno tests, portando la coverage sul package `model`, contenente
gli elementi di "model" (come `Disease`, `Cure`, etc...), gli eventi ed engine, ad avere una
copertura sulle linee di codice del 77%, sulle classi dell' 85% e sui metodi dell' 81%.

[Back to index](../index.md) |
[Previous Chapter](../5-implementation/impl.md) |
[Next Chapter](../7-conclusion/end.md)