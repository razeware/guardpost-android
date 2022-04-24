package com.raywenderlich.guardpost

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.raywenderlich.guardpost.data.SSOUser

@SuppressLint("GoogleAppIndexingApiWarning")
internal class RedirectActivity : AppCompatActivity() {

  companion object {
    internal const val EXTRA_RESULT: String = "extra_user"
  }

  private val localBroadcastManager by lazy(LazyThreadSafetyMode.NONE) {
    LocalBroadcastManager.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_redirect)
    getResultIntent(intent)
    FirebaseCrashlytics.getInstance()
      .recordException(Throwable("OnCreate Intent: ${intent.extras}"))
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    FirebaseCrashlytics.getInstance().recordException(Throwable("New Intent: ${intent?.extras}"))
    setIntent(intent)
    getResultIntent(intent)
  }

  private fun getResultIntent(newIntent: Intent?) {
    var resultIntent = Intent()

    val sendFailureResult = { intent: Intent ->
      intent.action = GuardpostAuth.BroadcastActions.FAILURE
      localBroadcastManager.sendBroadcast(intent)
    }

    if (newIntent == null) {
      sendFailureResult(resultIntent)
      return
    }

    val intentData = newIntent.data

    if (intentData == null) {
      sendFailureResult(resultIntent)
      return
    }

    if (GuardpostAuth.didLogout(this, intentData)) {
      resultIntent = handleLogoutResult()
    }

    if (GuardpostAuth.didLogin(this, intentData)) {
      FirebaseCrashlytics.getInstance().recordException(Throwable("Success Login: $intentData"))
      resultIntent = handleLoginResult(intentData)
      FirebaseCrashlytics.getInstance().recordException(Throwable("Login Result: $resultIntent"))
    }

    localBroadcastManager.sendBroadcast(resultIntent)
    finish()
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
  }

  private fun handleLogoutResult(): Intent {
    return Intent().apply {
      action = GuardpostAuth.BroadcastActions.LOGOUT_SUCCESS
      putExtra(EXTRA_RESULT, SSOUser())
    }
  }

  private fun handleLoginResult(data: Uri): Intent {
    val resultIntent = Intent().apply {
      action = GuardpostAuth.BroadcastActions.LOGIN_SUCCESS
    }
    val result = GuardpostAuth.getSignedInUser(data)

    if (null == result) {
      resultIntent.action = GuardpostAuth.BroadcastActions.LOGIN_FAILURE
    } else {
      resultIntent.putExtra(EXTRA_RESULT, result)
    }
    return resultIntent
  }
}
