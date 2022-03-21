package com.raywenderlich.guardpost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.raywenderlich.guardpost.databinding.ActivityMainBinding
import com.raywenderlich.guardpost.utils.randomString

class MainActivity : AppCompatActivity() {
  private lateinit var activityMainBinding: ActivityMainBinding

  private val localBroadcastManager by lazy(LazyThreadSafetyMode.NONE) {
    LocalBroadcastManager.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    val view = activityMainBinding.root
    setContentView(view)

    val clientApiKey = getString(R.string.client_api_key)
    val nonce = randomString()

    activityMainBinding.login.setOnClickListener {
      GuardpostAuth.startLogin(this, clientApiKey, nonce)
    }

    activityMainBinding.logout.setOnClickListener {
      GuardpostAuth.startLogout(this)
    }

    val guardpostAuthReceiver = GuardpostAuthReceiver()

    guardpostAuthReceiver.login.observe(this) {
      FirebaseCrashlytics.getInstance().recordException(Throwable(it.toString()))
      activityMainBinding.textView.text = it.toString()
    }

    guardpostAuthReceiver.error.observe(this) {
      activityMainBinding.textView.text = it.toString()
      FirebaseCrashlytics.getInstance().recordException(Throwable("Error: $it"))
    }

    localBroadcastManager.registerReceiver(
      guardpostAuthReceiver,
      GuardpostAuth.BroadcastActions.INTENT_FILTER
    )
  }
}
