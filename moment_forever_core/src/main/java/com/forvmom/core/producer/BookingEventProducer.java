package com.forvmom.core.producer;

import com.forvmom.common.dto.events.BookingRequestEvent;
import com.forvmom.common.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer responsible for publishing {@link BookingRequestEvent}s to the
 * {@code booking-requested} topic.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Generate a unique {@code bookingId}</li>
 * <li>Stamp {@code requestedAt} timestamp</li>
 * <li>Send the event with the bookingId as the Kafka message key (for partition
 * routing)</li>
 * <li>Log success/failure callbacks</li>
 * </ul>
 *
 * <p>
 * All business validation and price resolution are done upstream in
 * {@code BookingPublisherService} before this class is called.
 */
@Service
public class BookingEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(BookingEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.booking-requested:booking-requested}")
    private String bookingRequestedTopic;

    public BookingEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Generates a unique booking reference, stamps the timestamp, and publishes
     * the event to Kafka.
     *
     * @param event the fully enriched booking event (without bookingId and
     *              requestedAt — set here)
     * @return the generated bookingId for the HTTP response
     */
    public String sendBookingRequested(BookingRequestEvent event) {
        String bookingId = generateBookingId();
        event.setBookingId(bookingId);
        event.setRequestedAt(LocalDateTime.now());

        logger.info("Publishing booking-requested: bookingId={}, userId={}, experienceId={}, "
                + "slotMapperId={}, date={}, guests={}, grandTotal={}",
                bookingId,
                event.getUserId(),
                event.getExperienceId(),
                event.getTimeSlotMapperId(),
                event.getBookingDate(),
                event.getGuestCount(),
                event.getGrandTotal());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(bookingRequestedTopic, bookingId,
                event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("Failed to publish booking-requested: bookingId={}, error={}",
                        bookingId, ex.getMessage(), ex);
            } else {
                logger.info("Successfully published booking-requested: bookingId={}, "
                        + "topic={}, partition={}, offset={}",
                        bookingId,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });

        return bookingId;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Generates a booking reference in the format {@code MFB-<epoch_ms>-<uuid4>}.
     * Example: {@code MFB-1735000000000-a3f2}
     */
    private String generateBookingId() {
        return AppConstants.BOOKING_REFERENCE_PREFIX
                + System.currentTimeMillis()
                + "-"
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}