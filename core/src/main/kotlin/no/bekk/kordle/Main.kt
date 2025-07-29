package no.bekk.kordle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.async.KtxAsync
import ktx.freetype.freeTypeFontParameters
import ktx.freetype.registerFreeTypeFontLoaders
import ktx.scene2d.*
import ktx.style.label
import no.bekk.kordle.requests.getTilfeldigOppgave
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
    private var currentGuessIndex: Int = 0
        set(value) {
            field = value
            currentGuessRow.setIsActive()
        }

    private var guessRows: MutableList<GuessRow> = mutableListOf()

    private val currentGuessRow: GuessRow
        get() = guessRows[currentGuessIndex]

    private val buttonByCharacter = mutableMapOf<Char, KButton>()
    private val bestGuessByCharacter = mutableMapOf<Char, LetterGuessStatus>()
    private val guessTable: KTableWidget

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
        }

        Scene2DSkin.defaultSkin = createSkin()
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
        rootTable.table {
            setupKeyboard(this)
            it.fillX().expandX()
        }

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


    private fun updateBestGuess(letter: Char, guessStatus: LetterGuessStatus) {
        val currentBestStatus = bestGuessByCharacter.getOrDefault(letter, LetterGuessStatus.NOT_GUESSED)
        if (guessStatus > currentBestStatus) {
            bestGuessByCharacter[letter] = guessStatus
            buttonByCharacter[letter]?.apply {
                color = when (guessStatus) {
                    LetterGuessStatus.NOT_IN_WORD -> BekkColors.Natt
                    LetterGuessStatus.WRONG_POSITION -> BekkColors.Ild1
                    LetterGuessStatus.CORRECT_POSITION -> BekkColors.Jord1
                    else -> BekkColors.Vann1
                }
            }
        }
    }

    private fun submit() {
        if (oppgaveId == -1) return
        val gjettOrdRequest = GjettOrdRequest(
            oppgaveId = oppgaveId,
            ordGjett = value.uppercase()
        )
        gjettOrd(gjettOrdRequest) { response ->
            currentGuessRow.markGuess(response)
            response.alleBokstavtreff.forEach { result ->
                updateBestGuess(result.bokstavGjettet.lowercaseChar(), LetterGuessStatus.fromResponse(result))
            }
            if (currentGuessIndex < maxGuesses - 1) {
                currentGuessIndex++
            }
            value = ""
        }
    }

    private fun reset() {
        value = ""
        currentGuessIndex = 0
        guessRows.forEach { it.reset() } // Reset all guesses
        bestGuessByCharacter.clear() // Clear the best guesses
        buttonByCharacter.forEach { (_, button) ->
            button.color = BekkColors.Vann1 // Reset button colors
        }
    }

    fun removeLetter() {
        if (value.isNotEmpty()) {
            value = value.dropLast(1)
            currentGuessRow.removeLetter()
        }
    }

    private fun setupKeyboard(parent: KTableWidget) {
        val lines = listOf("qwertyuiopå", "asdfghjkløæ", "zxcvbnm")
        lines.forEachIndexed { i, line ->
            // row with 5 buttons
            parent.row()
            parent.table {
                if (i == lines.size - 1) {
                    // Add a spacer for the last row to align with the delete button
                    button {
                        label("✓", "small")
                        color = BekkColors.Vann1
                        onClick {
                            submit()
                        }
                        it.width(40f)
                    }
                }
                line.forEach { letter ->
                    val b = button {
                        label(letter.uppercase(), "small")
                        color = BekkColors.Vann1
                        onClick {
                            addLetter(letter)
                        }
                        it
                            .width(24f).height(40f)
                            .pad(2f)
                    }
                    buttonByCharacter[letter] = b
                }
                if (i == lines.size - 1) {
                    button {
                        label("⌫", "small")
                        color = BekkColors.Vann1

                        onClick {
                            removeLetter()
                        }
                        it.width(40f)
                    }
                }
            }
        }
    }

    private fun createSkin(): Skin {
        val assetManager = initiateAssetManager()

        assetManager.load(
            "sourceSans30.ttf",
            BitmapFont::class.java,
            freeTypeFontParameters("fonts/source-sans-3/SourceSans3-ExtraBold.ttf") {
                size = 30
                color = Color.WHITE
            }
        )
        assetManager.load(
            "sourceSans24.ttf",
            BitmapFont::class.java,
            freeTypeFontParameters("fonts/source-sans-3/SourceSans3-Bold.ttf") {
                size = 24
                color = Color.WHITE
                characters = FreeTypeFontGenerator.DEFAULT_CHARS + "⌫✓"
            }
        )
        assetManager.finishLoading()

        return Skin("skins/new/KordleNew.json".toInternalFile()).apply {
            add("sourceSans30", assetManager["sourceSans30.ttf", BitmapFont::class.java])
            add("sourceSans24", assetManager["sourceSans24.ttf", BitmapFont::class.java])
            label("small") {
                font = getFont("sourceSans24")
                fontColor = Color.WHITE
            }
            label("large") {
                font = getFont("sourceSans30")
                fontColor = Color.WHITE
            }
        }
    }

    fun initiateAssetManager(): AssetManager {
        val assetManager = AssetManager()
        // Calling registerFreeTypeFontLoaders is necessary in order to load TTF/OTF files:
        assetManager.registerFreeTypeFontLoaders()
        return assetManager
    }

    private fun addLetter(letter: Char) {
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
