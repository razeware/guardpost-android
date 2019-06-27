package com.raywenderlich.guardpost

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.raywenderlich.guardpost.RedirectActivity.Companion.EXTRA_CLIENT_API_KEY
import com.raywenderlich.guardpost.RedirectActivity.Companion.EXTRA_NONCE
import com.raywenderlich.guardpost.data.Result
import com.raywenderlich.guardpost.data.SSORequest
import com.raywenderlich.guardpost.data.SSOResponse
import com.raywenderlich.guardpost.data.SSOUser

class GuardpostAuth(
  private val nonce: String,
  private val clientApiKey: String
) {

  companion object {

    /**
     * Creates intent to start login flow.
     *
     * Implement [Activity.onActivityResult] to handle success or failure
     *
     * @param context Current context
     * @param nonce Random String
     * @param clientApiKey Client API Secret for encryption
     *
     * @return [Intent] to start the login flow or null if provided parameters were incorrect
     */
    fun getLoginIntent(context: Activity, nonce: String, clientApiKey: String): Intent? {
      if (nonce.isEmpty() || clientApiKey.isEmpty()) {
        return null
      }

      return Intent(context, RedirectActivity::class.java).apply {
        action = RedirectActivity.ACTION_LOGIN
        putExtra(EXTRA_NONCE, nonce)
        putExtra(EXTRA_CLIENT_API_KEY, clientApiKey)
      }
    }

    /**
     * Creates intent to start logout flow.
     *
     * Implement [Activity.onActivityResult] to handle success or failure
     *
     * @param context Current context
     *
     * @return [Intent] to start the logout flow or null if provided parameters were incorrect
     */
    fun getLogoutIntent(context: Activity): Intent {
      return Intent(context, RedirectActivity::class.java).apply {
        action = RedirectActivity.ACTION_LOGOUT
      }
    }

    /**
     * Gets logged in user [SSOUser] from Intent
     *
     * @param intent Success Intent from [Activity.onActivityResult]
     *
     * @return Logged in user as [SSOUser]
     */
    fun getUser(intent: Intent): SSOUser =
      intent.getParcelableExtra(RedirectActivity.EXTRA_RESULT)

    /**
     * Gets logged in error code
     *
     * @param intent Success Intent from [Activity.onActivityResult]
     *
     * @return error code [AuthError]
     */
    fun getError(intent: Intent): Int =
      intent.getIntExtra(RedirectActivity.EXTRA_RESULT, AuthError.INVALID_RESPONSE)

    internal fun newInstance(
      nonce: String,
      clientApiKey: String
    ): GuardpostAuth =
      GuardpostAuth(nonce, clientApiKey)

    internal fun startLogout(context: Context) {
      val baseEndpoint = context.getString(R.string.base_url)
      val redirectUrl = getRedirectUrlLogout(context)

      val logoutPath = context.getString(R.string.path_logout)
      val ssoRequest = SSORequest(
        "$baseEndpoint$logoutPath",
        redirectUrl
      )

      val logoutUri: Uri = ssoRequest.buildLogoutUri() ?: return
      launchCustomTab(context, logoutUri)
    }

    internal fun launchCustomTab(context: Context, uri: Uri) {
      val builder = CustomTabsIntent.Builder()
      val customTabsIntent = builder.build()
      customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
      customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      customTabsIntent.launchUrl(context, uri)
    }

    internal fun getRedirectUrl(context: Context): String {
      val redirectScheme = context.getString(R.string.redirect_scheme)
      val redirectHost = context.getString(R.string.redirect_host)
      val redirectPath = context.getString(R.string.redirect_path)
      return "$redirectScheme://$redirectHost$redirectPath"
    }

    private fun getRedirectUrlLogout(context: Context): String {
      val redirectScheme = context.getString(R.string.redirect_scheme)
      val redirectHost = context.getString(R.string.redirect_host)
      val redirectPathLogout = context.getString(R.string.redirect_path_logout)
      return "$redirectScheme://$redirectHost$redirectPathLogout"
    }

    internal fun didLogin(context: Context, data: Uri): Boolean =
      data.toString().contains(getRedirectUrl(context))

    internal fun didLogout(context: Context, data: Uri): Boolean =
      data.toString().contains(getRedirectUrlLogout(context))

  }


  internal fun startLogin(context: Context): Result {

    val baseEndpoint = context.getString(R.string.base_url)
    val redirectUrl = getRedirectUrl(context)

    val loginPath = context.getString(R.string.path_login)
    val ssoRequest = SSORequest(
      "$baseEndpoint$loginPath",
      redirectUrl,
      nonce
    )

    val loginUri: Uri = ssoRequest.buildLoginUri(clientApiKey)
      ?: return Result.Failure(AuthError.UNABLE_TO_CREATE_LOGIN_URL)

    launchCustomTab(context, loginUri)
    return Result.Success(true)
  }

  internal fun getSignedInUser(uri: Uri): Result {
    val ssoResponse = SSOResponse(uri)

    if (!ssoResponse.isValid(clientApiKey, nonce)) {
      return Result.Failure(AuthError.INVALID_RESPONSE)
    }

    return Result.Success(ssoResponse.getUser())
  }

  /**
   * Auth Error Constants
   */
  object AuthError {

    /**
     * Failed to create login url *This should never happen*
     */
    const val UNABLE_TO_CREATE_LOGIN_URL: Int = 1

    /**
     * API has sent an invalid response
     */
    const val INVALID_RESPONSE: Int = 5
  }
}
