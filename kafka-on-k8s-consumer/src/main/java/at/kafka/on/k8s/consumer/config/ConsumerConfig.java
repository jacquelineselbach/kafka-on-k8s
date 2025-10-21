package at.kafka.on.k8s.consumer.config;

import at.kafka.on.k8s.shared.model.Event;
import at.kafka.on.k8s.shared.serde.JacksonSerde;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class ConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public JacksonSerde<Event> jacksonSerde() {
        return new JacksonSerde<>(Event.class);
    }

    @Bean
    public ConsumerFactory<String, Event> consumerFactory(JacksonSerde<Event> serde) {

        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                serde.deserializer()
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Event> kafkaListenerContainerFactory(
            ConsumerFactory<String, Event> factory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Event> listenerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();

        listenerFactory.setConsumerFactory(factory);
        return listenerFactory;
    }
}
