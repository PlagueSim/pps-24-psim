package view

case class NodeView(
                     id: String,
                     visuals: Seq[Any],              // UI nodes (e.g., javafx.scene.Node)
                     position: () => (Double, Double) // A function returning current (x,y)
                   )
