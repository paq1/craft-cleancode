package com.vote.services.justice

import com.vote.services.justice.models.{Dossier, NiveauDossier}
import com.vote.services.personnes.IdentifiantPersonne
import scala.concurrent.Future
trait JusticeService {

  def getDossiersJusticeFromIdentifiantPersonne(
      numeroPersonne: IdentifiantPersonne
  ): Future[List[Dossier]]

  def isEligibleForVoting(dossiers: List[Dossier]): Boolean =
    dossiers.forall(dossierPasGrave)

  private def dossierPasGrave(dossier: Dossier): Boolean =
    dossier.niveau != NiveauDossier.Grave

}
