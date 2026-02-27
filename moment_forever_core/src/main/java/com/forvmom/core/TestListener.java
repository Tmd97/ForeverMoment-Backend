package com.forvmom.core;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class TestListener {
    @KafkaListener(topics = "booking-requested")
    public void listen(String message, Acknowledgment ack) {
        System.out.println("Received: " + message);
        ack.acknowledge();
    }
}