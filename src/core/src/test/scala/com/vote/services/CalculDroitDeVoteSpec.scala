package com.vote.services

import com.context.ContextFixture
import com.vote.helpers.Context
import com.vote.services.justice.JusticeService
import com.vote.services.justice.models.Dossier
import com.vote.services.justiceEtrangere.JusticeEtrangereService
import com.vote.services.personnes.models.Personne
import com.vote.services.personnes.{IdentifiantPersonne, PersonneService}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class CalculDroitDeVoteSpec
    extends AsyncFlatSpec
    with ContextFixture
    with AsyncMockFactory {


  it should "false if is not majeur and dont check anything" in {
    (personneServiceMock
      .getPersonne(_: IdentifiantPersonne))
      .expects("id-12345")
      .returning(
        Future.successful(
          Personne(
            "id-12345",
            LocalDate.parse("1996-01-01"),
            "Paquin",
            "Pierre",
            authorisationSpecial = false
          )
        )
      )

    (personneServiceMock
      .isMajeur(_: Personne)(_: Context))
      .expects(*, _ctx)
      .returning(false)

    calculDroitDeVoteService
      .canVote("id-12345")
      .map { res => res shouldBe false }
  }

  it should "false if have no one spec OK but is major" in {

    (personneServiceMock
      .getPersonne(_: IdentifiantPersonne))
      .expects("id-12345")
      .returning(
        Future.successful(
          Personne(
            "id-12345",
            LocalDate.parse("1996-01-01"),
            "Paquin",
            "Pierre",
            authorisationSpecial = false
          )
        )
      )

    (personneServiceMock
      .isMajeur(_: Personne)(_: Context))
      .expects(*, _ctx)
      .returning(true)

    (personneServiceMock
      .hasSpecialAuthorisation(_: Personne))
      .expects(*)
      .returning(false)

    (justiceServiceMock
      .getDossiersJusticeFromIdentifiantPersonne(_: IdentifiantPersonne))
      .expects(*)
      .returning(Future.successful(Nil))

    (justiceServiceMock
      .isEligibleForVoting(_: List[Dossier]))
      .expects(*)
      .returning(false)

    (justiceEtrangereServiceMock
      .getDossierFromIdentifiantPersonne(_: IdentifiantPersonne))
      .expects(*)
      .returning(Future.successful(Nil))

    (justiceEtrangereServiceMock
      .hasDossiers(_: List[Unit]))
      .expects(*)
      .returning(true)

    calculDroitDeVoteService
      .canVote("id-12345")
      .map { res => res shouldBe false }
  }

  it should "true if has special authorization but is major" in {

    (personneServiceMock
      .getPersonne(_: IdentifiantPersonne))
      .expects("id-12345")
      .returning(
        Future.successful(
          Personne(
            "id-12345",
            LocalDate.parse("1996-01-01"),
            "Paquin",
            "Pierre",
            authorisationSpecial = false
          )
        )
      )

    (personneServiceMock
      .isMajeur(_: Personne)(_: Context))
      .expects(*, _ctx)
      .returning(true)

    (personneServiceMock
      .hasSpecialAuthorisation(_: Personne))
      .expects(*)
      .returning(true)

    calculDroitDeVoteService
      .canVote("id-12345")
      .map { res => res shouldBe true }
  }

  it should "true if justice is eligible but is major" in {

    (personneServiceMock
      .getPersonne(_: IdentifiantPersonne))
      .expects("id-12345")
      .returning(
        Future.successful(
          Personne(
            "id-12345",
            LocalDate.parse("1996-01-01"),
            "Paquin",
            "Pierre",
            authorisationSpecial = false
          )
        )
      )

    (personneServiceMock
      .isMajeur(_: Personne)(_: Context))
      .expects(*, _ctx)
      .returning(true)

    (personneServiceMock
      .hasSpecialAuthorisation(_: Personne))
      .expects(*)
      .returning(false)

    (justiceServiceMock
      .getDossiersJusticeFromIdentifiantPersonne(_: IdentifiantPersonne))
      .expects(*)
      .returning(Future.successful(Nil))

    (justiceServiceMock
      .isEligibleForVoting(_: List[Dossier]))
      .expects(*)
      .returning(true)

    calculDroitDeVoteService
      .canVote("id-12345")
      .map { res => res shouldBe true }
  }

  it should "true if dont have dossierJusticeEtrangere but is major" in {

    (personneServiceMock
      .getPersonne(_: IdentifiantPersonne))
      .expects("id-12345")
      .returning(
        Future.successful(
          Personne(
            "id-12345",
            LocalDate.parse("1996-01-01"),
            "Paquin",
            "Pierre",
            authorisationSpecial = false
          )
        )
      )

    (personneServiceMock
      .isMajeur(_: Personne)(_: Context))
      .expects(*, _ctx)
      .returning(true)

    (personneServiceMock
      .hasSpecialAuthorisation(_: Personne))
      .expects(*)
      .returning(false)

    (justiceServiceMock
      .getDossiersJusticeFromIdentifiantPersonne(_: IdentifiantPersonne))
      .expects(*)
      .returning(Future.successful(Nil))

    (justiceServiceMock
      .isEligibleForVoting(_: List[Dossier]))
      .expects(*)
      .returning(false)

    (justiceEtrangereServiceMock
      .getDossierFromIdentifiantPersonne(_: IdentifiantPersonne))
      .expects(*)
      .returning(Future.successful(Nil))

    (justiceEtrangereServiceMock
      .hasDossiers(_: List[Unit]))
      .expects(*)
      .returning(false)

    calculDroitDeVoteService
      .canVote("id-12345")
      .map { res => res shouldBe true }
  }


  lazy val calculDroitDeVoteService: CalculDroitDeVoteService =
    new CalculDroitDeVoteService {

      override implicit def ec: ExecutionContext = executionContext
      override def personneService: PersonneService = personneServiceMock
      override def justiceService: JusticeService = justiceServiceMock
      override def justiceEtrangereService: JusticeEtrangereService =
        justiceEtrangereServiceMock
    }

  val personneServiceMock: PersonneService = mock[PersonneService]
  val justiceServiceMock: JusticeService = mock[JusticeService]
  val justiceEtrangereServiceMock: JusticeEtrangereService =
    mock[JusticeEtrangereService]

}
