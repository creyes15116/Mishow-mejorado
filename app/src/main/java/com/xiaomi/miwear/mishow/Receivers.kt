package com.xiaomi.miwear.mishow
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlin.concurrent.thread

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        val pr = goAsync()
        thread {
            try {
                when (i.action) {
                    Scheduler.JUMP -> Actions.jump(c)
                    Scheduler.RESTORE -> Actions.restore(c)
                }
            } finally { pr.finish() }
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        Store.log(c, "Arranque (${i.action?.substringAfterLast('.')})")
        if (Store.pending(c)) Scheduler.scheduleRestore(c, 10_000) else Scheduler.scheduleNextJump(c) // activa por defecto
    }
}
