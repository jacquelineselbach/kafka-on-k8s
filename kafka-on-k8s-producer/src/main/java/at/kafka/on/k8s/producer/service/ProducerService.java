package at.kafka.on.k8s.producer.service;

import at.kafka.on.k8s.shared.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class ProducerService implements CommandLineRunner {

    private final KafkaTemplate<String, Event> kafkaTemplate;

    @Value("${app.topic.name}")
    private String topic;

    @Value("${app.producer.delay-ms:1000}")
    private long delay;

    @Override
    public void run(String... args) {

        int counter = 0;
        while (true) {

            Event event = Event.builder()
                    .message("hello kafka " + counter)
                    .id("demo")
                    .build();

            kafkaTemplate
                    .send(topic, event.id(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent key={} value={} partition={} offset={}",
                                    event.id(),
                                    event.message(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Error sending message", ex);
                        }
                    });

            counter++;

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.warn("Producer interrupted, stopping...");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
