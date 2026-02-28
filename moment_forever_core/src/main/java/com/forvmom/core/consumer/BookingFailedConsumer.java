package com.forvmom.core.consumer;

import com.forvmom.common.dto.events.BookingFailedEvent;
import com.forvmom.data.dao.ExperienceTimeSlotMapperDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core service consumer for the {@code booking-failed} topic.
 *
 * <p>
 * <strong>Gap 1 — Inventory Compensation:</strong><br>
 * When the Booking Service cannot fulfill a booking (payment failure,
 * validation
 * failure), it publishes a {@link BookingFailedEvent}. This consumer receives
 * that event and atomically decrements {@code current_bookings} on the affected
 * {@code ExperienceTimeSlotMapper} row — rolling back the capacity that was
 * reserved during the booking initiation transaction.
 *
 * <p>
 * Idempotency is handled by the DAO which floors the decrement at 0 using a
 * {@code CASE} expression, so duplicate events cannot drive the count negative.
 */
@Component
public class BookingFailedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BookingFailedConsumer.class);

    private final ExperienceTimeSlotMapperDao slotMapperDao;

    public BookingFailedConsumer(ExperienceTimeSlotMapperDao slotMapperDao) {
        this.slotMapperDao = slotMapperDao;
    }

    @KafkaListener(topics = "${kafka.topics.booking-failed:booking-failed}", groupId = "core-booking-failed-group", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void onBookingFailed(@Payload BookingFailedEvent event, Acknowledgment ack) {
        logger.info("Received booking-failed: bookingId={}, slotMapperId={}, guestCount={}",
                event.getBookingId(), event.getTimeSlotMapperId(), event.getGuestCount());

        try {
            int rows = slotMapperDao.atomicDecrementCapacity(
                    event.getTimeSlotMapperId(), event.getGuestCount());

            logger.info("Inventory rolled back: bookingId={}, slotMapperId={}, rowsAffected={}",
                    event.getBookingId(), event.getTimeSlotMapperId(), rows);

            ack.acknowledge();

        } catch (Exception e) {
            logger.error("Failed to rollback inventory for bookingId={}: {}",
                    event.getBookingId(), e.getMessage(), e);
            // Do NOT ack — Kafka will redeliver → DLQ after max retries
            throw e;
        }
    }
}
