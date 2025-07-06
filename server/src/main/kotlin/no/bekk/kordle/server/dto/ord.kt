package no.bekk.kordle.server.dto


data class Ord(
    val tekst: String,
    val lengde: Int,
)

data class LeggTilOrdRequest(
    val tekstSomSkalLeggesTil: String
)
