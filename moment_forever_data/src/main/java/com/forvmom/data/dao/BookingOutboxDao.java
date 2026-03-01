package com.forvmom.data.dao;

import com.forvmom.data.entities.BookingOutbox;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingOutboxDao extends GenericDao<BookingOutbox, Long> {

    BookingOutbox findByBookingReferenceId(String bookingReferenceId);

    /**
     * Returns outbox records eligible for retry by the scheduled poller.
     * Picks up records that are PENDING or FAILED, older than {@code olderThan},
     * and have not yet exceeded {@code maxRetries}.
     */
    List<BookingOutbox> findRetryable(LocalDateTime olderThan, int maxRetries);

    /**
     * Clean up published messages that are older than a cutoff time.
     */
    int deletePublishedOlderThan(LocalDateTime cutoff);

    /**
     * Atomically locks the record by setting status to PROCESSING if it is
     * currently PENDING or FAILED.
     * Returns the number of rows updated (0 if already processing/published, 1 if
     * claimed).
     */
    int markAsProcessing(String bookingReferenceId);

    /**
     * Resets records stuck in PROCESSING state back to FAILED so they can be
     * retried.
     */
    int resetStuckProcessing(LocalDateTime cutoff);
}
