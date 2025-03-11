package shirkneko.zako.sukisu.ui.theme

import androidx.compose.ui.graphics.Color

sealed class ThemeColors {
    abstract val Primary: Color
    abstract val Secondary: Color
    abstract val Tertiary: Color
    abstract val OnPrimary: Color
    abstract val OnSecondary: Color
    abstract val OnTertiary: Color
    abstract val PrimaryContainer: Color
    abstract val SecondaryContainer: Color
    abstract val TertiaryContainer: Color
    abstract val OnPrimaryContainer: Color
    abstract val OnSecondaryContainer: Color
    abstract val OnTertiaryContainer: Color


    // 蓝色主题
    object Default : ThemeColors() {
        override val Primary = Color(0xFF2196F3)
        override val Secondary = Color(0xFF1E88E5)
        override val Tertiary = Color(0xFF0D47A1)
        override val OnPrimary = Color(0xFFFFFFFF)
        override val OnSecondary = Color(0xFFFFFFFF)
        override val OnTertiary = Color(0xFFFFFFFF)
        override val PrimaryContainer = Color(0xFFE3F2FD)
        override val SecondaryContainer = Color(0xFFBBDEFB)
        override val TertiaryContainer = Color(0xFF90CAF9)
        override val OnPrimaryContainer = Color(0xFF000000)
        override val OnSecondaryContainer = Color(0xFF000000)
        override val OnTertiaryContainer = Color(0xFF000000)
    }

    // （黄色）
    object Yellow : ThemeColors() {
        override val Primary = Color(0xFFFFC107)
        override val Secondary = Color(0xFFFFA000)
        override val Tertiary = Color(0xFF5D4037)
        override val OnPrimary = Color(0xFF000000)
        override val OnSecondary = Color(0xFF000000)
        override val OnTertiary = Color(0xFFFFFFFF)
        override val PrimaryContainer = Color(0xFFFFF9C4)
        override val SecondaryContainer = Color(0xFFFFECB3)
        override val TertiaryContainer = Color(0xFFBCAAA4)
        override val OnPrimaryContainer = Color(0xFF000000)
        override val OnSecondaryContainer = Color(0xFF000000)
        override val OnTertiaryContainer = Color(0xFF000000)
    }



    // 绿色主题
    object Green : ThemeColors() {
        override val Primary = Color(0xFF4CAF50)
        override val Secondary = Color(0xFF43A047)
        override val Tertiary = Color(0xFF1B5E20)
        override val OnPrimary = Color(0xFFFFFFFF)
        override val OnSecondary = Color(0xFFFFFFFF)
        override val OnTertiary = Color(0xFFFFFFFF)
        override val PrimaryContainer = Color(0xFFE8F5E9)
        override val SecondaryContainer = Color(0xFFC8E6C9)
        override val TertiaryContainer = Color(0xFFA5D6A7)
        override val OnPrimaryContainer = Color(0xFF000000)
        override val OnSecondaryContainer = Color(0xFF000000)
        override val OnTertiaryContainer = Color(0xFF000000)
    }

    // 紫色主题
    object Purple : ThemeColors() {
        override val Primary = Color(0xFF9C27B0)
        override val Secondary = Color(0xFF8E24AA)
        override val Tertiary = Color(0xFF4A148C)
        override val OnPrimary = Color(0xFFFFFFFF)
        override val OnSecondary = Color(0xFFFFFFFF)
        override val OnTertiary = Color(0xFFE1BEE7)
        override val PrimaryContainer = Color(0xFFF3E5F5)
        override val SecondaryContainer = Color(0xFFE1BEE7)
        override val TertiaryContainer = Color(0xFFCE93D8)
        override val OnPrimaryContainer = Color(0xFF000000)
        override val OnSecondaryContainer = Color(0xFF000000)
        override val OnTertiaryContainer = Color(0xFF000000)
    }

    // 橙色主题
    object Orange : ThemeColors() {
        override val Primary = Color(0xFFFF9800)
        override val Secondary = Color(0xFFFB8C00)
        override val Tertiary = Color(0xFFE65100)
        override val OnPrimary = Color(0xFF000000)
        override val OnSecondary = Color(0xFF000000)
        override val OnTertiary = Color(0xFFFFFFFF)
        override val PrimaryContainer = Color(0xFFFFF3E0)
        override val SecondaryContainer = Color(0xFFFFE0B2)
        override val TertiaryContainer = Color(0xFFFFCC80)
        override val OnPrimaryContainer = Color(0xFF000000)
        override val OnSecondaryContainer = Color(0xFF000000)
        override val OnTertiaryContainer = Color(0xFF000000)
    }

    // 粉色主题
    object Pink : ThemeColors() {
        override val Primary = Color(0xFFE91E63)
        override val Secondary = Color(0xFFD81B60)
        override val Tertiary = Color(0xFF880E4F)
        override val OnPrimary = Color(0xFFFFFFFF)
        override val OnSecondary = Color(0xFFFFFFFF)
        override val OnTertiary = Color(0xFFFFFFFF)
        override val PrimaryContainer = Color(0xFFFCE4EC)
        override val SecondaryContainer = Color(0xFFF8BBD0)
        override val TertiaryContainer = Color(0xFFF48FB1)
        override val OnPrimaryContainer = Color(0xFF000000)
        override val OnSecondaryContainer = Color(0xFF000000)
        override val OnTertiaryContainer = Color(0xFF000000)
    }
}