package com.vote.services.personnes.models

import java.time.LocalDate

case class Personne(
    identifiant: String,
    dob: LocalDate,
    nom: String,
    prenom: String,
    authorisationSpecial: Boolean
)
