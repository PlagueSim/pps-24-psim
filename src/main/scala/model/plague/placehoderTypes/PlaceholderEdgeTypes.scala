package model.plague.placehoderTypes

sealed trait PlaceholderEdgeTypes

case object Air extends PlaceholderEdgeTypes
case object Water extends PlaceholderEdgeTypes
case object Land extends PlaceholderEdgeTypes
