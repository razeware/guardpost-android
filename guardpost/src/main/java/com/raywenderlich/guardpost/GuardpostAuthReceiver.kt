package com.raywenderlich.guardpost

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.raywenderlich.guardpost.data.SSOUser

class GuardpostAuthReceiver(
    val onLogin: (SSOUser) -> Unit,
    val onLogout: () -> Unit,
    val onError: (Int) -> Unit
) : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    if (null == intent) {
      onError(GuardpostAuth.AuthErrors.INVALID_RESPONSE)
    }
    when (intent?.action) {
      GuardpostAuth.BroadcastActions.LOGIN_SUCCESS ->
        onLogin(intent.getParcelableExtra(RedirectActivity.EXTRA_RESULT))
      GuardpostAuth.BroadcastActions.LOGIN_FAILURE ->
        onError(GuardpostAuth.AuthErrors.INVALID_RESPONSE)
      GuardpostAuth.BroadcastActions.LOGOUT_SUCCESS ->
        onLogout()
      GuardpostAuth.BroadcastActions.FAILURE ->
        onError(
            intent.getIntExtra(
                RedirectActivity.EXTRA_RESULT,
                GuardpostAuth.AuthErrors.INVALID_RESPONSE
            )
        )
    }
  }
}
