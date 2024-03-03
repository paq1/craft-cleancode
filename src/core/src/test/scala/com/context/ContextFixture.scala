package com.context

import com.vote.helpers.Context

import java.time.Instant

trait ContextFixture {

  implicit def _ctx: Context =
    Context(now = Instant.parse("2013-01-21T10:15:30.00Z"))

}
