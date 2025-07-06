package no.bekk.kordle.server.dto


data class Oppgave(
    val id: Int,
    val ord: String,
    val lengde: Int,
)

data class LeggTilOrdRequest(
    val ord: String
)
