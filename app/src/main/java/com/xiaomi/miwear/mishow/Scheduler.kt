package com.xiaomi.miwear.mishow
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import java.util.Calendar

object Scheduler {
    const val JUMP = "com.xiaomi.miwear.mishow.JUMP"
    const val RESTORE = "com.xiaomi.miwear.mishow.RESTORE"

    fun at(h: Int, m: Int, s: Int, base: Long = System.currentTimeMillis()): Long =
        Calendar.getInstance().apply {
            timeInMillis = base
            set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, m); set(Calendar.SECOND, s); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun pi(c: Context, action: String, code: Int) = PendingIntent.getBroadcast(
        c, code, Intent(c, AlarmReceiver::class.java).setAction(action),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    /** delayMs = milisegundos REALES desde ahora. Se usa ELAPSED_REALTIME para que saltos de hora no la muevan. */
    private fun setIn(c: Context, action: String, code: Int, delayMs: Long) {
        val am = c.getSystemService(AlarmManager::class.java)
        val trigger = SystemClock.elapsedRealtime() + maxOf(delayMs, 1000L)
        if (am.canScheduleExactAlarms())
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, pi(c, action, code))
        else
            am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, pi(c, action, code))
    }

    /** Próximo salto: 07:57:00 hora local. Solo llamar cuando la hora es la real. */
    fun scheduleNextJump(c: Context) {
        if (!Store.enabled(c)) return
        val now = System.currentTimeMillis()
        var t = at(7, 57, 0)
        if (now >= t - 20_000) t += 24L * 3600_000
        setIn(c, Scheduler.JUMP, 1, t - now)
        Store.log(c, "Próximo salto en ${(t - now) / 60000} min")
    }

    fun isScheduled(c: Context) = PendingIntent.getBroadcast(
        c, 1, Intent(c, AlarmReceiver::class.java).setAction(JUMP),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE) != null

    /** Se llama desde cualquier punto de entrada: arma el salto si falta, sin duplicar. */
    fun ensure(c: Context) {
        if (Store.enabled(c) && !Store.pending(c) && !isScheduled(c)) scheduleNextJump(c)
    }

    fun scheduleRestore(c: Context, delayMs: Long) = setIn(c, RESTORE, 2, delayMs)
    fun cancelAll(c: Context) {
        val am = c.getSystemService(AlarmManager::class.java)
        am.cancel(pi(c, JUMP, 1)); am.cancel(pi(c, RESTORE, 2))
    }
}
