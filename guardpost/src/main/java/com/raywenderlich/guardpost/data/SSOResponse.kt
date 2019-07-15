package com.raywenderlich.guardpost.data

import android.net.Uri
import com.raywenderlich.guardpost.utils.fromBase64
import com.raywenderlich.guardpost.utils.toHmacSha256

/**
 * Helper object for working with SSO response
 */
internal data class SSOResponse(
  private val responseUri: Uri?,
  private val sso: String? = responseUri?.getQueryParameter("sso"),
  private val sig: String? = responseUri?.getQueryParameter("sig")
) {

  private lateinit var resultUri: Uri

  /**
   * Validates the response
   *
   * @param secret Client secret key
   * @param nonce  Previous sent nonce
   *
   * @return True if response is valid False otherwise.
   */
  fun isValid(secret: String, nonce: String): Boolean {

    // 1 Check if signature and sso parameters are present
    if (null == sig || null == sso) {
      return false
    }

    // 2 Check if signature matches HmacSha256 of sso parameter
    if (sso.toHmacSha256(secret) != sig) {
      return false
    }

    val urlResponse = sso.fromBase64()

    resultUri = Uri.Builder().encodedQuery(urlResponse).build() ?: return false

    val receivedNonce = resultUri.getQueryParameter("nonce")

    // 3 Check if nonce is correct
    if (receivedNonce != nonce) {
      return false
    }

    return true
  }

  /**
   * Creates and returns the logged in User
   *
   * @return SSOUser object from response uri
   */
  fun getUser(): SSOUser = SSOUser(resultUri)
}
