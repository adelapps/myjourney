package ir.safareman.k60.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = CongressDarkPrimary,
  onPrimary = CongressDarkOnPrimary,
  primaryContainer = CongressDarkPrimaryContainer,
  onPrimaryContainer = CongressDarkOnPrimaryContainer,
  secondary = SlateDarkSecondary,
  onSecondary = SlateDarkOnSecondary,
  secondaryContainer = SlateDarkSecondaryContainer,
  onSecondaryContainer = SlateDarkOnSecondaryContainer,
  tertiary = CongressDarkTertiary,
  onTertiary = CongressDarkOnTertiary,
  tertiaryContainer = CongressDarkTertiaryContainer,
  onTertiaryContainer = CongressDarkOnTertiaryContainer,
  background = EditorialDarkBg,
  onBackground = Color(0xE1, 0xE3, 0xE1),
  surface = EditorialDarkSurface,
  onSurface = Color(0xE1, 0xE3, 0xE1),
  surfaceVariant = EditorialDarkSurfaceVariant,
  onSurfaceVariant = Color(0xC2, 0xC8, 0xC3)
)

private val LightColorScheme = lightColorScheme(
  primary = CongressLightPrimary,
  onPrimary = CongressLightOnPrimary,
  primaryContainer = CongressLightPrimaryContainer,
  onPrimaryContainer = CongressLightOnPrimaryContainer,
  secondary = SlateLightSecondary,
  onSecondary = SlateLightOnSecondary,
  secondaryContainer = SlateLightSecondaryContainer,
  onSecondaryContainer = SlateLightOnSecondaryContainer,
  tertiary = CongressLightTertiary,
  onTertiary = CongressLightOnTertiary,
  tertiaryContainer = CongressLightTertiaryContainer,
  onTertiaryContainer = CongressLightOnTertiaryContainer,
  background = EditorialLightBg,
  onBackground = Color(0x19, 0x1C, 0x1A),
  surface = EditorialLightSurface,
  onSurface = Color(0x19, 0x1C, 0x1A),
  surfaceVariant = EditorialLightSurfaceVariant,
  onSurfaceVariant = Color(0x41, 0x49, 0x42)
)

@Composable
fun TravelTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
