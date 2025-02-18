package shirkneko.zako.mksu.ui.theme


import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

object CardConfig {
    val defaultElevation: Dp = 4.dp

    // 卡片透明度，范围从 0f 到 1f
    var cardAlpha by mutableStateOf(1f)
    // 卡片阴影大小，单位为 dp
    var cardElevation by mutableStateOf(4.dp)
}

fun saveCardConfig(context: Context) {
    val prefs = context.getSharedPreferences("card_prefs", Context.MODE_PRIVATE)
    prefs.edit()
       .putFloat("card_alpha", CardConfig.cardAlpha)
       .putFloat("card_elevation", CardConfig.cardElevation.value)
       .apply()
}

fun loadCardConfig(context: Context) {
    val prefs = context.getSharedPreferences("card_prefs", Context.MODE_PRIVATE)
    CardConfig.cardAlpha = prefs.getFloat("card_alpha", 1f)
    CardConfig.cardElevation = prefs.getFloat("card_elevation", 4f).dp
}

@Composable
fun getCardColors(originalColor: Color): androidx.compose.material3.CardColors {
    val adjustedColor = originalColor.copy(alpha = CardConfig.cardAlpha)
    return CardDefaults.elevatedCardColors(
        containerColor = adjustedColor,
        contentColor = if (adjustedColor.luminance() > 0.5) Color.Black else Color.White
    )
}

fun getCardElevation(): Dp {
    return CardConfig.cardElevation
}
