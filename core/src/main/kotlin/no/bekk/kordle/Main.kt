package no.bekk.kordle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.scene2d.*
import no.bekk.kordle.requests.getTilfeldigOppgave
import no.bekk.kordle.requests.gjettOrd
import no.bekk.kordle.shared.dto.GjettOrdRequest
import no.bekk.kordle.widgets.GuessRow
import no.bekk.kordle.widgets.OnScreenKeyboard

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
    private var currentGuessIndex: Int = 0
        set(value) {
            field = value
            currentGuessRow.setIsActive()
        }

    private var guessRows: MutableList<GuessRow> = mutableListOf()

    private val currentGuessRow: GuessRow
        get() = guessRows[currentGuessIndex]

    private val guessTable: KTableWidget
    private val keyboard: OnScreenKeyboard

    private var oppgaveId = -1
    private var wordLength = 6
        set(value) {
            field = value
            buildGuessRows()
        }// Default word length, can be updated from the server

    private fun buildGuessRows() {
        guessTable.clearChildren()
        guessRows = (0 until maxGuesses).map {
            GuessRow(guessTable, wordLength)
        }.toMutableList()
    }

    init {
        getTilfeldigOppgave {
            oppgaveId = it.id
            wordLength = it.lengde
            // reset for å farge øverste rad
            currentGuessIndex = 0
        }

        Scene2DSkin.defaultSkin = createSkin(this)
        val rootTable = scene2d.table {
            setFillParent(true)
        }
        rootTable.label("KORDLE", "large") {
            color = BekkColors.Natt
            it.spaceBottom(20f)
        }
        rootTable.row()
        guessTable = rootTable.table {
            it
                .expandX()
                .expandY()
                .spaceBottom(20f)
        }
        buildGuessRows()
        rootTable.row()
        val keyboardTable = rootTable.table {
            it.fillX().expandX()
        }
        keyboard = OnScreenKeyboard(keyboardTable, this)

        stage.addActor(rootTable)

        stage.addListener(createKeyboardListener())
        currentGuessRow.setIsActive()
    }

    fun createKeyboardListener(): InputListener {
        return object : InputListener() {
            override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.BACKSPACE -> {
                        removeLetter()
                    }

                    Input.Keys.ENTER -> {
                        submit()
                    }

                    Input.Keys.ESCAPE -> {
                        reset()
                    }

                    else -> {
                        val letter = Input.Keys.toString(keycode)
                        val norwegianLetter = mapOf('\'' to 'æ', ';' to 'ø', '[' to 'å')
                        if (letter.length == 1) {
                            val char = letter[0]
                            if (char in norwegianLetter) {
                                addLetter(norwegianLetter[char] ?: char)
                                return super.keyDown(event, keycode)
                            } else if (char in 'A'..'Z') {
                                addLetter(char.lowercaseChar())
                                return super.keyDown(event, keycode)
                            }
                        }
                    }
                }

                return super.keyDown(event, keycode)
            }
        }
    }


    fun submit() {
        if (oppgaveId == -1) return
        val gjettOrdRequest = GjettOrdRequest(
            oppgaveId = oppgaveId,
            ordGjett = value.uppercase()
        )
        gjettOrd(gjettOrdRequest) { response ->
            currentGuessRow.markGuess(response)
            response.alleBokstavtreff.forEach { result ->
                keyboard.updateBestGuess(result.bokstavGjettet.lowercaseChar(), LetterGuessStatus.fromResponse(result))
            }
            if (currentGuessIndex < maxGuesses - 1) {
                currentGuessIndex++
            }
            value = ""
        }
    }

    fun reset() {
        value = ""
        guessRows.forEach { it.reset() } // Reset all guesses
        keyboard.reset()
        currentGuessIndex = 0
    }

    fun removeLetter() {
        if (value.isNotEmpty()) {
            value = value.dropLast(1)
            currentGuessRow.removeLetter()
        }
    }


    fun addLetter(letter: Char) {
        if (value.length >= wordLength) return
        value += letter
        currentGuessRow.addLetter(letter)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(BekkColors.Dag)
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

