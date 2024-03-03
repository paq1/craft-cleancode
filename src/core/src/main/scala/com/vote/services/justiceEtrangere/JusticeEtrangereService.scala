package com.vote.services.justiceEtrangere

import com.vote.services.personnes.IdentifiantPersonne

import scala.concurrent.Future

trait JusticeEtrangereService {
  def getDossierFromIdentifiantPersonne(
      identifiantPersonne: IdentifiantPersonne
  ): Future[List[Unit]]

  def hasDossiers(dossiers: List[Unit]): Boolean = dossiers.nonEmpty
}
