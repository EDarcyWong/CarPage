package com.cdh.carpage.shared

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.cdh.carpage.MainActivity

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {

            Log.d("BootReceiver", "BOOT_COMPLETED received")

            // 延迟 3 秒启动 MainActivity，确保系统组件可用
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    val activityIntent = Intent(context, MainActivity::class.java)
                    activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(activityIntent)

                    Log.d("BootReceiver", "MainActivity started")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Failed to start MainActivity", e)
                }
            }, 3000)
        }
    }
}
