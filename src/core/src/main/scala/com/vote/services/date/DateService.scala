package com.vote.services.date

import com.vote.helpers.Context
import com.vote.services.date.DateService.AgeMajorite

import java.time.{LocalDate, ZoneOffset}
import java.time.temporal.ChronoUnit

trait DateService {
  def isMajeur(dob: LocalDate)(implicit ctx: Context): Boolean = {
    val now = LocalDate.ofInstant(ctx.now, ZoneOffset.UTC)
    val deltatime = ChronoUnit.YEARS.between(dob, now)
    deltatime >= AgeMajorite.toLong
  }

}
object DateService {
  val AgeMajorite: Int = 18
}
