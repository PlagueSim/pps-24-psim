package model.plague.placehoderTypes

import model.plague.*

sealed trait PlaceholderNodeTypes

sealed trait Temperature extends PlaceholderNodeTypes
case object Cold extends Temperature
case object Hot extends Temperature

sealed trait Climate extends PlaceholderNodeTypes
case object Humid extends Climate
case object Arid extends Climate

sealed trait Welfare extends PlaceholderNodeTypes
case object Rich extends Welfare
case object Poor extends Welfare

sealed trait Urbanisation extends PlaceholderNodeTypes
case object Rural extends Urbanisation
case object Urban extends Urbanisation