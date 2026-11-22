package com.divora.music.ui.tv

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration

/**
 * Divora TV (1.8.0): whether this device is a TV (Android TV / Google TV). One APK serves both: on a TV, Divora opens
 * [com.divora.music.TvActivity] and its own screens (`ui/tv`), never the phone's.
 */
object TvDevice {
    fun isTv(context: Context): Boolean =
        context.getSystemService(UiModeManager::class.java)?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}
