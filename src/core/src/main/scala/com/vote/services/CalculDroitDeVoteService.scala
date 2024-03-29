package com.vote.services

import com.vote.helpers.Context
import com.vote.services.justice.JusticeService
import com.vote.services.justice.models.Dossier
import com.vote.services.justiceEtrangere.JusticeEtrangereService
import com.vote.services.personnes.models.Personne
import com.vote.services.personnes.{IdentifiantPersonne, PersonneService}

import scala.concurrent.{ExecutionContext, Future}

trait CalculDroitDeVoteService {

  implicit def ec: ExecutionContext

  protected def personneService: PersonneService
  protected def justiceService: JusticeService

  protected def justiceEtrangereService: JusticeEtrangereService

  /** les problemes sont les suivants:
    *  -> si la personne n'est pas majeur, on fait quand meme les autres call alors qu'on pourrait retourner faux.
    *  -> on fait tous les call des "ou" alors que si 1 est vrai pas besoins de verif le reste
    */
//  def canVote(
//      identifiantPersonne: IdentifiantPersonne
//  )(implicit ctx: Context): Future[Boolean] = for {
//    personne <- personneService.getPersonne(identifiantPersonne)
//    isMajeur = personneService.isMajeur(personne)
//    hasSpecialAuthorisation = personneService.hasSpecialAuthorisation(personne)
//    dossierPersonne <- justiceService.getDossiersJusticeFromIdentifiantPersonne(
//      identifiantPersonne
//    )
//    justiceIsEligible = justiceService.isEligibleForVoting(dossierPersonne)
//    dossierJusticeEtrangere <- justiceEtrangereService
//      .getDossierFromIdentifiantPersonne(identifiantPersonne)
//    hasDossierJusticeEtrangere = justiceEtrangereService.hasDossiers(
//      dossierJusticeEtrangere
//    )
//  } yield isMajeur && (hasSpecialAuthorisation || justiceIsEligible || !hasDossierJusticeEtrangere)

  def canVote(
      identifiantPersonne: IdentifiantPersonne
  )(implicit ctx: Context): Future[Boolean] = for {
    personne <- personneService.getPersonne(identifiantPersonne)
    isMajeur = personneService.isMajeur(personne)
    mandatoryRequestValid = isMajeur

    hasSpecialAuthorisation = maybeCheckHasSpecial(mandatoryRequestValid, personne)

    needCheckEligibleFromJustice =
      mandatoryRequestValid && !hasSpecialAuthorisation
    isEligibleForVoting <- maybeCheckEligibleFromJustice(needCheckEligibleFromJustice, identifiantPersonne)

    needCheckEligibleFromJusticeEtrangere = mandatoryRequestValid && (!hasSpecialAuthorisation && !isEligibleForVoting)
    hasNotDossiersEtranger <-
      if (needCheckEligibleFromJusticeEtrangere) hasNoDossierFromJusticeEtrangere(identifiantPersonne)
      else Future.successful(false)
  } yield mandatoryRequestValid && (hasSpecialAuthorisation || isEligibleForVoting || hasNotDossiersEtranger)

  private def maybeCheckEligibleFromJustice(
      needCheck: Boolean,
      identifiantPersonne: IdentifiantPersonne,
      default: Boolean = false
  ): Future[Boolean] =
    if (needCheck) isEligibleFromJustice(identifiantPersonne)
    else Future.successful(default)
  private def maybeCheckHasSpecial(needCheck: Boolean, personne: Personne, default: Boolean = false): Boolean =
    if (needCheck) personneService.hasSpecialAuthorisation(personne)
    else default

  private def hasNoDossierFromJusticeEtrangere(identifiantPersonne: IdentifiantPersonne): Future[Boolean] =
    justiceEtrangereService
      .getDossierFromIdentifiantPersonne(identifiantPersonne)
      .map { dossiesEtranger => !justiceEtrangereService.hasDossiers(dossiesEtranger) }

  private def isEligibleFromJustice(
      identifiantPersonne: IdentifiantPersonne
  ): Future[Boolean] =
    justiceService
      .getDossiersJusticeFromIdentifiantPersonne(
        identifiantPersonne
      )
      .map(justiceService.isEligibleForVoting(_))

  //  def canVote(
//      identifiantPersonne: IdentifiantPersonne
//  )(implicit ctx: Context): Future[Boolean] = {
//    personneService
//      .getPersonne(identifiantPersonne)
//      .map { isMajeur(_) }
//      .map { hasSpectialAutorisation } // or
//      .flatMap { isEligibleForVoting } // or
//      .flatMap { hasNotDossierJusticeEtrangere } // or
//      .map { case (result, _, _, _) => result }
//  }

//  private def isMajeur(
//      personne: Personne
//  )(implicit ctx: Context): (Boolean, Option[Personne]) =
//    if (personneService.isMajeur(personne)) {
//      (true, Some(personne))
//    } else {
//      (false, None)
//    }
//
//  private def hasSpectialAutorisation(
//      inputs: (Boolean, Option[Personne])
//  ): (Boolean, Option[Personne]) =
//    inputs match {
//      case (_, Some(personne)) =>
//        if (personneService.hasSpecialAuthorisation(personne)) {
//          (true, None)
//        } else {
//          (false, Some(personne))
//        }
//      case (result, _) => (result, None)
//    }
//
//  private def isEligibleForVoting(
//      inputs: (Boolean, Option[Personne])
//  ): Future[(Boolean, Option[Personne], Option[List[Dossier]])] = inputs match {
//    case (_, Some(personne)) =>
//      justiceService
//        .getDossiersJusticeFromIdentifiantPersonne(personne.identifiant)
//        .map { dossiers =>
//          if (justiceService.isEligibleForVoting(dossiers)) {
//            (true, None, None)
//          } else {
//            (false, Some(personne), Some(dossiers))
//          }
//        }
//    case (result, _) => Future.successful((result, None, None))
//  }
//
//  private def hasNotDossierJusticeEtrangere(
//      inputs: (Boolean, Option[Personne], Option[List[Dossier]])
//  ): Future[
//    (Boolean, Option[Personne], Option[List[Dossier]], Option[List[Unit]])
//  ] = {
//    inputs match {
//      case (_, Some(personne), Some(dossier)) =>
//        justiceEtrangereService
//          .getDossierFromIdentifiantPersonne(personne.identifiant)
//          .map { dossiersJusticeEtrangere =>
//            if (justiceEtrangereService.hasDossiers(dossiersJusticeEtrangere)) {
//              (
//                false,
//                Some(personne),
//                Some(dossier),
//                Some(dossiersJusticeEtrangere)
//              )
//            } else {
//              (true, None, None, None)
//            }
//          }
//      case (result, _, _) =>
//        Future.successful((result, None, None, None))
//    }
//  }

}
