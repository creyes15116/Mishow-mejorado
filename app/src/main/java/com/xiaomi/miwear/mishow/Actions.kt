package com.xiaomi.miwear.mishow
import android.app.AlarmManager
import android.content.Context
import android.provider.Settings
import java.util.TimeZone

/** Salta de zona horaria (no de hora): no necesita shell ni depuración. */
object Actions {
    const val JUMP_TZ = "Etc/GMT+3" // UTC-3: la hora local avanza 3 h respecto a Costa Rica (UTC-6)

    private fun autoFlag(c: Context, key: String) = try { Settings.Global.getInt(c.contentResolver, key, 0) } catch (e: Exception) { 0 }

    /** Aplica el salto y programa la vuelta en restoreInMs (tiempo real). */
    fun applyJump(c: Context, restoreInMs: Long) {
        if (!Store.pending(c)) {
            Store.setOrigTz(c, TimeZone.getDefault().id)
            Store.setAutoFlags(c, "${autoFlag(c, "auto_time_zone")},${autoFlag(c, "clockwork_auto_time_zone")}")
        }
        try {
            Settings.Global.putInt(c.contentResolver, "auto_time_zone", 0)
            Settings.Global.putInt(c.contentResolver, "clockwork_auto_time_zone", 0)
        } catch (e: Exception) { Store.log(c, "No pude apagar auto zona: ${e.javaClass.simpleName}") }
        c.getSystemService(AlarmManager::class.java).setTimeZone(JUMP_TZ)
        Store.setPending(c, true)
        Scheduler.scheduleRestore(c, restoreInMs)
        Thread.sleep(2000)
        TimeZone.setDefault(null)
        Store.log(c, "Zona -> ${TimeZone.getDefault().id} (pedí $JUMP_TZ)")
    }

    /** 07:57 real: salto. Se calcula la vuelta (08:10 reales) ANTES de cambiar la zona. */
    fun jump(c: Context) {
        val now = System.currentTimeMillis()
        if (now !in Scheduler.at(7, 50, 0)..Scheduler.at(7, 59, 40)) {
            Store.log(c, "Salto omitido: fuera de 07:50-07:59:40"); Scheduler.scheduleNextJump(c); return
        }
        val delay = Scheduler.at(8, 10, 0) - now
        try { applyJump(c, delay) } catch (e: Exception) {
            Store.log(c, "Salto falló: ${e.javaClass.simpleName} ${e.message}"); Scheduler.scheduleNextJump(c)
        }
    }

    fun restore(c: Context) {
        if (!Store.pending(c)) { Scheduler.scheduleNextJump(c); return }
        try {
            val orig = Store.origTz(c).ifEmpty { "America/Costa_Rica" }
            c.getSystemService(AlarmManager::class.java).setTimeZone(orig)
            val f = Store.autoFlags(c).split(",")
            try {
                Settings.Global.putInt(c.contentResolver, "auto_time_zone", f[0].toInt())
                Settings.Global.putInt(c.contentResolver, "clockwork_auto_time_zone", f[1].toInt())
            } catch (e: Exception) {}
            Store.setPending(c, false)
            Thread.sleep(2000); TimeZone.setDefault(null)
            Store.log(c, "Zona restaurada -> ${TimeZone.getDefault().id}")
            Scheduler.scheduleNextJump(c)
        } catch (e: Exception) {
            Store.log(c, "Restaurar falló (reintento 60s): ${e.javaClass.simpleName}")
            Scheduler.scheduleRestore(c, 60_000)
        }
    }
}
