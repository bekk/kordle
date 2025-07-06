package no.bekk.kordle.server.repository

import no.bekk.kordle.server.dto.Ord
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Repository
class OppgaveRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {

    fun hentAlleOrd(): List<Ord> {
        return jdbcTemplate.query(
            "SELECT * FROM ORD",
            DataClassRowMapper(Ord::class.java),
        )
    }

    fun leggTilOrd(ord: Ord): Int {
        return jdbcTemplate.update(
            """INSERT INTO ORD (tekst, lengde)
                |VALUES (:tekst, :lengde)
            """.trimMargin(),
            MapSqlParameterSource(
                mapOf(
                    "tekst" to ord.tekst,
                    "lengde" to ord.lengde,
                )
            )
        )
    }

    fun eksistererOrdIDatabasen(tekst:String): Int? {
        return jdbcTemplate.queryForObject(
            """SELECT CASE WHEN
                |EXISTS(SELECT tekst FROM ORD WHERE tekst=:tekst)
                |THEN 1
                |ELSE 0
                |END AS tekstEksisterer
                |FROM dual;
            """.trimMargin(),
            MapSqlParameterSource(
                mapOf(
                    "tekst" to tekst,
                )
            ),
            DataClassRowMapper(Int::class.java)
        )
    }

}


