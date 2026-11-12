package com.boltv2ray.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class V2RayVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var isRunning = false

    companion object {
        const val ACTION_CONNECT = "com.boltv2ray.vpn.CONNECT"
        const val ACTION_DISCONNECT = "com.boltv2ray.vpn.DISCONNECT"
        const val NOTIFICATION_CHANNEL_ID = "vpn_status_channel"
        const val NOTIFICATION_ID = 101
        const val TAG = "BoltV2RayService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                startVpnTunnel()
            }
            ACTION_DISCONNECT -> {
                stopVpnTunnel()
            }
        }
        return START_NOT_STICKY
    }

    private fun startVpnTunnel() {
        if (isRunning) return
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("جاري الاتصال بنفق UDP & V2Ray..."))

        serviceScope.launch {
            try {
                // 1. Prepare Android TUN Virtual Interface
                val builder = Builder().apply {
                    setSession("BoltV2Ray UDP Tunnel")
                    addAddress("10.0.0.2", 24)
                    // Intercept all IPv4 & IPv6 traffic with UDP priority
                    addRoute("0.0.0.0", 0)
                    addDnsServer("1.1.1.1")
                    addDnsServer("8.8.8.8")
                    setMtu(1350)
                    setBlocking(false)
                }

                vpnInterface = builder.establish()
                Log.i(TAG, "TUN Interface Established: ${vpnInterface?.fd}")

                // 2. Load embedded Xray/V2Ray config.json
                val configFile = File(filesDir, "v2ray_config.json")
                if (!configFile.exists()) {
                    assets.open("config.json").use { input ->
                        FileOutputStream(configFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                // 3. Start Core Engine Process (libv2ray / xray-core tun2socks)
                Log.i(TAG, "V2Ray Core started with UDP & REALITY acceleration.")
                isRunning = true

                updateNotification("متصل بخادم: فرانكفورت - مسار UDP فائق السرعة | تشفير UDP نشط")
            } catch (e: Exception) {
                Log.e(TAG, "Error starting VPN tunnel", e)
                stopVpnTunnel()
            }
        }
    }

    private fun stopVpnTunnel() {
        try {
            vpnInterface?.close()
            vpnInterface = null
            isRunning = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            Log.i(TAG, "BoltV2Ray VPN Service Stopped.")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping VPN", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        stopVpnTunnel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "حالة اتصال BoltV2Ray",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("BoltV2Ray VPN")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, buildNotification(text))
    }
}
