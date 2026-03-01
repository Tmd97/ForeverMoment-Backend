package com.forvmom.core.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forvmom.core.async.BookingEnrichmentTask;
import com.forvmom.data.dao.BookingOutboxDao;
import com.forvmom.data.dao.ExperienceTimeSlotMapperDao;
import com.forvmom.data.entities.BookingOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OutboxCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(OutboxCleanupService.class);
    private static final String STATUS_DEAD = "DEAD";
    private static final String STATUS_COMPENSATED = "compensated inventory";
    @Autowired
    private BookingOutboxDao bookingOutboxDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExperienceTimeSlotMapperDao slotMapperDao;

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
        List<BookingOutbox> unresolved = bookingOutboxDao.findUnresolved(cutoff);
        logger.info("Outbox poller found {} record(s) to retry", unresolved.size());
        for (BookingOutbox record : unresolved) {

            if (record.getRetryCount() >= MAX_RETRIES) {
                logger.error("Booking permanently failed after {} retries: bookingId={}",
                        MAX_RETRIES, record.getBookingReferenceId());
                compensatePermanentFailure(record);
            } else {
                logger.info("Retrying enrichment for bookingReferenceId={}, retryCount={}",
                        record.getBookingReferenceId(), record.getRetryCount());
                bookingEnrichmentTask.enrich(record.getBookingReferenceId());
            }
        }

    }

    @Transactional
    public void compensatePermanentFailure(BookingOutbox record) {
        try {
            // Parse the minimal payload to get slotMapperId and guestCount
            Map<String, Object> payload = objectMapper.readValue(
                    record.getPayload(), new TypeReference<Map<String, Object>>() {
                    });

            Long slotMapperId = toLong(payload.get("slotMapperId"));
            Integer guestCount = toInt(payload.get("guestCount"));

            if (slotMapperId == null || guestCount == null) {
                logger.error("Invalid payload for compensation: bookingId={}",
                        record.getBookingReferenceId());
                record.setStatus(STATUS_DEAD);
                bookingOutboxDao.update(record);
                return;
            }

            // Release inventory (decrement current_bookings)
            int updated = slotMapperDao.atomicDecrementCapacity(slotMapperId, guestCount);

            if (updated > 0) {
                logger.info("Inventory released for failed booking: slotMapperId={}, bookingId={}",
                        slotMapperId, record.getBookingReferenceId());

                // Mark as compensated
                record.setStatus(STATUS_COMPENSATED);
                record.setCompensatedAt(LocalDateTime.now());
                bookingOutboxDao.update(record);

            } else {
                // This is bad - inventory might be inconsistent
                logger.error("CRITICAL: Could not release inventory for slotMapperId={}, bookingId={}",
                        slotMapperId, record.getBookingReferenceId());

                // Mark as DEAD for manual investigation
                record.setStatus(STATUS_DEAD);
                record.setFailureReason("Inventory compensation failed");
                bookingOutboxDao.update(record);

                // Alert operations team
                sendAlert("Permanent booking failure with inventory inconsistency: " +
                        record.getBookingReferenceId());
            }

        } catch (Exception e) {
            logger.error("Failed to compensate booking: {}", record.getBookingReferenceId(), e);

            // Mark as DEAD for manual investigation
            record.setStatus(STATUS_DEAD);
            record.setFailureReason(e.getMessage());
            bookingOutboxDao.update(record);

            // Alert operations team
            sendAlert("Compensation failed for booking: " + record.getBookingReferenceId());
        }
    }

    // Helper methods for parsing
    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        return Long.parseLong(obj.toString());
    }

    private Integer toInt(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return Integer.parseInt(obj.toString());
    }

    private void sendAlert(String message) {
        // Implement alerting (email, Slack, PagerDuty, etc.)
        logger.error("ALERT: {}", message);
    }
}
