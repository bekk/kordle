package no.bekk.kordle

import no.bekk.kordle.requests.gjettOrd
import no.bekk.kordle.shared.dto.GjettOrdRequest

class KordleController(private val ui: KordleUI) {
    var value = ""
    var oppgaveId = -1
    val maxGuesses = 6
    var currentGuessIndex: Int = 0
        set(value) {
            field = value
            ui.processSetActiveRow(value)
        }
    var wordLength = 6
        set(value) {
            field = value
        }

    fun submit() {
        if (oppgaveId == -1) return
        val gjettOrdRequest = GjettOrdRequest(
            oppgaveId = oppgaveId,
            ordGjett = value.uppercase()
        )
        gjettOrd(gjettOrdRequest) { response ->
            ui.processGjett(response)
            if (currentGuessIndex < maxGuesses - 1) {
                currentGuessIndex++
            }
            value = ""
        }
    }

    fun reset() {
        value = ""
        currentGuessIndex = 0
        ui.processReset()
    }

    fun removeLetter() {
        if (value.isNotEmpty()) {
            value = value.dropLast(1)
            ui.processRemoveLetter()
        }
    }

    fun addLetter(letter: Char) {
        if (value.length >= wordLength) return
        value += letter
        ui.processAddLetter(letter)
    }
}
