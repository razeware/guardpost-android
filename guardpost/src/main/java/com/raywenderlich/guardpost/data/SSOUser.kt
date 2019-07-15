package com.raywenderlich.guardpost.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Helper object for logged in User details
 */
@Parcelize
data class SSOUser(
    val externalId: String? = null,
    val email: String? = null,
    val username: String? = null,
    val avatarUrl: String? = null,
    val name: String? = null,
    val token: String? = null,
    val loggedIn: Boolean = false
) : Parcelable {

  constructor(responseUri: Uri) : this(
      responseUri.getQueryParameter("external_id"),
      responseUri.getQueryParameter("email"),
      responseUri.getQueryParameter("username"),
      responseUri.getQueryParameter("avatar_url"),
      responseUri.getQueryParameter("name")?.replace('+', ' ')?.trim(),
      responseUri.getQueryParameter("token"),
      true
  )
}
