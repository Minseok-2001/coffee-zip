package org.coffeezip.note

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.coffeezip.entity.DailyNote
import org.coffeezip.entity.TimerLog
import java.time.LocalDate
import java.time.LocalDateTime

data class UpsertNoteRequest(val content: String?, val rating: Int?)

data class TimerLogResponse(
    val id: Long,
    val recipeId: Long,
    val recipeName: String,
    val startedAt: String,
    val completedAt: String?
)

data class NoteResponse(
    val id: Long,
    val memberId: Long,
    val noteDate: String,
    val content: String?,
    val rating: Int?,
    val timerLogs: List<TimerLogResponse>,
    val updatedAt: String
)

@ApplicationScoped
class NoteService {

    @Inject
    lateinit var noteRepository: NoteRepository

    fun getNote(memberId: Long, date: LocalDate): NoteResponse? {
        val note = noteRepository.findByMemberAndDate(memberId, date) ?: return null
        val timerLogs = noteRepository.findTimerLogsByNoteId(note.id!!)
        return toNoteResponse(note, timerLogs)
    }

    @Transactional
    fun upsertNote(memberId: Long, date: LocalDate, req: UpsertNoteRequest): NoteResponse {
        val existing = noteRepository.findByMemberAndDate(memberId, date)
        val note = if (existing != null) {
            existing.content = req.content
            existing.rating = req.rating
            existing.updatedAt = LocalDateTime.now()
            existing
        } else {
            val newNote = DailyNote().apply {
                this.memberId = memberId
                this.noteDate = date
                this.content = req.content
                this.rating = req.rating
                this.createdAt = LocalDateTime.now()
                this.updatedAt = LocalDateTime.now()
            }
            noteRepository.em.persist(newNote)
            newNote
        }
        val timerLogs = noteRepository.findTimerLogsByNoteId(note.id!!)
        return toNoteResponse(note, timerLogs)
    }

    private fun toTimerLogResponse(timerLog: TimerLog): TimerLogResponse {
        return TimerLogResponse(
            id = timerLog.id!!,
            recipeId = timerLog.recipeId,
            recipeName = timerLog.recipeName,
            startedAt = timerLog.startedAt.toString(),
            completedAt = timerLog.completedAt?.toString()
        )
    }

    private fun toNoteResponse(note: DailyNote, timerLogs: List<TimerLog>): NoteResponse {
        return NoteResponse(
            id = note.id!!,
            memberId = note.memberId,
            noteDate = note.noteDate.toString(),
            content = note.content,
            rating = note.rating,
            timerLogs = timerLogs.map { toTimerLogResponse(it) },
            updatedAt = note.updatedAt.toString()
        )
    }
}
