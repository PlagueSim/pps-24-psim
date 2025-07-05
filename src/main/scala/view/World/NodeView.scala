package view

case class NodeView(
                     id: String,
                     visuals: Seq[Any],              
                     position: () => (Double, Double) 
                   )
