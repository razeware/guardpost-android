package com.raywenderlich.guardpost.data

import android.net.Uri
import com.raywenderlich.guardpost.utils.randomString
import com.raywenderlich.guardpost.utils.toBase64
import com.raywenderlich.guardpost.utils.toHmacSha256

/**
 * Helper object for creating SSO request
 */
internal data class SSORequest(
    private val endpoint: String,
    private val callbackUrl: String,
    private val nonce: String = randomString()
) {

  /**
   * Builds login URI
   *
   * @param secret Client secret key
   *
   * @return Generated URI or null
   */
  fun buildLoginUri(secret: String): Uri? {
    val unsignedPayload = buildUnSignedPayload() ?: return null

    return Uri.Builder().path(endpoint)
        .scheme("https")
        .appendQueryParameter("sso", unsignedPayload.toBase64())
        .appendQueryParameter("sig", unsignedPayload.toBase64().toHmacSha256(secret))
        .build()
  }

  /**
   * Builds logout URI
   *
   * @return Generated URI or null
   */
  fun buildLogoutUri(): Uri? {
    return Uri.Builder().path(endpoint)
        .scheme("https")
        .appendQueryParameter("redirect_uri", callbackUrl)
        .build()
  }

  private fun buildUnSignedPayload(): String? = Uri.Builder()
      .appendQueryParameter("callback_url", callbackUrl)
      .appendQueryParameter("nonce", nonce)
      .build()
      .query
}
