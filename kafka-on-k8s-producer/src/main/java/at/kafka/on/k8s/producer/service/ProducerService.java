package at.kafka.on.k8s.producer.service;

import at.kafka.on.k8s.shared.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer service starts automatically at application startup and continuously publishes records
 * to a Kafka topic. This implementation is intentionally minimalistic: it creates synthetic Event objects in a loop,
 * sends them asynchronously to Kafka and logs the delivery outcome when the broker responds.
 */
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

            // kafkaTemplate is your main "Kafka client" interface to send messages to Kafka in Spring
            kafkaTemplate
                    .send(topic, event.id(), event)
                    .whenComplete((result, exception) -> {
                        if (exception == null) {
                            log.info("Sent key={} value={} partition={} offset={}",
                                    event.id(),
                                    event.message(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Error sending message", exception);
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
