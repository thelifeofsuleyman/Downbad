package com.thelifeofsuleyman.downbad.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class SoberWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: SoberWidget = SoberWidget()
}