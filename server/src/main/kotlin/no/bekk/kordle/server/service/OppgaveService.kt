package no.bekk.kordle.server.service

import no.bekk.kordle.server.domain.BokstavTreff
import no.bekk.kordle.server.domain.Oppgave
import no.bekk.kordle.server.exceptions.GjettetErIkkeIOrdlistaException
import no.bekk.kordle.server.exceptions.GjettetHarUgyldigLengdeException
import no.bekk.kordle.server.repository.OppgaveRepository
import no.bekk.kordle.server.utils.sjekkBokstavTreff
import org.springframework.stereotype.Service


@Service
class OppgaveService(
    val oppgaveRepository: OppgaveRepository,
    private val ordValidatorService: OrdValidatorService
) {
    fun hentTilfeldigOppgave(): Oppgave {
        val alleOppgaver = oppgaveRepository.hentAlleOppgaver()
        val tilfeldigOppgave = alleOppgaver.random()
        return tilfeldigOppgave
    }

    fun gjettOrd(oppgaveId: Int, ordGjettet: String): List<BokstavTreff> {
        if (!ordValidatorService.isValid(ordGjettet)) {
            throw GjettetErIkkeIOrdlistaException("Ordet '${ordGjettet}' er ikke i ordlista.")
        }
        val oppgaveGjettetPaa = oppgaveRepository.hentOppgave(oppgaveId)
        if (ordGjettet.length != oppgaveGjettetPaa.ord.length) {
            throw GjettetHarUgyldigLengdeException("Gjettet '${ordGjettet}' er feil lengde for oppgaven. Oppgaven har lengde ${oppgaveGjettetPaa.ord.length} tegn.")
        }
        val bokstavTreff = sjekkBokstavTreff(
            ordIOppgave = oppgaveGjettetPaa.ord,
            ordGjettet = ordGjettet
        )
        return bokstavTreff
    }
}
