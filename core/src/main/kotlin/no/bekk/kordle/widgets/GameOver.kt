package no.bekk.kordle.widgets

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.actors.onClick
import ktx.graphics.copy
import ktx.scene2d.*
import no.bekk.kordle.BekkColors
import no.bekk.kordle.KordleController
import no.bekk.kordle.requests.getTilfeldigOppgave
import no.bekk.kordle.shared.dto.StatsForUser

class GameOver(private val parent: Stage, private val controller: KordleController) {
    private val label: Label
    private val stats: Stats
    private val table: KTableWidget = scene2d.table {
        background = (skin.getDrawable("white") as TextureRegionDrawable).tint(BekkColors.Natt.copy(alpha = 0.9f))
        label = label("Game over", "large") {
            color = BekkColors.Dag
        }
        row()
        button {
            label("Ny oppgave", "small")
            color = BekkColors.Vann1
            onClick {
                getTilfeldigOppgave { oppgaveResponse ->
                    controller.currentOppgave = oppgaveResponse
                    controller.reset()
                    hide()
                }
            }
        }
        row()
        table {
        }.also {
            stats = Stats(it, controller.maxGuesses)
        }
        setFillParent(true)
    }

    init {
        table.isVisible = false
        parent.addActor(table)
    }

    fun toggle(): Boolean {
        this.label.setText("Pauset")
        val isVisible = !table.isVisible
        table.isVisible = isVisible
        return isVisible
    }

    fun setStats(stats: StatsForUser?) {
        this.stats.stats = stats
    }

    fun show(won: Boolean) {
        this.label.setText(
            if (won) {
                "Du vant!"
            } else {
                "Du tapte!"
            }
        )
        table.isVisible = true
        table.toFront()
    }

    fun hide() {
        table.isVisible = false
    }
}

