package com.forvmom.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Transactional outbox record written atomically with the inventory update.
 * An async enrichment task reads this record post-commit, constructs the full
 * {@code BookingRequestedEvent}, publishes to Kafka, and marks it PUBLISHED.
 * A scheduled poller retries any record stuck in PENDING/FAILED state.
 *
 * <p>
 * Table: {@code booking_outbox}
 */
@Entity
@Table(name = "booking_outbox", indexes = {
        @Index(name = "idx_outbox_status_created", columnList = "status, created_at"),
        @Index(name = "idx_outbox_booking_ref", columnList = "booking_reference_id", unique = true)
})
public class BookingOutbox {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PUBLISHED = "PUBLISHED";
    public static final String STATUS_FAILED = "FAILED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * e.g. MFB-1735000000000-A3F2 — client-visible reference and idempotency key
     */
    @Column(name = "booking_reference_id", nullable = false, unique = true, length = 60)
    private String bookingReferenceId;

    /**
     * Minimal JSON:
     * {@code {userId, slotMapperId, guestCount, addonMapperIds[], pincode}}
     * Full enrichment happens asynchronously.
     */
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    /** PENDING → PUBLISHED on success, PENDING/FAILED → retried by poller */
    @Column(name = "status", nullable = false, length = 20)
    private String status = STATUS_PENDING;

    /** How many enrichment attempts have been made (fast-path + poller together) */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public BookingOutbox() {
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReferenceId() {
        return bookingReferenceId;
    }

    public void setBookingReferenceId(String bookingReferenceId) {
        this.bookingReferenceId = bookingReferenceId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    // ── Business helpers ───────────────────────────────────────────────────────

    public void markPublished() {
        this.status = STATUS_PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = STATUS_FAILED;
        this.retryCount = this.retryCount + 1;
    }

    public void incrementRetry() {
        this.retryCount = this.retryCount + 1;
    }
}
