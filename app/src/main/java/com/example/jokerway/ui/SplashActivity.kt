package com.example.jokerway.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.jokerway.R
import java.util.*


class SplashActivity : AppCompatActivity() {
    private val tag = "SplashActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var openedFromNotification = false
        if (intent != null) {
            if (intent.extras?.getString("from", "none")?.toLowerCase(Locale.ROOT) == "notification") {
                Log.d(tag, "Intent is not null")
                openedFromNotification = true
            } else {
                Log.d(tag, "Intent is null")
            }
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(OPENED_FROM_NOTIFICATION, openedFromNotification)
        startActivity(intent)
        finish()
    }

    companion object {
        const val OPENED_FROM_NOTIFICATION = "OPENED_FROM_NOTIFICATION"
    }
}