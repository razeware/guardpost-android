package com.raywenderlich.guardpost.utils

import android.util.Base64
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and


/**
 * Generate a random hex string
 */
fun randomString(): String =
    (1..40).map {
      ("${('a'..'z').random()}${('A'..'Z').random()}${(0..9).random()}").random()
    }.joinToString("").toByteArray().toHexString()

/**
 * Encodes the String to Base64
 */
fun String.toBase64(): String =
    Base64.encodeToString(this.toByteArray(), Base64.DEFAULT)

fun String.fromBase64(): String =
    Base64.decode(this.toByteArray(), Base64.DEFAULT).toString(Charsets.UTF_8)

/**
 * Encrypts the String using HmacSHA256
 *
 * @param secret Secret for encryption
 */
fun String.toHmacSha256(secret: String): String? {
  return try {
    val algorithm = "HmacSHA256"
    val mac = Mac.getInstance(algorithm)
    val secretSpec = SecretKeySpec(secret.toByteArray(), algorithm)
    mac.init(secretSpec)
    mac.doFinal(toByteArray()).toHexString()
  } catch (exception: NoSuchAlgorithmException) {
    null
  }
}

/**
 * Encodes the ByteArray to Hex String
 */
fun ByteArray.toHexString(): String {
  return joinToString("") {
    String.format("%02x", it and 0xff.toByte())
  }
}
