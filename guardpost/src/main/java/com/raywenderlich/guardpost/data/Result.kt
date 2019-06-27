package com.raywenderlich.guardpost.data

/**
 * Wrapper class to deliver Result
 */
internal sealed class Result {
  data class Success<T>(val result: T) : Result()
  data class Failure(val error: Int? = null) : Result()
}
