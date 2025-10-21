package at.kafka.on.k8s.consumer.service;

import at.kafka.on.k8s.shared.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
class ConsumerService {

    @KafkaListener(topics = "${app.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Event event) {
        log.info("Received key={} value={}", event.id(), event.message());
    }
}
