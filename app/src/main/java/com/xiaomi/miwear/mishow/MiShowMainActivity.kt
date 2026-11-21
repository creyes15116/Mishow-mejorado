package com.xiaomi.miwear.mishow
import android.app.Activity
import android.os.Bundle

/** El sistema (MiShowModule) lo lanza en cada pantalla apagada: aprovechamos para rearmar el salto si falta. */
class MiShowMainActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        try { Scheduler.ensure(this) } catch (e: Exception) { Store.log(this, "ensure: ${e.javaClass.simpleName}") }
        finish()
    }
}
