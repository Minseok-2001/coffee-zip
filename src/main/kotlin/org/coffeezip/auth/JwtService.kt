package org.coffeezip.auth

import io.smallrye.jwt.build.Jwt
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Duration

@ApplicationScoped
class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    lateinit var issuer: String

    @ConfigProperty(name = "coffeezip.jwt.access-token-expiry", defaultValue = "3600")
    var accessTokenExpiry: Long = 0L

    @ConfigProperty(name = "coffeezip.jwt.refresh-token-expiry", defaultValue = "2592000")
    var refreshTokenExpiry: Long = 0L

    fun generateAccessToken(memberId: Long, nickname: String): String {
        return Jwt.issuer(issuer)
            .subject(memberId.toString())
            .claim("nickname", nickname)
            .expiresIn(Duration.ofSeconds(accessTokenExpiry))
            .sign()
    }

    fun generateRefreshToken(memberId: Long): String {
        return Jwt.issuer(issuer)
            .subject(memberId.toString())
            .claim("type", "refresh")
            .expiresIn(Duration.ofSeconds(refreshTokenExpiry))
            .sign()
    }
}
