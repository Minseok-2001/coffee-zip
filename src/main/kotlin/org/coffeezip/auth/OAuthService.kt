package org.coffeezip.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

data class OAuthUserInfo(
    val provider: String,
    val providerId: String,
    val email: String?,
    val nickname: String,
    val profileImage: String?,
)

@ApplicationScoped
class OAuthService {

    @ConfigProperty(name = "coffeezip.oauth.google.client-id", defaultValue = "")
    lateinit var googleClientId: String

    @ConfigProperty(name = "coffeezip.oauth.google.client-secret", defaultValue = "")
    lateinit var googleClientSecret: String

    @ConfigProperty(name = "coffeezip.oauth.google.redirect-uri", defaultValue = "http://localhost:3000/auth/callback")
    lateinit var googleRedirectUri: String

    private val httpClient = HttpClient.newHttpClient()
    private val objectMapper = ObjectMapper()

    fun exchangeGoogleCode(code: String): OAuthUserInfo {
        // 1. code → access token
        val tokenBody = mapOf(
            "code" to code,
            "client_id" to googleClientId,
            "client_secret" to googleClientSecret,
            "redirect_uri" to googleRedirectUri,
            "grant_type" to "authorization_code",
        ).entries.joinToString("&") { (k, v) ->
            "${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"
        }

        val tokenRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://oauth2.googleapis.com/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(tokenBody))
            .build()

        val tokenResponse = httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString())
        @Suppress("UNCHECKED_CAST")
        val tokenMap = objectMapper.readValue(tokenResponse.body(), Map::class.java) as Map<String, Any>
        val accessToken = tokenMap["access_token"] as String

        // 2. access token → user info
        val userInfoRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"))
            .header("Authorization", "Bearer $accessToken")
            .GET()
            .build()

        val userInfoResponse = httpClient.send(userInfoRequest, HttpResponse.BodyHandlers.ofString())
        @Suppress("UNCHECKED_CAST")
        val userInfo = objectMapper.readValue(userInfoResponse.body(), Map::class.java) as Map<String, Any>

        return OAuthUserInfo(
            provider = "google",
            providerId = userInfo["id"] as String,
            email = userInfo["email"] as? String,
            nickname = (userInfo["name"] as? String) ?: (userInfo["email"] as? String ?: ""),
            profileImage = userInfo["picture"] as? String,
        )
    }
}
