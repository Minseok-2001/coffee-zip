package org.coffeezip.calendar

import io.quarkus.security.Authenticated
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import org.coffeezip.auth.AuthContext
import org.coffeezip.note.NoteRepository

data class CalendarResponse(val year: Int, val month: Int, val dates: List<String>)

@Path("/calendar")
@ApplicationScoped
@Authenticated
class CalendarResource {

    @Inject
    lateinit var authContext: AuthContext

    @Inject
    lateinit var noteRepository: NoteRepository

    @GET
    fun getCalendar(@QueryParam("year") year: Int, @QueryParam("month") month: Int): Response {
        val memberId = authContext.memberId
        val dates = noteRepository.findDatesWithNoteInMonth(memberId, year, month)
            .map { it.toString() }
        return Response.ok(CalendarResponse(year, month, dates)).build()
    }
}
