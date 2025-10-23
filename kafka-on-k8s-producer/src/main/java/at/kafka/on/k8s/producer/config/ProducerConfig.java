package at.kafka.on.k8s.producer.config;

import at.kafka.on.k8s.shared.model.Event;
import at.kafka.on.k8s.shared.serde.JacksonSerde;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

/**
 * Kafka producer configuration class is a central place for setting up the Kafka producer infrastructure.
 * Instead of configuring serializers, factories, and templates manually,
 * we expose them as beans so that Spring can wire them into services automatically.
 */
@Configuration
@RequiredArgsConstructor
public class ProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public JacksonSerde<Event> jacksonSerde() {
        return new JacksonSerde<>(Event.class);
    }

    @Bean
    public ProducerFactory<String, Event> producerFactory(JacksonSerde<Event> serde) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                serde.serializer()
        );
    }

    @Bean
    public KafkaTemplate<String, Event> kafkaTemplate(ProducerFactory<String, Event> factory) {
        return new KafkaTemplate<>(factory);
    }
}
