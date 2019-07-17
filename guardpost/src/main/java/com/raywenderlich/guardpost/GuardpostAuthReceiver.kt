package com.raywenderlich.guardpost

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raywenderlich.guardpost.data.SSOUser

class GuardpostAuthReceiver(
  private val _login: MutableLiveData<SSOUser> = MutableLiveData(),
  private val _logout: MutableLiveData<Boolean> = MutableLiveData(),
  private val _error: MutableLiveData<Int> = MutableLiveData(),
  val login: LiveData<SSOUser> = _login,
  val logout: LiveData<Boolean> = _logout,
  val error: LiveData<Int> = _error
) : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    if (null == intent) {
      _error.postValue(GuardpostAuth.AuthErrors.INVALID_RESPONSE)
    }
    when (intent?.action) {
      GuardpostAuth.BroadcastActions.LOGIN_SUCCESS ->
        _login.postValue(intent.getParcelableExtra(RedirectActivity.EXTRA_RESULT))
      GuardpostAuth.BroadcastActions.LOGIN_FAILURE ->
        _error.postValue(GuardpostAuth.AuthErrors.INVALID_RESPONSE)
      GuardpostAuth.BroadcastActions.LOGOUT_SUCCESS ->
        _logout.postValue(true)
      GuardpostAuth.BroadcastActions.FAILURE ->
        _error.postValue(
          intent.getIntExtra(
            RedirectActivity.EXTRA_RESULT,
            GuardpostAuth.AuthErrors.INVALID_RESPONSE
          )
        )
    }
  }
}
