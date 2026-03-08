package org.coffeezip.timer

import io.quarkus.security.Authenticated
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.coffeezip.auth.AuthContext
import org.coffeezip.entity.DailyNote
import org.coffeezip.entity.TimerLog
import org.coffeezip.note.NoteRepository
import java.time.LocalDate
import java.time.LocalDateTime

data class TimerLogRequest(
    val recipeId: Long,
    val recipeName: String,
    val startedAt: String,
    val completedAt: String?
)

@Path("/timer")
@ApplicationScoped
@Authenticated
class TimerResource {

    @Inject
    lateinit var authContext: AuthContext

    @Inject
    lateinit var em: EntityManager

    @Inject
    lateinit var noteRepository: NoteRepository

    @POST
    @Path("/log")
    @Transactional
    fun logTimer(req: TimerLogRequest): Response {
        val memberId = authContext.memberId
        val today = LocalDate.now()

        val note = noteRepository.findByMemberAndDate(memberId, today) ?: run {
            val newNote = DailyNote().apply {
                this.memberId = memberId
                this.noteDate = today
                this.createdAt = LocalDateTime.now()
                this.updatedAt = LocalDateTime.now()
            }
            em.persist(newNote)
            newNote
        }

        val timerLog = TimerLog().apply {
            this.memberId = memberId
            this.noteId = note.id
            this.recipeId = req.recipeId
            this.recipeName = req.recipeName
            this.startedAt = LocalDateTime.parse(req.startedAt)
            this.completedAt = req.completedAt?.let { LocalDateTime.parse(it) }
        }
        em.persist(timerLog)

        return Response.ok(mapOf("id" to timerLog.id)).build()
    }
}
