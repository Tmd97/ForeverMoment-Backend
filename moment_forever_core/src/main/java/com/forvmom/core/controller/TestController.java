package com.forvmom.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/kafka")
public class TestController {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/send-to-booking")
    public String send(@RequestParam String message) {
        kafkaTemplate.send("booking-requested", message);
        return "Sent: " + message;
    }
}