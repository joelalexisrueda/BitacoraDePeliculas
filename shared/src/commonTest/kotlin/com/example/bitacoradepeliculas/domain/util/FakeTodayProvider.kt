package com.example.bitacoradepeliculas.domain.util

import kotlinx.datetime.LocalDate

class FakeTodayProvider(
    var fixedToday: LocalDate = LocalDate(2026, 6, 15)
) : TodayProvider {
    override fun today(): LocalDate = fixedToday
}
