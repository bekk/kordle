package no.bekk.kordle.widgets

import ktx.scene2d.KTableWidget

class GuessRow(parent: KTableWidget, private val length: Int) {
    private var value = ""
    private val boxes: MutableList<GuessBox>

    init {
        parent.row()
        boxes = (0 until length).map {
            GuessBox(parent)
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
}
