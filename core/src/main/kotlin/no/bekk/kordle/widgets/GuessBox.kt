package no.bekk.kordle.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.scene2d.KTableWidget
import ktx.scene2d.label

class GuessBox(parent: KTableWidget) {
    private val label: Label = parent.label("") {
        it.width(40f).height(40f)
            .spaceLeft(10f).spaceBottom(10f)
            .expandX().growX()
    }
    var value: Char? = null
        set(value) {
            field = value
            label.setText(value?.toString() ?: "")
        }
}
