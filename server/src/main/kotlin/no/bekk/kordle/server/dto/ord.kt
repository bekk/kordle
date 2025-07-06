package no.bekk.kordle.server.dto

import org.springframework.stereotype.Service


data class Oppgave(
    val id: Int,
    val ord: String,
    val lengde: Int,
)

data class LeggTilOrdRequest(
    val ord: String
)

data class GjettOrdRequest(
    val oppgaveId: Int,
    val ordGjett: String
)


data class GjettResponse(
    val oppgaveId: Int,
    val alleBokstavtreff: List<BokstavTreff>
)

data class BokstavTreff(
    val plassISekvensen: Int,
    val bokstavGjettet: String,
    val erBokstavenIOrdet: Boolean,
    val erBokstavenPaaRettsted: Boolean
)
