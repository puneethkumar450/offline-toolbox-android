package com.puneeth450.offlinetoolbox.app.domain.developer

import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object DeveloperTools {
    private val json = Json { prettyPrint = true; isLenient = true; ignoreUnknownKeys = true }

    fun formatJson(input: String): String {
        val element = json.decodeFromString<JsonElement>(input)
        return json.encodeToString(JsonElement.serializer(), element)
    }

    fun minifyJson(input: String): String {
        val element = json.decodeFromString<JsonElement>(input)
        return Json.encodeToString(JsonElement.serializer(), element)
    }

    fun encodeUrl(input: String): String = URLEncoder.encode(input, Charsets.UTF_8.name())
    fun decodeUrl(input: String): String = URLDecoder.decode(input, Charsets.UTF_8.name())

    fun hash(input: String, algorithm: HashAlgorithm): String {
        val digest = MessageDigest.getInstance(algorithm.jvmName).digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun lorem(paragraphs: Int): String {
        require(paragraphs in 1..20) { "Paragraphs must be 1 to 20" }
        val paragraph = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
        return List(paragraphs) { paragraph }.joinToString("\n\n")
    }
}

enum class HashAlgorithm(val label: String, val jvmName: String) {
    MD5("MD5", "MD5"),
    SHA1("SHA-1", "SHA-1"),
    SHA256("SHA-256", "SHA-256"),
    SHA512("SHA-512", "SHA-512")
}
