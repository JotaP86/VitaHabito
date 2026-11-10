package com.vitahabitoapp

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Formato almacenado: pbkdf2_sha1:<iteraciones>:<sal en hex>:<hash en hex>
object PasswordHasher {

    // Con minSdk 24 solo hay PBKDF2 con HMAC-SHA1; las variantes con SHA-2 requieren API 26
    private const val ALGORITHM = "PBKDF2WithHmacSHA1"
    private const val PREFIX = "pbkdf2_sha1"
    private const val ITERATIONS = 120_000
    private const val SALT_BYTES = 16
    private const val HASH_BYTES = 32
    private const val HEX_DIGITS = "0123456789abcdef"

    private val random = SecureRandom()

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES)
        random.nextBytes(salt)
        val hash = pbkdf2(password, salt, ITERATIONS, HASH_BYTES)
        return "$PREFIX:$ITERATIONS:${toHex(salt)}:${toHex(hash)}"
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size != 4 || parts[0] != PREFIX) return false

        val iterations = parts[1].toIntOrNull() ?: return false
        val salt = fromHex(parts[2]) ?: return false
        val expected = fromHex(parts[3]) ?: return false
        if (iterations <= 0 || salt.isEmpty() || expected.isEmpty()) return false

        val actual = pbkdf2(password, salt, iterations, expected.size)
        return constantTimeEquals(actual, expected)
    }

    private fun pbkdf2(password: String, salt: ByteArray, iterations: Int, lengthBytes: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, lengthBytes * 8)
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    // Recorre siempre todos los bytes para que el tiempo no dependa de dónde está la primera diferencia
    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var diff = 0
        for (i in a.indices) {
            diff = diff or (a[i].toInt() xor b[i].toInt())
        }
        return diff == 0
    }

    private fun toHex(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            val v = b.toInt() and 0xff
            sb.append(HEX_DIGITS[v ushr 4])
            sb.append(HEX_DIGITS[v and 0x0f])
        }
        return sb.toString()
    }

    private fun fromHex(hex: String): ByteArray? {
        if (hex.length % 2 != 0) return null
        val out = ByteArray(hex.length / 2)
        for (i in out.indices) {
            val hi = HEX_DIGITS.indexOf(hex[2 * i])
            val lo = HEX_DIGITS.indexOf(hex[2 * i + 1])
            if (hi < 0 || lo < 0) return null
            out[i] = ((hi shl 4) or lo).toByte()
        }
        return out
    }
}
