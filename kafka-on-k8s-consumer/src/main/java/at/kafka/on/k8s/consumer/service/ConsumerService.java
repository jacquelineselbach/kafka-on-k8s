package at.kafka.on.k8s.consumer.service;

import at.kafka.on.k8s.shared.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


/**
 * Kafka consumer service listens to the configured Kafka topic and processes incoming Event messages.
 * Thanks to the configured Serde, the listener receives fully deserialized Event objects
 * instead of raw byte arrays or strings.
 */
@Slf4j
@Service
class ConsumerService {

    @KafkaListener(topics = "${app.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Event event) {
        log.info("Received key={} value={}", event.id(), event.message());
    }
}
