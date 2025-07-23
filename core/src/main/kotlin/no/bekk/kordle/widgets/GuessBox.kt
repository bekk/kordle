package no.bekk.kordle.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import ktx.actors.plusAssign
import ktx.scene2d.KTableWidget
import ktx.scene2d.container
import ktx.scene2d.label
import no.bekk.kordle.LetterGuessStatus

class GuessBox(parent: KTableWidget, val index: Int) {
    private val label: Label
    private val container = parent.container {
        it.width(40f).height(40f)
        isTransform = true
        setSize(40f, 40f)
        setOrigin(Align.center)
        background = parent.skin.getDrawable("default-pane")

        label = label("") {
            setAlignment(Align.center)
        }

    }
    var value: Char? = null
        set(value) {
            field = value
            label.setText(value?.toString() ?: "")
        }

    fun setStatus(status: LetterGuessStatus) {
        val color = when (status) {
            LetterGuessStatus.NOT_GUESSED -> return
            LetterGuessStatus.NOT_IN_WORD -> Color.DARK_GRAY
            LetterGuessStatus.WRONG_POSITION -> Color.YELLOW
            LetterGuessStatus.CORRECT_POSITION -> Color.GREEN
        }
        animateToColor(color)
    }

    private fun animateToColor(color: Color) {
        val action = sequence(
            delay(0.1f * index),
            scaleTo(1f, 0f, 0.2f),
            color(color),
            run(Runnable { label.color = color }),
            scaleTo(1f, 1f, 0.2f)
        )
        container += action
    }
}

