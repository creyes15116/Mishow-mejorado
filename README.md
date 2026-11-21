# MiShow Guard (automática, sin menú)
Activa por defecto. A las 07:57 cambia la zona a UTC-3 y a las 08:10 reales vuelve a la tuya.
Se rearma sola: tras cada arranque y en cada pantalla apagada (MiShowModule lanza MiShowMainActivity).

## Instalar
1. adb uninstall com.xiaomi.miwear.mishow ; adb install app-debug.apk
2. adb shell pm grant com.xiaomi.miwear.mishow android.permission.WRITE_SECURE_SETTINGS
3. adb shell cmd deviceidle whitelist +com.xiaomi.miwear.mishow
4. Arrancarla UNA vez (una app recién instalada no recibe eventos hasta abrirse):
   adb shell am start -n com.xiaomi.miwear.mishow/.MiShowMainActivity
Listo. Para ver el estado o probar: adb shell am start -n com.xiaomi.miwear.mishow/.SettingsActivity
