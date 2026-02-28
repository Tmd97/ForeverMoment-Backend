package com.forvmom.core.scheduler;

import com.forvmom.core.async.BookingEnrichmentTask;
import com.forvmom.data.dao.BookingOutboxDao;
import com.forvmom.data.entities.BookingOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled safety-net poller that retries outbox records stuck in
 * {@code PENDING} or {@code FAILED} state.
 *
 * <p>
 * Runs every 60 seconds. Skips records created within the last 2 minutes
 * (gives the async fast-path time to complete first), and caps retry count
 * at 5 to avoid infinite loops. Records that hit the cap should trigger an
 * ops alert (future work).
 */
@Component
public class BookingOutboxPoller {

    private static final Logger logger = LoggerFactory.getLogger(BookingOutboxPoller.class);

    private static final int MAX_RETRIES = 5;
    private static final long GRACE_PERIOD_MINUTES = 2;

    private final BookingOutboxDao outboxDao;
    private final BookingEnrichmentTask enrichmentTask;

    public BookingOutboxPoller(BookingOutboxDao outboxDao,
            BookingEnrichmentTask enrichmentTask) {
        this.outboxDao = outboxDao;
        this.enrichmentTask = enrichmentTask;
    }

    /**
     * Every 60 seconds: fetch unpublished outbox records older than the grace
     * period and retry enrichment on each.
     */
    @Scheduled(fixedDelay = 60_000)
    public void pollAndRetry() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(GRACE_PERIOD_MINUTES);
        List<BookingOutbox> retryable = outboxDao.findRetryable(cutoff, MAX_RETRIES);

        if (retryable.isEmpty()) {
            return;
        }

        logger.info("Outbox poller found {} record(s) to retry", retryable.size());

        for (BookingOutbox record : retryable) {
            logger.info("Retrying enrichment for bookingReferenceId={}, retryCount={}",
                    record.getBookingReferenceId(), record.getRetryCount());
            enrichmentTask.enrich(record.getBookingReferenceId());
        }
    }

    /**
     * Every hour: clean up records that were successfully published
     * more than 24 hours ago to prevent the outbox table from growing indefinitely.
     */
    @Scheduled(cron = "0 0 * * * *") // Run at the top of every hour
    public void cleanUpPublished() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        int deletedCount = outboxDao.deletePublishedOlderThan(cutoff);
        if (deletedCount > 0) {
            logger.info("Cleaned up {} published outbox record(s) older than {}", deletedCount, cutoff);
        }
    }
}
