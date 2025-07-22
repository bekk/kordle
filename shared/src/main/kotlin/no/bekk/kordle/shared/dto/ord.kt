package no.bekk.kordle.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class Oppgave(
    val id: Int,
    val ord: String,
    val lengde: Int,
)

@Serializable
data class LeggTilOrdRequest(
    val ord: String
)

@Serializable
data class GjettOrdRequest(
    val oppgaveId: Int,
    val ordGjett: String
)


@Serializable
data class GjettResponse(
    val oppgaveId: Int,
    val alleBokstavtreff: List<BokstavTreff>
)

@Serializable
data class BokstavTreff(
    val plassISekvensen: Int,
    val bokstavGjettet: String,
    val erBokstavenIOrdet: Boolean,
    val erBokstavenPaaRettsted: Boolean
)
