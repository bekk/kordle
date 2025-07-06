package no.bekk.kordle.server.service

import no.bekk.kordle.server.repository.OppgaveRepository
import no.bekk.kordle.server.repository.Ord
import org.springframework.stereotype.Service

@Service
class OppgaveService(
    val oppgaveRepository: OppgaveRepository
) {

    fun hentTilfeldigOrd(): Ord {
        val alleOrd = oppgaveRepository.hentAlleOrd()
        return alleOrd.random()
    }
}
