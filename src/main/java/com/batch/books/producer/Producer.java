package com.batch.books.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class Producer {

    Logger log = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/publish")
    public void publish(@RequestBody String message) {
        for (int i = 1; i < 3; i++) {
            String id = UUID.randomUUID().toString();
            log.info("published --> :: " + message);
            this.kafkaTemplate.send("quick-events", id, message);
        }
    }
}