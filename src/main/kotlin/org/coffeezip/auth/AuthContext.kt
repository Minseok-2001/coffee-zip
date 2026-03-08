package org.coffeezip.auth

import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Path
import jakarta.ws.rs.WebApplicationException
import org.eclipse.microprofile.jwt.JsonWebToken

@RequestScoped
@Path("/noop-auth-context")
class AuthContext {

    @Inject
    lateinit var jwt: JsonWebToken

    val memberId: Long
        get() = jwt.subject?.toLong() ?: throw WebApplicationException(401)
}
