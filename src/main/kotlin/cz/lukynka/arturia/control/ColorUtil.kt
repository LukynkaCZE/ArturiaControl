package cz.lukynka.arturia.control

import java.awt.Color
import kotlin.math.abs
import kotlin.math.roundToInt

fun hslToRgb(h: Double, s: Double, l: Double): Color {
    val c = (1 - abs(2 * l - 1)) * s
    val x = c * (1 - abs(((h / 60) % 2) - 1))
    val m = l - c / 2

    var r = 0.0
    var g = 0.0
    var b = 0.0

    when (h.roundToInt()) {
        in 0..59 -> {
            r = c; g = x; b = 0.0
        }
        in 60..119 -> {
            r = x; g = c; b = 0.0
        }
        in 120..179 -> {
            r = 0.0; g = c; b = x
        }
        in 180..239 -> {
            r = 0.0; g = x; b = c
        }
        in 240..299 -> {
            r = x; g = 0.0; b = c
        }
        in 300..360 -> {
            r = c; g = 0.0; b = x
        }
    }
    return Color(((r + m) * 255).roundToInt(), ((g + m) * 255).roundToInt(), ((b + m) * 255).roundToInt())
}
