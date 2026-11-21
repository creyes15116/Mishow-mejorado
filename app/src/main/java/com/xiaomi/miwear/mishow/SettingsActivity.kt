package com.xiaomi.miwear.mishow
import android.app.Activity
import android.os.Bundle
import android.widget.*
import kotlin.concurrent.thread

class SettingsActivity : Activity() {
    private lateinit var status: TextView
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 40, 40, 60) }
        status = TextView(this).apply { textSize = 11f }
        fun btn(t: String, f: () -> Unit) = Button(this).apply { text = t; setOnClickListener { f() } }.also { col.addView(it) }
        col.addView(TextView(this).apply { text = "MiShow Guard"; textSize = 16f })
        btn("1. Probar cambio de zona (10 s)") { bg { Actions.applyJump(this, 10_000) } }
        btn("2. Activar guardia") { Store.setEnabled(this, true); Scheduler.scheduleNextJump(this); refresh() }
        btn("Desactivar guardia") { Store.setEnabled(this, false); Scheduler.cancelAll(this); refresh() }
        btn("Restaurar zona ahora") { bg { Actions.restore(this) } }
        col.addView(status)
        setContentView(ScrollView(this).apply { addView(col) })
        Scheduler.ensure(this)
        refresh()
    }
    private fun bg(f: () -> Unit) = thread {
        try { f() } catch (e: Exception) { Store.log(this, "ERROR ${e.javaClass.simpleName}: ${e.message}") }
        runOnUiThread { refresh() }
    }
    private fun refresh() {
        status.text = "Guardia: ${if (Store.enabled(this)) "ACTIVA" else "apagada"}  pendiente: ${Store.pending(this)}\n${Store.logText(this)}"
    }
}
