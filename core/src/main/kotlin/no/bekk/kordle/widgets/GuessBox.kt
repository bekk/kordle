package no.bekk.kordle.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.scene2d.KTableWidget
import ktx.scene2d.label
import no.bekk.kordle.LetterGuessStatus

class GuessBox(parent: KTableWidget) {
    private val label: Label = parent.label("", style = "background") {
        it.width(40f).height(40f)
            .spaceLeft(10f).spaceBottom(10f)
            .expandX().growX()
    }
    var value: Char? = null
        set(value) {
            field = value
            label.setText(value?.toString() ?: "")
        }

    fun setStatus(status: LetterGuessStatus) {
        when (status) {
            LetterGuessStatus.NOT_GUESSED -> return
            LetterGuessStatus.NOT_IN_WORD -> label.color = Color.DARK_GRAY
            LetterGuessStatus.WRONG_POSITION -> label.color = Color.YELLOW
            LetterGuessStatus.CORRECT_POSITION -> label.color = Color.GREEN
        }
    }
}

