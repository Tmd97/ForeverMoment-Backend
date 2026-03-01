package com.forvmom.data.dao;

import com.forvmom.data.entities.BookingOutbox;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class BookingOutboxDaoImpl extends GenericDaoImpl<BookingOutbox, Long>
                implements BookingOutboxDao {

        public BookingOutboxDaoImpl() {
                super(BookingOutbox.class);
        }

        @Override
        public BookingOutbox findByBookingReferenceId(String bookingReferenceId) {
                List<BookingOutbox> results = em.createQuery(
                                "SELECT o FROM BookingOutbox o WHERE o.bookingReferenceId = :ref",
                                BookingOutbox.class)
                                .setParameter("ref", bookingReferenceId)
                                .getResultList();
                return results.isEmpty() ? null : results.get(0);
        }

        @Override
        public List<BookingOutbox> findRetryable(LocalDateTime olderThan, int maxRetries) {
                return em.createQuery(
                                "SELECT o FROM BookingOutbox o " +
                                                "WHERE o.status IN ('PENDING', 'FAILED') " +
                                                "  AND o.createdAt < :olderThan " +
                                                "  AND o.retryCount < :maxRetries " +
                                                "ORDER BY o.createdAt ASC",
                                BookingOutbox.class)
                                .setParameter("olderThan", olderThan)
                                .setParameter("maxRetries", maxRetries)
                                .setMaxResults(20)
                                .getResultList();
        }

        @Override
        public int deletePublishedOlderThan(LocalDateTime cutoff) {
                return em.createQuery(
                                "DELETE FROM BookingOutbox o " +
                                                "WHERE o.status = :published AND o.publishedAt < :cutoff")
                                .setParameter("published", BookingOutbox.STATUS_PUBLISHED)
                                .setParameter("cutoff", cutoff)
                                .executeUpdate();
        }

        @Override
        public int markAsProcessing(String bookingReferenceId) {
                return em.createQuery(
                                "UPDATE BookingOutbox o SET o.status = :processing " +
                                                "WHERE o.bookingReferenceId = :ref AND o.status IN (:pending, :failed)")
                                .setParameter("processing", BookingOutbox.STATUS_PROCESSING)
                                .setParameter("ref", bookingReferenceId)
                                .setParameter("pending", BookingOutbox.STATUS_PENDING)
                                .setParameter("failed", BookingOutbox.STATUS_FAILED)
                                .executeUpdate();
        }

        @Override
        public int resetStuckProcessing(LocalDateTime cutoff) {
                return em.createQuery(
                                "UPDATE BookingOutbox o SET o.status = :failed " +
                                                "WHERE o.status = :processing AND o.createdAt < :cutoff")
                                .setParameter("failed", BookingOutbox.STATUS_FAILED)
                                .setParameter("processing", BookingOutbox.STATUS_PROCESSING)
                                .setParameter("cutoff", cutoff)
                                .executeUpdate();
        }
}
