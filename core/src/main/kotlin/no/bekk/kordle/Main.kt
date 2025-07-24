package no.bekk.kordle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
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

    private val guessRows: MutableList<GuessRow>

    private val currentGuessRow: GuessRow
        get() = guessRows[currentGuessIndex]

    init {
        Scene2DSkin.defaultSkin = createSkin()
        val rootTable = scene2d.table {
            setFillParent(true)
        }
        rootTable.table {
            guessRows = (0 until maxGuesses).map {
                GuessRow(this, 6)
            }.toMutableList()
            it
                .expandX()
                .spaceBottom(20f)
        }
        rootTable.row()
        rootTable.table {
            setupKeyboard(this)
            it.fillX().expandX()
        }

        stage.addActor(rootTable)
        currentGuessRow.setIsActive()
    }

    private fun setupKeyboard(parent: KTableWidget) {
        val lines = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
        lines.forEachIndexed { i, line ->
            // row with 5 buttons
            parent.row()
            parent.table {
                if (i == lines.size - 1) {
                    // Add a spacer for the last row to align with the delete button
                    button {
                        label("✓", "small")
                        onClick {
                            val gjettOrdRequest = GjettOrdRequest(
                                oppgaveId = 1,
                                ordGjett = value.uppercase()
                            )
                            gjettOrd(gjettOrdRequest) { response ->
                                currentGuessRow.markGuess(response)
                                if (currentGuessIndex < maxGuesses - 1) {
                                    currentGuessIndex++
                                }
                                value = ""
                            }
                        }
                    }
                }
                line.forEach { letter ->
                    button {
                        label(letter.uppercase(), "small")
                        onClick {
                            addLetter(letter)
                        }
                        it
                            .width(24f).height(40f)
                            .pad(2f)
                            .expandX().growX()
                    }
                }
                if (i == lines.size - 1) {
                    button {
                        label("⌫", "small")
                        onClick {
                            if (value.isNotEmpty()) {
                                value = value.dropLast(1)
                                currentGuessRow.removeLetter()
                            }
                        }
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

        return Skin("skins/default/uiskin.json".toInternalFile()).apply {
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
        if (value.length >= 6) return
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
