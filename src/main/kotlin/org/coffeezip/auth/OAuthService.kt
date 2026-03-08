package org.coffeezip.auth

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.core.Form
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.config.inject.ConfigProperty

data class OAuthUserInfo(
    val provider: String,
    val providerId: String,
    val email: String?,
    val nickname: String,
    val profileImage: String?
)

@ApplicationScoped
class OAuthService {

    @ConfigProperty(name = "coffeezip.oauth.google.client-id", defaultValue = "")
    lateinit var googleClientId: String

    @ConfigProperty(name = "coffeezip.oauth.google.client-secret", defaultValue = "")
    lateinit var googleClientSecret: String

    @ConfigProperty(name = "coffeezip.oauth.google.redirect-uri", defaultValue = "http://localhost:8080/auth/google/callback")
    lateinit var googleRedirectUri: String

    @ConfigProperty(name = "coffeezip.oauth.kakao.client-id", defaultValue = "")
    lateinit var kakaoClientId: String

    @ConfigProperty(name = "coffeezip.oauth.kakao.client-secret", defaultValue = "")
    lateinit var kakaoClientSecret: String

    @ConfigProperty(name = "coffeezip.oauth.kakao.redirect-uri", defaultValue = "http://localhost:8080/auth/kakao/callback")
    lateinit var kakaoRedirectUri: String

    fun exchangeGoogleCode(code: String): OAuthUserInfo {
        val client = ClientBuilder.newClient()
        try {
            val tokenForm = Form()
                .param("code", code)
                .param("client_id", googleClientId)
                .param("client_secret", googleClientSecret)
                .param("redirect_uri", googleRedirectUri)
                .param("grant_type", "authorization_code")

            val tokenResponse = client
                .target("https://oauth2.googleapis.com/token")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.form(tokenForm))

            val tokenBody = tokenResponse.readEntity(Map::class.java)
            val accessToken = tokenBody["access_token"] as String

            val userInfoResponse = client
                .target("https://www.googleapis.com/oauth2/v2/userinfo")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $accessToken")
                .get()

            val userInfo = userInfoResponse.readEntity(Map::class.java)

            return OAuthUserInfo(
                provider = "google",
                providerId = userInfo["id"] as String,
                email = userInfo["email"] as? String,
                nickname = (userInfo["name"] as? String) ?: (userInfo["email"] as? String ?: ""),
                profileImage = userInfo["picture"] as? String
            )
        } finally {
            client.close()
        }
    }

    fun exchangeKakaoCode(code: String): OAuthUserInfo {
        val client = ClientBuilder.newClient()
        try {
            val tokenForm = Form()
                .param("code", code)
                .param("client_id", kakaoClientId)
                .param("client_secret", kakaoClientSecret)
                .param("redirect_uri", kakaoRedirectUri)
                .param("grant_type", "authorization_code")

            val tokenResponse = client
                .target("https://kauth.kakao.com/oauth/token")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.form(tokenForm))

            val tokenBody = tokenResponse.readEntity(Map::class.java)
            val accessToken = tokenBody["access_token"] as String

            val userInfoResponse = client
                .target("https://kapi.kakao.com/v1/user/me")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $accessToken")
                .get()

            val userInfo = userInfoResponse.readEntity(Map::class.java)

            @Suppress("UNCHECKED_CAST")
            val kakaoAccount = userInfo["kakao_account"] as? Map<String, Any>
            @Suppress("UNCHECKED_CAST")
            val profile = kakaoAccount?.get("profile") as? Map<String, Any>

            val providerId = userInfo["id"].toString()
            val email = kakaoAccount?.get("email") as? String
            val nickname = profile?.get("nickname") as? String ?: email ?: providerId
            val profileImage = profile?.get("profile_image_url") as? String

            return OAuthUserInfo(
                provider = "kakao",
                providerId = providerId,
                email = email,
                nickname = nickname,
                profileImage = profileImage
            )
        } finally {
            client.close()
        }
    }
}
