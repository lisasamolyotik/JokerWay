package com.example.jokerway.viewodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainViewModel : ViewModel() {
    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token
    private val tag = "MainViewModel"

    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(tag, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            _token.value = task.result
            Log.d(tag, "token: ${token.value}")
        })
    }
}