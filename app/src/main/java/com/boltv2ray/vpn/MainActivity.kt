package com.boltv2ray.vpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isConnected = false

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startVpnService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToggle = findViewById<Button>(R.id.btnToggle)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        btnToggle.setOnClickListener {
            if (!isConnected) {
                val intent = VpnService.prepare(this)
                if (intent != null) {
                    vpnPermissionLauncher.launch(intent)
                } else {
                    startVpnService()
                }
            } else {
                stopVpnService()
            }
        }
    }

    private fun startVpnService() {
        val intent = Intent(this, V2RayVpnService::class.java).apply {
            action = V2RayVpnService.ACTION_CONNECT
        }
        startService(intent)
        isConnected = true
        findViewById<TextView>(R.id.tvStatus).text = "متصل بنفق UDP و V2Ray المشفر"
        findViewById<Button>(R.id.btnToggle).text = "قطع الاتصال"
    }

    private fun stopVpnService() {
        val intent = Intent(this, V2RayVpnService::class.java).apply {
            action = V2RayVpnService.ACTION_DISCONNECT
        }
        startService(intent)
        isConnected = false
        findViewById<TextView>(R.id.tvStatus).text = "غير متصل"
        findViewById<Button>(R.id.btnToggle).text = "اتصال بالـ VPN"
    }
}
