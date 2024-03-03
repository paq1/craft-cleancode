package com.vote.helpers

import java.time.Instant

case class Context(
    now: Instant,
    by: String = "sys:bot"
)
