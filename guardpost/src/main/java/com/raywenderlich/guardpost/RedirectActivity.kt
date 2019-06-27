package com.raywenderlich.guardpost

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.raywenderlich.guardpost.data.Result
import com.raywenderlich.guardpost.data.SSOUser

@SuppressLint("GoogleAppIndexingApiWarning")
internal class RedirectActivity : AppCompatActivity() {

  companion object {
    internal const val ACTION_LOGIN: String = "action_login"
    internal const val ACTION_LOGOUT: String = "action_logout"
    internal const val EXTRA_NONCE: String = "extra_nonce"
    internal const val EXTRA_CLIENT_API_KEY: String = "extra_client_api_key"
    internal const val EXTRA_RESULT: String = "extra_user"
  }

  private var guardPostAuth: GuardpostAuth? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_redirect)

    val nonce = intent.getStringExtra(EXTRA_NONCE)
    val clientApiKey = intent.getStringExtra(EXTRA_CLIENT_API_KEY)

    if (intent?.action == ACTION_LOGIN) {
      guardPostAuth = GuardpostAuth.newInstance(nonce, clientApiKey)
      guardPostAuth?.startLogin(this)
    }

    when (intent?.action) {
      ACTION_LOGIN -> guardPostAuth?.startLogin(this)
      ACTION_LOGOUT -> GuardpostAuth.startLogout(this)
    }
  }

  override fun onNewIntent(newIntent: Intent?) {
    super.onNewIntent(newIntent)

    val resultIntent = Intent()

    val sendFailureResult = { intent: Intent ->
      resultIntent.putExtra(EXTRA_RESULT, GuardpostAuth.AuthError.INVALID_RESPONSE)
      setResult(Activity.RESULT_CANCELED, intent)
      finish()
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
      handleLogoutResult()
    }

    if (GuardpostAuth.didLogin(this, intentData)) {
      handleLoginResult(intentData)
    }
  }

  private fun handleLogoutResult() {
    val resultIntent = Intent().apply {
      putExtra(EXTRA_RESULT, SSOUser())
    }
    setResult(Activity.RESULT_OK, resultIntent)
    finish()
    return
  }

  private fun handleLoginResult(data: Uri) {
    val resultIntent = Intent()
    val result = guardPostAuth?.getSignedInUser(data)

    if (result is Result.Success<*>) {
      resultIntent.putExtra(EXTRA_RESULT, (result.result as SSOUser))
      setResult(Activity.RESULT_OK, resultIntent)
    } else if (result is Result.Failure) {
      resultIntent.putExtra(EXTRA_RESULT, (result.error))
      setResult(Activity.RESULT_CANCELED, resultIntent)
    }

    finish()
  }

  override fun onDestroy() {
    super.onDestroy()
    guardPostAuth = null
  }
}
