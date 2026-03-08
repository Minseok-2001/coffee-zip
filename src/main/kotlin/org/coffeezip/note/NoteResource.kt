package org.coffeezip.note

import io.quarkus.security.Authenticated
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import org.coffeezip.auth.AuthContext
import java.time.LocalDate

@Path("/notes")
@ApplicationScoped
@Authenticated
class NoteResource {

    @Inject
    lateinit var noteService: NoteService

    @Inject
    lateinit var authContext: AuthContext

    @GET
    fun getNote(@QueryParam("date") date: String): Response {
        val memberId = authContext.memberId
        val localDate = LocalDate.parse(date)
        val note = noteService.getNote(memberId, localDate)
        return if (note == null) {
            Response.noContent().build()
        } else {
            Response.ok(note).build()
        }
    }

    @PUT
    @Path("/{date}")
    fun upsertNote(@PathParam("date") date: String, req: UpsertNoteRequest): Response {
        val memberId = authContext.memberId
        val localDate = LocalDate.parse(date)
        val note = noteService.upsertNote(memberId, localDate, req)
        return Response.ok(note).build()
    }
}
