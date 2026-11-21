package com.xiaomi.miwear.mishow
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object Store {
    private fun p(c: Context) = c.getSharedPreferences("mishow", 0)
    fun origTz(c: Context) = p(c).getString("origTz", "")!!
    fun setOrigTz(c: Context, v: String) = p(c).edit().putString("origTz", v).apply()
    fun autoFlags(c: Context) = p(c).getString("autoFlags", "0,0")!!
    fun setAutoFlags(c: Context, v: String) = p(c).edit().putString("autoFlags", v).apply()
    fun enabled(c: Context) = p(c).getBoolean("enabled", true)
    fun setEnabled(c: Context, v: Boolean) = p(c).edit().putBoolean("enabled", v).apply()
    fun pending(c: Context) = p(c).getBoolean("pending", false)
    fun setPending(c: Context, v: Boolean) = p(c).edit().putBoolean("pending", v).apply()
    fun log(c: Context, msg: String) {
        val t = SimpleDateFormat("dd HH:mm:ss", Locale.US).format(Date())
        val l = (p(c).getString("log", "")!!.lines().filter { it.isNotBlank() } + "$t $msg").takeLast(40)
        p(c).edit().putString("log", l.joinToString("\n")).apply()
    }
    fun logText(c: Context) = p(c).getString("log", "")!!
}
