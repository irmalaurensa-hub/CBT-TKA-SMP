package com.example.data.security

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Manages AES-256 encryption and decryption for question bank security.
 * Ensures uploaded questions cannot be read in plaintext from device storage or logs.
 */
object CryptoManager {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val DEFAULT_SECRET_PASSPHRASE = "TKA_SMP_CBT_SECURE_VAULT_KEY_2026"

    /**
     * Derives a 256-bit AES key from a passphrase using SHA-256.
     */
    private fun deriveKey(passphrase: String = DEFAULT_SECRET_PASSPHRASE): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(passphrase.toByteArray(StandardCharsets.UTF_8))
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    data class EncryptedResult(
        val cipherTextBase64: String,
        val ivBase64: String,
        val sha256Checksum: String
    )

    /**
     * Encrypts plain text string using AES-256-CBC with a randomly generated 16-byte IV.
     */
    fun encrypt(plainText: String, passphrase: String = DEFAULT_SECRET_PASSPHRASE): EncryptedResult {
        val secretKey = deriveKey(passphrase)
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val ivBytes = ByteArray(16)
        SecureRandom().nextBytes(ivBytes)
        val ivSpec = IvParameterSpec(ivBytes)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

        // Calculate SHA-256 checksum of plaintext for integrity check
        val md = MessageDigest.getInstance("SHA-256")
        val checksumBytes = md.digest(plainText.toByteArray(StandardCharsets.UTF_8))
        val checksumHex = checksumBytes.joinToString("") { "%02x".format(it) }

        return EncryptedResult(
            cipherTextBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP),
            ivBase64 = Base64.encodeToString(ivBytes, Base64.NO_WRAP),
            sha256Checksum = checksumHex
        )
    }

    /**
     * Decrypts AES-256-CBC cipher text using the provided Base64 IV.
     */
    fun decrypt(
        cipherTextBase64: String,
        ivBase64: String,
        passphrase: String = DEFAULT_SECRET_PASSPHRASE
    ): String {
        return try {
            val secretKey = deriveKey(passphrase)
            val cipher = Cipher.getInstance(TRANSFORMATION)

            val ivBytes = Base64.decode(ivBase64, Base64.NO_WRAP)
            val ivSpec = IvParameterSpec(ivBytes)

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val encryptedBytes = Base64.decode(cipherTextBase64, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
