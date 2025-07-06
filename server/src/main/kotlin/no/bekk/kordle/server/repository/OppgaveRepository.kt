package no.bekk.kordle.server.repository

import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Repository
class OppgaveRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {

    fun hentAlleOrd(): List<Ord> =
        jdbcTemplate.query(
            "SELECT * FROM ORD",
            DataClassRowMapper(Ord::class.java),
        )
}

data class Ord(
    val tekst: String,
    val lengde: Int,
)
