package com.raywenderlich.guardpost

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.raywenderlich.guardpost.utils.randomString
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val clientApiKey = getString(R.string.client_api_key)
    val nonce = randomString()

    login.setOnClickListener {
      startActivityForResult(
          GuardpostAuth.getLoginIntent(
              this@MainActivity,
              nonce, clientApiKey
          ), 1
      )
    }

    logout.setOnClickListener {
      startActivityForResult(
          GuardpostAuth.getLogoutIntent(
              this@MainActivity
          ), 1
      )
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
      data?.let {
        textView.text = GuardpostAuth.getUser(it).toString()
      }
    }
  }
}
