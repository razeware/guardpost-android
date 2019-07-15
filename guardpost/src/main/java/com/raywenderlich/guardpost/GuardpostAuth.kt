package com.raywenderlich.guardpost

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.raywenderlich.guardpost.data.SSORequest
import com.raywenderlich.guardpost.data.SSOResponse
import com.raywenderlich.guardpost.data.SSOUser

object GuardpostAuth {
  private var nonce: String = ""
  private var clientApiKey: String = ""

  fun startLogout(context: Context) {
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

  private fun launchCustomTab(context: Context, uri: Uri) {
    val builder = CustomTabsIntent.Builder()
    builder.setShowTitle(true)
    builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
    builder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right)
    builder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
    val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_material_icon_arrow_back)
    drawable?.let {
      builder.setCloseButtonIcon(drawable.toBitmap())
    }
    val customTabsIntent = builder.build()
    customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    customTabsIntent.launchUrl(context, uri)
  }

  private fun getRedirectUrl(context: Context): String {
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

  fun startLogin(context: Context, clientApiKey: String, nonce: String): Boolean {

    if (clientApiKey.isBlank() || nonce.isBlank()) {
      return false
    }

    this.clientApiKey = clientApiKey
    this.nonce = nonce

    val baseEndpoint = context.getString(R.string.base_url)
    val redirectUrl = getRedirectUrl(context)

    val loginPath = context.getString(R.string.path_login)
    val ssoRequest = SSORequest(
      "$baseEndpoint$loginPath",
      redirectUrl,
      nonce
    )

    val loginUri: Uri = ssoRequest.buildLoginUri(clientApiKey)
      ?: return false

    launchCustomTab(context, loginUri)
    return true
  }

  internal fun getSignedInUser(uri: Uri): SSOUser? {
    val ssoResponse = SSOResponse(uri)

    if (!ssoResponse.isValid(clientApiKey, nonce)) {
      return null
    }

    return ssoResponse.getUser()
  }

  /**
   * Auth Error Constants
   */
  internal object AuthErrors {

    /**
     * API has sent an invalid response
     */
    const val INVALID_RESPONSE: Int = 5
  }

  /**
   * Auth Error Constants
   */
  object BroadcastActions {

    /**
     * Login success action
     */
    internal const val LOGIN_SUCCESS: String = "action_login_success"

    /**
     * Login failure action
     */
    internal const val LOGIN_FAILURE: String = "action_login_failure"

    /**
     * Logout action
     */
    internal const val LOGOUT_SUCCESS: String = "action_logout_success"

    internal const val FAILURE: String = "action_failure"

    val INTENT_FILTER: IntentFilter = IntentFilter().apply {
      addAction(LOGIN_SUCCESS)
      addAction(LOGIN_FAILURE)
      addAction(LOGOUT_SUCCESS)
      addAction(FAILURE)
    }
  }
}
