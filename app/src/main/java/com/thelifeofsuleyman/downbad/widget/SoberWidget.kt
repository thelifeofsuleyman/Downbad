package com.thelifeofsuleyman.downbad.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.thelifeofsuleyman.downbad.data.SoberRepository
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDateTime

class SoberWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = SoberRepository(context)
        val startTime = repository.startTimeFlow.first()
        val habitName = repository.habitNameFlow.first()

        val duration = Duration.between(startTime, LocalDateTime.now())
        val days = duration.toDays()
        val hours = duration.toHours() % 24

        provideContent {
            // Box allows us to stack items and align them to corners
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    // 0xCC is ~80% opacity. 0x104454 is your teal color.
                    .background(Color(0xCC104454))
                    .padding(12.dp)
            ) {
                // Timer Content (Centered)
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$habitName free".uppercase(),
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                            fontSize = 12.sp
                        )
                    )
                    Spacer(GlanceModifier.height(4.dp))
                    Text(
                        text = "$days Days",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$hours Hours",
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.9f)),
                            fontSize = 14.sp
                        )
                    )
                }

                // Refresh Button (Aligned to Bottom-Right)
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Image(
                        provider = ImageProvider(android.R.drawable.stat_notify_sync),
                        contentDescription = "Refresh",
                        modifier = GlanceModifier
                            .size(28.dp)
                            .padding(4.dp)
                            // Correct import for actionRunCallback is now included
                            .clickable(actionRunCallback<RefreshAction>())
                    )
                }
            }
        }
    }
}