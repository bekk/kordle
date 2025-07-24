package no.bekk.kordle.widgets

import ktx.scene2d.KTableWidget
import no.bekk.kordle.LetterGuessStatus
import no.bekk.kordle.shared.dto.GjettResponse

class GuessRow(parent: KTableWidget, private val length: Int) {
    private var value = ""
    private val boxes: MutableList<GuessBox>

    init {
        parent.row()
        boxes = (0 until length).map {
            GuessBox(parent, it)
        }.toMutableList()
    }

    fun addLetter(letter: Char) {
        if (value.length >= length) return
        value += letter
        boxes[value.length - 1].value = letter
    }

    fun removeLetter() {
        if (value.isNotEmpty()) {
            value = value.dropLast(1)
            boxes[value.length].value = null
        }
    }

    fun markGuess(guessResult: GjettResponse) {
        guessResult.alleBokstavtreff.forEachIndexed { i, result ->
            if (i >= boxes.size) return@forEachIndexed
            boxes[i].setStatus(
                when {
                    !result.erBokstavenIOrdet -> LetterGuessStatus.NOT_IN_WORD
                    result.erBokstavenPaaRettsted -> LetterGuessStatus.CORRECT_POSITION
                    else -> LetterGuessStatus.WRONG_POSITION
                }
            )
        }
        (guessResult.alleBokstavtreff.size..boxes.size - 1).forEach { i ->
            boxes[i].setStatus(LetterGuessStatus.NOT_IN_WORD)
        }
    }

    fun reset() {
        value = ""
        boxes.forEach { it.reset() }
    }

    fun setIsActive() {
        boxes.forEach { it.setIsActive() }
    }
}
