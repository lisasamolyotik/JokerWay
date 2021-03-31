package com.example.jokerway.ui

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerLibCore
import com.example.jokerway.R
import com.example.jokerway.api.ApiService
import com.example.jokerway.api.data.ApiData
import com.example.jokerway.api.data.PushClickApiData
import com.example.jokerway.databinding.ActivityMainBinding
import com.example.jokerway.model.Url
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val linkKey = "link"
    private val organicKey = "organic"
    private val os = "Android"
    private val appBundle = "com.example.jokerway"
    private val afKey = "KDnjABKKYvMH9FSEdHAuCd"
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.fragment_container_view)
            }
        }

        startAppsFlyer()
        val appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(this)
        Log.d(tag, appsFlyerId)

        val token = getToken()
        Log.d(tag, "firebase device token: $token")

        val remoteConfig = Firebase.remoteConfig
        fetchFirebaseRemoteConfig(remoteConfig)
        addWebView(createUrl(remoteConfig, token, appsFlyerId))

        val locale = resources.configuration.locales[0]
        val apiData = ApiData(appBundle, locale.toString(), token, appsFlyerId, os)
        Log.d(tag, "apiData: $apiData")
        val apiService = ApiService()
        apiService.postData(apiData) {
            if (it != null) {
                Log.d(tag, "Successful post request, apiData: $it")
            } else {
                Log.d(tag, "Post request failed")
            }
        }

        /*if (intent != null) {
            Log.d(tag, "Activity started from a notification")
            val pushClickApiData = PushClickApiData(appBundle, appsFlyerId, os)
            Log.d(tag, "pushClickApiData: $pushClickApiData")
            apiService.postPushClickData(pushClickApiData) {
                if (it != null) {
                    Log.d(tag, "Successful post request, apiData: $it")
                } else {
                    Log.d(tag, "Post request failed")
                }
            }
        }*/
    }

    private fun startAppsFlyer() {
        val conversionDataListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                data?.let { cvData ->
                    cvData.map {
                        Log.i(
                            AppsFlyerLibCore.LOG_TAG,
                            "conversion_attribute:  ${it.key} = ${it.value}"
                        )
                    }
                }
            }

            override fun onConversionDataFail(error: String?) {
                Log.e(AppsFlyerLibCore.LOG_TAG, "error onAttributionFailure :  $error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    Log.d(AppsFlyerLibCore.LOG_TAG, "onAppOpen_attribute: ${it.key} = ${it.value}")
                }
            }

            override fun onAttributionFailure(error: String?) {
                Log.e(AppsFlyerLibCore.LOG_TAG, "error onAttributionFailure :  $error")
            }
        }

        AppsFlyerLib.getInstance().init(afKey, conversionDataListener, this)
        AppsFlyerLib.getInstance().start(this)
    }

    private fun getToken(): String {
        var token: String? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(tag, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            token = task.result
        })
        return if (token != null) token!!
        else "nodata"
    }

    private fun fetchFirebaseRemoteConfig(remoteConfig: FirebaseRemoteConfig) {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(tag, "Config params updated: $updated")
                }
            }
    }

    private fun createUrl(
        remoteConfig: FirebaseRemoteConfig,
        token: String,
        appsFlyerId: String
    ): String {
        val link = remoteConfig.getString(linkKey)
        val organic = remoteConfig.getString(organicKey)
        Log.d(tag, "Link from remoteConfig: $link")
        Log.d(tag, "Organic value from remoteConfig: $organic")
        return Url(baseLink = link, pushToken = token, afId = appsFlyerId).getLink(organic)
    }

    private fun addWebView(url: String) {
        val webView = WebView(this)
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        webView.settings.domStorageEnabled = true
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
