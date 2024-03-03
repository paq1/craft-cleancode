package com.vote.services.personnes

import com.vote.helpers.Context
import com.vote.services.date.DateService
import com.vote.services.personnes.models.Personne

import scala.concurrent.Future

trait PersonneService {
  def dateService: DateService

  def getPersonne(identifiantPersonne: IdentifiantPersonne): Future[Personne]

  def isMajeur(personne: Personne)(implicit ctx: Context): Boolean =
    dateService.isMajeur(personne.dob)

  def hasSpecialAuthorisation(personne: Personne): Boolean =
    personne.authorisationSpecial
}
