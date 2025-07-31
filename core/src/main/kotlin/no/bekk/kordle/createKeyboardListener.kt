package no.bekk.kordle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

fun createKeyboardListener(controller: KordleController): InputListener {
    return object : InputListener() {
        override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
            when (keycode) {
                Input.Keys.BACKSPACE -> {
                    controller.removeLetter()
                }

                Input.Keys.ENTER -> {
                    controller.submit()
                }

                Input.Keys.ESCAPE -> {
                    controller.reset()
                }

                else -> {
                    val letter = Input.Keys.toString(keycode)
                    val norwegianLetter = mapOf('\'' to 'æ', ';' to 'ø', '[' to 'å')
                    if (letter.length == 1) {
                        val char = letter[0]
                        if (char in norwegianLetter) {
                            controller.addLetter(norwegianLetter[char] ?: char)
                            return super.keyDown(event, keycode)
                        } else if (char in 'A'..'Z') {
                            controller.addLetter(char.lowercaseChar())
                            return super.keyDown(event, keycode)
                        }
                    }
                }
            }

            return super.keyDown(event, keycode)
        }
    }
}
