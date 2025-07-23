package no.bekk.kordle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
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
import no.bekk.kordle.requests.gjettOrd
import no.bekk.kordle.shared.dto.GjettOrdRequest
import no.bekk.kordle.widgets.GuessRow

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
    private val maxGuesses = 6
    private var currentGuessIndex = 0
    private val guessRows: MutableList<GuessRow>

    private val currentGuessRow: GuessRow
        get() = guessRows[currentGuessIndex]

    init {
        Scene2DSkin.defaultSkin = Skin("skins/default/uiskin.json".toInternalFile())
        val rootTable = scene2d.table {
            setFillParent(true)
        }
        guessRows = (0 until maxGuesses).map {
            GuessRow(rootTable, 6)
        }.toMutableList()
        val lines = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
        lines.forEachIndexed { i, line ->
            // row with 5 buttons
            rootTable.row()
            if (i == lines.size - 1) {
                // Add a spacer for the last row to align with the delete button
                rootTable.button {
                    label("[ENT]")
                    onClick {
                        val gjettOrdRequest = GjettOrdRequest(
                            oppgaveId = 1,
                            ordGjett = value.uppercase()
                        )
                        gjettOrd(gjettOrdRequest)
                        if (currentGuessIndex < maxGuesses - 1) {
                            currentGuessIndex++
                        }
                        value = ""

                        println("Entered value $value")
                    }
                }
            }
            line.forEach { letter ->
                rootTable.button {
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
        rootTable.button {
            label("[DEL]")
            onClick {
                if (value.isNotEmpty()) {
                    value = value.dropLast(1)
                    currentGuessRow.removeLetter()
                }
            }
        }

        stage.addActor(rootTable)
    }

    private fun addLetter(letter: Char) {
        if (value.length >= 6) return
        value += letter
        currentGuessRow.addLetter(letter)
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
