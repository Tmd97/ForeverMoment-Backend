package com.forvmom.core.services;

import com.forvmom.core.async.BookingEnrichmentTask;
import com.forvmom.data.dao.BookingOutboxDao;
import com.forvmom.data.entities.BookingOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(OutboxCleanupService.class);
    @Autowired
    private BookingOutboxDao bookingOutboxDao;

    @Autowired
    private BookingEnrichmentTask bookingEnrichmentTask;
    private static final int HOURS_TO_KEEP = 24;
    private static final int MAX_RETRIES = 5;
    private static final long GRACE_PERIOD_MINUTES = 2;

    @Transactional
    public void cleanupPublishedRecords() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(HOURS_TO_KEEP);
        int deletedCount = bookingOutboxDao.deletePublishedOlderThan(cutoff);

        if (deletedCount > 0) {
            logger.info("Cleaned up {} published outbox record(s) older than {}",
                    deletedCount, cutoff);
        }
    }

    @Transactional
    public void retriesForStuckEnrichments() {
        // 1. Reset any records stuck in PROCESSING for more than 5 minutes
        LocalDateTime stuckCutoff = LocalDateTime.now().minusMinutes(5);
        int resetCount = bookingOutboxDao.resetStuckProcessing(stuckCutoff);
        if (resetCount > 0) {
            logger.warn("Reset {} stuck outbox record(s) from PROCESSING back to FAILED", resetCount);
        }
        // 2. Fetch unpublished outbox records older than the grace period and retry
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(GRACE_PERIOD_MINUTES);
        List<BookingOutbox> retryable = bookingOutboxDao.findRetryable(cutoff, MAX_RETRIES);

        if (retryable.isEmpty()) {
            return;
        }
        logger.info("Outbox poller found {} record(s) to retry", retryable.size());
        for (BookingOutbox record : retryable) {
            logger.info("Retrying enrichment for bookingReferenceId={}, retryCount={}",
                    record.getBookingReferenceId(), record.getRetryCount());
            bookingEnrichmentTask.enrich(record.getBookingReferenceId());
        }

    }


}
