package no.bekk.kordle.server.service

import no.bekk.kordle.server.dto.Ord
import no.bekk.kordle.server.exceptions.OrdetEksistererAlleredeIDatabasenException
import no.bekk.kordle.server.exceptions.OrdetHarUgyldigLengdeException
import no.bekk.kordle.server.repository.OppgaveRepository
import org.springframework.stereotype.Service

@Service
class OppgaveService(
    val oppgaveRepository: OppgaveRepository
) {

    fun hentTilfeldigOrd(): Ord {
        val alleOrd = oppgaveRepository.hentAlleOrd()
        return alleOrd.random()
    }

    // TODO: Vurder om sjekken for eksisterende ord bør gjøres i databasen istedenfor. Kanskje en oppgave i seg selv?
    fun leggTilOrd(ordSomSkalLeggesTil:String): Ord {
        val erOrdetGyldigLengde = ordSomSkalLeggesTil.length in 4..6
        if (!erOrdetGyldigLengde) {
            throw OrdetHarUgyldigLengdeException("Ordet '$ordSomSkalLeggesTil' må ha en lengde mellom 4 og 6 tegn.")
        }

        val eksistererOrdetAlleredeIDatabasen = oppgaveRepository
            .hentAlleOrd()
            // 'Any' spør om det finnes MINST ett ord i listen som matcher betingelsen. I dette tilfellet om det finnes et ord med samme tekst som det nye ordet.
            .any { ordIdatabasen ->
                val tekstTilOrdIdatabasen = ordIdatabasen.tekst
                return@any tekstTilOrdIdatabasen == ordSomSkalLeggesTil
            }

        if (eksistererOrdetAlleredeIDatabasen) {
            throw OrdetEksistererAlleredeIDatabasenException("Ordet '$ordSomSkalLeggesTil' finnes allerede i databasen.")
        }

        val tekstLengde = ordSomSkalLeggesTil.length
        val nyttOrd = Ord(
            tekst = ordSomSkalLeggesTil,
            lengde = tekstLengde
        )
        oppgaveRepository.leggTilOrd(nyttOrd)
        return nyttOrd
    }
}
