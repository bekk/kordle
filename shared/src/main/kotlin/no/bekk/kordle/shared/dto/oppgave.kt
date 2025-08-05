package no.bekk.kordle.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class OppgaveResponse(
    val oppgaveId: Int,
    val lengde: Int,
)

data class Oppgave(
    val id: Int,
    val ord: String,
    val lengde: Int,
) {
    fun tilOppgaveResponse(): OppgaveResponse {
        return OppgaveResponse(
            oppgaveId = this.id,
            lengde = this.lengde
        )
    }
}

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
    val bokstavGjettet: Char,
    var erBokstavenIOrdet: Boolean,
    val erBokstavenPaaRettsted: Boolean
)

@Serializable
data class HentFasitRequest(
    val oppgaveId: Int
)


@Serializable
data class HentFasitResponse(
    val fasitOrd: String
)
