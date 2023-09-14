package model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerButton

class MouseAction (
    var position: Offset,
    var button: PointerButton
)