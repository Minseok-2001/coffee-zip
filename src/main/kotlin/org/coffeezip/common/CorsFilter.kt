package org.coffeezip.common

import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.container.PreMatching
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider

private val ALLOWED_ORIGINS = setOf("http://localhost:3000")

@Provider
@PreMatching
class CorsPreflightFilter : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {
        val origin = requestContext.getHeaderString("Origin") ?: return
        if (!requestContext.method.equals("OPTIONS", ignoreCase = true)) return

        requestContext.abortWith(
            Response.ok()
                .header("Access-Control-Allow-Origin", if (origin in ALLOWED_ORIGINS) origin else "")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .header("Access-Control-Max-Age", "86400")
                .build()
        )
    }
}

@Provider
class CorsResponseFilter : ContainerResponseFilter {
    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        val origin = requestContext.getHeaderString("Origin") ?: return
        if (origin !in ALLOWED_ORIGINS) return

        responseContext.headers.putSingle("Access-Control-Allow-Origin", origin)
        responseContext.headers.putSingle("Access-Control-Allow-Credentials", "true")
        responseContext.headers.putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
        responseContext.headers.putSingle("Access-Control-Allow-Headers", "Content-Type, Authorization")
    }
}
