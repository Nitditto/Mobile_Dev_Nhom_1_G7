package com.nhom1.studyschedule.util

import java.security.MessageDigest

object PasswordHasher {
    fun hash(raw: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
