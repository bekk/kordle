package no.bekk.kordle.server.service

import no.bekk.kordle.server.exceptions.OrdetEksistererAlleredeIDatabasenException
import no.bekk.kordle.server.exceptions.OrdetHarUgyldigLengdeException
import no.bekk.kordle.server.repository.OppgaveRepository
import no.bekk.kordle.shared.dto.BokstavTreff
import no.bekk.kordle.shared.dto.GjettOrdRequest
import no.bekk.kordle.shared.dto.Oppgave
import org.springframework.stereotype.Service

@Service
class OppgaveService(
    val oppgaveRepository: OppgaveRepository
) {

    fun hentTilfeldigOppgave(): Oppgave {
        val alleOppgaver = oppgaveRepository.hentAlleOppgaver()
        return alleOppgaver.random()
    }

    // TODO: Vurder om sjekken for eksisterende ord bør gjøres i databasen istedenfor. Kanskje en oppgave i seg selv?
    fun leggTilOrd(ordSomSkalLeggesTil:String): Oppgave {
        val erOrdetGyldigLengde = ordSomSkalLeggesTil.length in 4..6
        if (!erOrdetGyldigLengde) {
            throw OrdetHarUgyldigLengdeException("Ordet '$ordSomSkalLeggesTil' må ha en lengde mellom 4 og 6 tegn.")
        }

        val eksistererOrdetAlleredeIDatabasen = oppgaveRepository
            .hentAlleOppgaver()
            // 'Any' spør om det finnes MINST ett ord i listen som matcher betingelsen. I dette tilfellet om det finnes en oppgave med samme ord som det nye ordet en prøver å legge til.
            .any { oppgaveIDatabasen ->
                val ordTiloppgave = oppgaveIDatabasen.ord
                return@any ordTiloppgave == ordSomSkalLeggesTil
            }

        if (eksistererOrdetAlleredeIDatabasen) {
            throw OrdetEksistererAlleredeIDatabasenException("Ordet '$ordSomSkalLeggesTil' finnes allerede i databasen.")
        }

        val idTilNyopprettetOppgave = oppgaveRepository.leggTilOppgave(
            ord = ordSomSkalLeggesTil,
            lengde = ordSomSkalLeggesTil.length
        )
        return Oppgave(
            id = idTilNyopprettetOppgave,
            ord = ordSomSkalLeggesTil,
            lengde = ordSomSkalLeggesTil.length
        )
    }

    // TODO: Vurder om sjekken for eksisterende ord bør gjøres i databasen istedenfor. Kanskje en oppgave i seg selv?
    fun gjettOrd(gjettOrdRequest: GjettOrdRequest): List<BokstavTreff> {
        val oppgaveGjettetPaa = oppgaveRepository.hentOppgave(gjettOrdRequest.oppgaveId)
        val bokstavTreff = sjekkBokstavTreff(
            oppgave = oppgaveGjettetPaa,
            ordGjettet = gjettOrdRequest.ordGjett
        )
        return bokstavTreff
    }

    fun sjekkBokstavTreff(
        oppgave: Oppgave,
        ordGjettet: String
    ): List<BokstavTreff> {
        val ordIOppgave = oppgave.ord
        return ordGjettet.mapIndexed { index, bokstav ->
            BokstavTreff(
                plassISekvensen = index,
                bokstavGjettet = bokstav.toString(),
                erBokstavenIOrdet = ordIOppgave.contains(bokstav),
                erBokstavenPaaRettsted = ordIOppgave[index] == bokstav
            )
        }.sortedBy { it.plassISekvensen }
    }
}
