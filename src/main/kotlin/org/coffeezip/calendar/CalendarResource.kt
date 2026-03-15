package org.coffeezip.calendar

import io.quarkus.security.Authenticated
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import org.coffeezip.auth.AuthContext
import org.coffeezip.entity.Recipe
import org.coffeezip.entity.TimerLog
import org.coffeezip.note.NoteRepository
import java.time.LocalDate
import java.time.LocalDateTime

data class CalendarResponse(val year: Int, val month: Int, val dates: List<String>)

data class BrewSummary(
    val recipeName: String,
    val durationSeconds: Long?,
    val waterTemp: Int?,
)

data class DaySummaryResponse(
    val date: String,
    val hasNote: Boolean,
    val noteContent: String?,
    val rating: Int?,
    val brews: List<BrewSummary>,
)

@Path("/calendar")
@ApplicationScoped
@Authenticated
class CalendarResource {

    @Inject
    lateinit var authContext: AuthContext

    @Inject
    lateinit var noteRepository: NoteRepository

    @Inject
    lateinit var entityManager: EntityManager

    @GET
    fun getCalendar(@QueryParam("year") year: Int, @QueryParam("month") month: Int): Response {
        val memberId = authContext.memberId
        val dates = noteRepository.findDatesWithNoteInMonth(memberId, year, month)
            .map { it.toString() }
        return Response.ok(CalendarResponse(year, month, dates)).build()
    }

    @GET
    @Path("/{date}/summary")
    fun getDaySummary(@PathParam("date") dateStr: String): Response {
        val memberId = authContext.memberId
        val date = LocalDate.parse(dateStr)

        val note = noteRepository.findByMemberAndDate(memberId, date)

        val timerLogs: List<TimerLog> = try {
            entityManager.createQuery(
                "SELECT t FROM TimerLog t WHERE t.memberId = :memberId AND CAST(t.startedAt AS date) = :date",
                TimerLog::class.java
            )
                .setParameter("memberId", memberId)
                .setParameter("date", date)
                .resultList
        } catch (e: Exception) {
            val start: LocalDateTime = date.atStartOfDay()
            val end: LocalDateTime = date.plusDays(1).atStartOfDay()
            entityManager.createQuery(
                "SELECT t FROM TimerLog t WHERE t.memberId = :memberId AND t.startedAt >= :start AND t.startedAt < :end",
                TimerLog::class.java
            )
                .setParameter("memberId", memberId)
                .setParameter("start", start)
                .setParameter("end", end)
                .resultList
        }

        val brews = timerLogs.map { log ->
            val durationSeconds = if (log.completedAt != null) {
                java.time.Duration.between(log.startedAt, log.completedAt).seconds
            } else {
                null
            }
            val waterTemp = entityManager.find(Recipe::class.java, log.recipeId)?.waterTemp
            BrewSummary(
                recipeName = log.recipeName,
                durationSeconds = durationSeconds,
                waterTemp = waterTemp,
            )
        }

        val response = DaySummaryResponse(
            date = dateStr,
            hasNote = note != null,
            noteContent = note?.content,
            rating = note?.rating,
            brews = brews,
        )

        return Response.ok(response).build()
    }
}
