package no.bekk.kordle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.async.KtxAsync
import ktx.scene2d.*

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val stage = Stage(ScreenViewport()).also { Gdx.input.inputProcessor = it }
    private var value = ""
    private val valueLabel: Label

    init {
        Scene2DSkin.defaultSkin = Skin("skins/default/uiskin.json".toInternalFile())
        val table = scene2d.table {
            setFillParent(true)
            valueLabel = label(value).cell(colspan = 5)
        }
        val lines = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
        lines.forEachIndexed { i, line ->
            // row with 5 buttons
            table.row()
            if (i == lines.size - 1) {
                // Add a spacer for the last row to align with the delete button
                table.button {
                    label("[ENT]")
                    onClick {
                        println("Entered value $value")s
                    }
                }
            }
            line.forEach { letter ->
                table.button {
                    label(letter.uppercase())
                    onClick {
                        addLetter(letter)
                    }
                    it
                        .width(50f).height(50f)
                        .spaceLeft(10f).spaceBottom(10f)
                        .expandX().growX()
                }
            }
        }
        table.button {
            label("[DEL]")
            onClick {
                if (value.isNotEmpty()) {
                    value = value.dropLast(1)
                    valueLabel.setText(value.uppercase())
                }
            }
        }
        table.rows

        stage.addActor(table)
    }

    private fun addLetter(letter: Char) {
        if (value.length >= 6) return // Limit to 5 characters
        value += letter
        valueLabel.setText(value.uppercase())
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        batch.disposeSafely()
    }
}
