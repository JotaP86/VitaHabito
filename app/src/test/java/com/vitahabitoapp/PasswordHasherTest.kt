package com.vitahabitoapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun hashTieneFormatoEsperado() {
        val parts = PasswordHasher.hash("secreto123").split(":")

        assertEquals(4, parts.size)
        assertEquals("pbkdf2_sha1", parts[0])
        assertTrue(parts[1].toInt() > 0)
        assertEquals(32, parts[2].length)
        assertEquals(64, parts[3].length)
    }

    @Test
    fun mismaContrasenaGeneraHashDistinto() {
        val first = PasswordHasher.hash("secreto123")
        val second = PasswordHasher.hash("secreto123")

        assertNotEquals(first, second)
        assertTrue(PasswordHasher.verify("secreto123", first))
        assertTrue(PasswordHasher.verify("secreto123", second))
    }

    @Test
    fun hashNoContieneLaContrasena() {
        assertFalse(PasswordHasher.hash("secreto123").contains("secreto123"))
    }

    @Test
    fun verifyAceptaContrasenaCorrecta() {
        val stored = PasswordHasher.hash("Contraseña-ñ 2026")

        assertTrue(PasswordHasher.verify("Contraseña-ñ 2026", stored))
    }

    @Test
    fun verifyRechazaContrasenaIncorrecta() {
        val stored = PasswordHasher.hash("secreto123")

        assertFalse(PasswordHasher.verify("secreto124", stored))
        assertFalse(PasswordHasher.verify("Secreto123", stored))
        assertFalse(PasswordHasher.verify("", stored))
    }

    @Test
    fun verifyRechazaHashModificado() {
        val stored = PasswordHasher.hash("secreto123")
        val lastChar = stored.last()
        val tampered = stored.dropLast(1) + if (lastChar == '0') '1' else '0'

        assertFalse(PasswordHasher.verify("secreto123", tampered))
    }

    @Test
    fun verifyDevuelveFalseConFormatoInvalido() {
        val valid = PasswordHasher.hash("secreto123")
        val parts = valid.split(":")

        val invalid = listOf(
            "",
            "secreto123",
            "pbkdf2_sha1:120000:abcd",
            "md5:120000:${parts[2]}:${parts[3]}",
            "pbkdf2_sha1:muchas:${parts[2]}:${parts[3]}",
            "pbkdf2_sha1:0:${parts[2]}:${parts[3]}",
            "pbkdf2_sha1:-5:${parts[2]}:${parts[3]}",
            "pbkdf2_sha1:120000::${parts[3]}",
            "pbkdf2_sha1:120000:${parts[2]}:",
            "pbkdf2_sha1:120000:zz${parts[2].drop(2)}:${parts[3]}",
            "pbkdf2_sha1:120000:${parts[2]}:${parts[3].drop(1)}",
            "$valid:extra"
        )

        for (stored in invalid) {
            assertFalse("Formato aceptado: $stored", PasswordHasher.verify("secreto123", stored))
        }
    }
}
