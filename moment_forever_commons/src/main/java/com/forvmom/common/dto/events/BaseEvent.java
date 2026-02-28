package com.forvmom.common.dto.events;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseEvent {
    private String eventId = UUID.randomUUID().toString();
    private Instant timestamp = Instant.now();
    private String eventType;

    // Constructors
    public BaseEvent() {
        this.eventType = this.getClass().getSimpleName();
    }

    // Getters and setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}