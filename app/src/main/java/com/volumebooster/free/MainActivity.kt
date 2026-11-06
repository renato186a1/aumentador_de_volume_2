package com.volumebooster.free

import android.app.Activity
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import kotlin.math.roundToInt

/**
 * Volume Booster Free
 * Offline, ad-free prototype. Audio effects are attached to output mix session 0.
 * Actual support and audible effect depend on Android vendor/audio HAL.
 */
class MainActivity : Activity() {
    private val bg = Color.rgb(13, 17, 27)
    private val panel = Color.rgb(25, 32, 46)
    private val accent = Color.rgb(89, 230, 164)
    private val muted = Color.rgb(158, 171, 191)
    private var enhancer: LoudnessEnhancer? = null
    private var equalizer: Equalizer? = null
    private var enabled = false
    private var gainMb = 0
    private lateinit var gainLabel: TextView
    private lateinit var status: TextView
    private lateinit var toggle: Button
    private lateinit var gainSlider: SeekBar
    private lateinit var eqContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() or
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        buildUi()
        initializeEffects()
        renderState()
    }

    private fun initializeEffects() {
        try {
            enhancer = LoudnessEnhancer(0).apply {
                setTargetGain(0)
                enabled = false
            }
        } catch (_: Exception) { enhancer = null }
        try {
            equalizer = Equalizer(0, 0).apply { enabled = false }
        } catch (_: Exception) { equalizer = null }
        if (enhancer == null && equalizer == null) {
            status.text = "Efeitos globais não disponíveis neste aparelho"
        }
    }

    private fun buildUi() {
        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(28), dp(22), dp(28))
        }
        scroll.addView(content)
        setContentView(scroll)

        val brand = TextView(this).apply {
            text = "VOLUME BOOSTER"
            textSize = 12f
            letterSpacing = .18f
            setTextColor(accent)
            typeface = Typeface.DEFAULT_BOLD
        }
        content.addView(brand)
        content.addView(text("FREE", 30f, Color.WHITE, true).apply {
            setPadding(0, dp(4), 0, dp(2))
        })
        content.addView(text("Amplificação de áudio • Offline • Sem anúncios", 13f, muted))
        content.addView(space(24))

        val boostCard = card()
        boostCard.addView(text("AMPLIFICAÇÃO", 12f, muted, true))
        gainLabel = text("0%", 42f, accent, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, dp(4))
        }
        boostCard.addView(gainLabel)
        boostCard.addView(text("Ajuste gradual do ganho de áudio", 12f, muted).apply {
            gravity = Gravity.CENTER
        })
        gainSlider = SeekBar(this).apply {
            max = 100
            progress = 0
            progressTintList = android.content.res.ColorStateList.valueOf(accent)
            thumbTintList = android.content.res.ColorStateList.valueOf(accent)
        }
        boostCard.addView(gainSlider)
        val bounds = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(text("0%", 11f, muted), LinearLayout.LayoutParams(0, -2, 1f))
            addView(text("100%", 11f, muted).apply { gravity = Gravity.END })
        }
        boostCard.addView(bounds)
        content.addView(boostCard)
        content.addView(space(16))

        toggle = Button(this).apply {
            text = "ATIVAR AMPLIFICADOR"
            isAllCaps = false
            setTextColor(bg)
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            background = rounded(accent, 16)
            setPadding(0, dp(8), 0, dp(8))
            setOnClickListener { setEnabledState(!enabled) }
        }
        content.addView(toggle, LinearLayout.LayoutParams(-1, dp(56)))
        status = text("Desativado", 12f, muted).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(18))
        }
        content.addView(status)

        val eqCard = card()
        eqCard.addView(text("EQUALIZADOR", 12f, muted, true))
        eqCard.addView(text("Ajuste as bandas disponíveis no aparelho", 12f, muted).apply {
            setPadding(0, dp(5), 0, dp(12))
        })
        eqContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        eqCard.addView(eqContainer)
        content.addView(eqCard)
        buildEqualizerControls()

        content.addView(space(18))
        content.addView(text(
            "A amplificação real depende do aparelho, da versão do Android e do caminho de áudio. Ganho excessivo pode causar distorção e risco auditivo. Use volume moderado.",
            11f, muted
        ))
    }

    private fun buildEqualizerControls() {
        eqContainer.removeAllViews()
        val eq = equalizer
        if (eq == null) {
            eqContainer.addView(text("Equalizador não suportado neste dispositivo.", 12f, muted))
            return
        }
        try {
            val range = eq.bandLevelRange
            for (band in 0 until eq.numberOfBands) {
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, dp(5), 0, dp(5))
                }
                val hz = eq.getCenterFreq(band.toShort()) / 1000
                val title = if (hz >= 1000) "${hz / 1000f} kHz" else "${hz} Hz"
                row.addView(text(title, 12f, Color.WHITE, true))
                val seek = SeekBar(this).apply {
                    max = (range[1] - range[0]).toInt()
                    progress = (-range[0]).toInt()
                    progressTintList = android.content.res.ColorStateList.valueOf(accent)
                    thumbTintList = android.content.res.ColorStateList.valueOf(accent)
                    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                            if (fromUser) try {
                                eq.setBandLevel(band.toShort(), (p + range[0]).toShort())
                            } catch (_: Exception) {}
                        }
                        override fun onStartTrackingTouch(s: SeekBar?) {}
                        override fun onStopTrackingTouch(s: SeekBar?) {}
                    })
                }
                row.addView(seek)
                eqContainer.addView(row)
            }
        } catch (_: Exception) {
            eqContainer.addView(text("Não foi possível carregar as bandas do equalizador.", 12f, muted))
        }
    }

    private fun setEnabledState(value: Boolean) {
        enabled = value
        try {
            enhancer?.setTargetGain(if (value) gainMb else 0)
            enhancer?.enabled = value
            equalizer?.enabled = value
        } catch (_: Exception) {
            status.text = "O aparelho não permitiu aplicar o efeito"
        }
        renderState()
    }

    private fun renderState() {
        gainLabel.text = "${gainSlider.progress}%"
        toggle.text = if (enabled) "DESATIVAR AMPLIFICADOR" else "ATIVAR AMPLIFICADOR"
        toggle.background = rounded(if (enabled) Color.rgb(255, 190, 90) else accent, 16)
        status.text = if (enabled) "Amplificador ativado • efeito sujeito ao suporte do aparelho" else "Desativado"
        gainMb = (gainSlider.progress * 10).coerceIn(0, 1000)
    }

    private fun card() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(18), dp(18), dp(18), dp(18))
        background = rounded(panel, 22)
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false) =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }

    private fun space(height: Int) = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(height))
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()

    override fun onDestroy() {
        try { enhancer?.release() } catch (_: Exception) {}
        try { equalizer?.release() } catch (_: Exception) {}
        enhancer = null
        equalizer = null
        super.onDestroy()
    }
}
