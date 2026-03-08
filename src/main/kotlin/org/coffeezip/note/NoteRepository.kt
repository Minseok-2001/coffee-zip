package org.coffeezip.note

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import org.coffeezip.entity.DailyNote
import org.coffeezip.entity.TimerLog
import java.time.LocalDate

@ApplicationScoped
class NoteRepository {

    @Inject
    lateinit var em: EntityManager

    fun findByMemberAndDate(memberId: Long, date: LocalDate): DailyNote? {
        return em.createQuery(
            "SELECT n FROM DailyNote n WHERE n.memberId = :memberId AND n.noteDate = :date",
            DailyNote::class.java
        )
            .setParameter("memberId", memberId)
            .setParameter("date", date)
            .resultList
            .firstOrNull()
    }

    fun findTimerLogsByNoteId(noteId: Long): List<TimerLog> {
        return em.createQuery(
            "SELECT t FROM TimerLog t WHERE t.noteId = :noteId ORDER BY t.startedAt",
            TimerLog::class.java
        )
            .setParameter("noteId", noteId)
            .resultList
    }

    fun findDatesWithNoteInMonth(memberId: Long, year: Int, month: Int): List<LocalDate> {
        return em.createQuery(
            "SELECT n.noteDate FROM DailyNote n WHERE n.memberId = :memberId AND YEAR(n.noteDate) = :year AND MONTH(n.noteDate) = :month",
            LocalDate::class.java
        )
            .setParameter("memberId", memberId)
            .setParameter("year", year)
            .setParameter("month", month)
            .resultList
    }
}
