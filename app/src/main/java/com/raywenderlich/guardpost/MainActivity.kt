package com.raywenderlich.guardpost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.raywenderlich.guardpost.utils.randomString
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private val localBroadcastManager by lazy(LazyThreadSafetyMode.NONE) {
    LocalBroadcastManager.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val clientApiKey = getString(R.string.client_api_key)
    val nonce = randomString()

    login.setOnClickListener {
      GuardpostAuth.startLogin(this, clientApiKey, nonce)
    }

    logout.setOnClickListener {
      GuardpostAuth.startLogout(this)
    }

    val guardpostAuthReceiver = GuardpostAuthReceiver()

    guardpostAuthReceiver.login.observe(this, Observer {
      textView.text = it.toString()
    })

    localBroadcastManager.registerReceiver(
      guardpostAuthReceiver,
      GuardpostAuth.BroadcastActions.INTENT_FILTER
    )
  }
}
