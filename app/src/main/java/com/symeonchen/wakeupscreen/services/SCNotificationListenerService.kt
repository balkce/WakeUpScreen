package com.symeonchen.wakeupscreen.services

import android.os.PowerManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.blankj.utilcode.util.LogUtils
import com.symeonchen.wakeupscreen.utils.DataInjection

@Suppress("DEPRECATION")
class SCNotificationListenerService : NotificationListenerService() {

    companion object {
        private const val TAG_WAKE = "symeonchen:wakeupscreen"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        LogUtils.d("Received notification from: " + sbn?.packageName)
        val status = DataInjection.getSwitchOfCustom()
        if (!status) {
            return
        }
        if (sbn?.packageName == "com.android.systemui") {
            return
        }
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (pm.isInteractive) {
            return
        }

        val proximityStatus = DataInjection.getStatueOfProximity()
        val proximitySwitch = DataInjection.getSwitchOfProximity()

        if (proximitySwitch && proximityStatus == 0) {
            return
        }

        val wl = pm.newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
            TAG_WAKE
        )
        val sec = DataInjection.getSecondOfWakeUpScreen()
        wl.acquire((sec * 1000).toLong())
        wl.release()

    }

}