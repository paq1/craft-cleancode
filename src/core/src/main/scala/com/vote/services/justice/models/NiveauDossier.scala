package com.vote.services.justice.models

object NiveauDossier extends Enumeration {
  type NiveauDossier = Value

  val Grave, EnCours, Classique = Value
}

// scala 3 :
//enum NiveauDossier {
//  case Grave extends NiveauDossier
//  case EnCours extends NiveauDossier
//  case Classique extends NiveauDossier
//
//}
