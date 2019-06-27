package com.raywenderlich.guardpost.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Helper object for logged in User details
 */
@Parcelize
data class SSOUser(
    val externalId: String?,
    val email: String?,
    val username: String?,
    val avatarUrl: String?,
    val name: String?,
    val token: String?
) : Parcelable {

  constructor(responseUri: Uri) : this(
      responseUri.getQueryParameter("external_id"),
      responseUri.getQueryParameter("email"),
      responseUri.getQueryParameter("username"),
      responseUri.getQueryParameter("avatar_url"),
      responseUri.getQueryParameter("name")?.replace('+', ' ')?.trim(),
      responseUri.getQueryParameter("token")
  )
}
