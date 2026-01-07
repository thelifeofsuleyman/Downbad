package com.thelifeofsuleyman.downbad.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class SlantedShape(val slantWidth: Float = 40f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(slantWidth, 0f) // Top left shifted right
            lineTo(size.width, 0f) // Top right
            lineTo(size.width - slantWidth, size.height) // Bottom right shifted left
            lineTo(0f, size.height) // Bottom left
            close()
        }
        return Outline.Generic(path)
    }
}