package at.kafka.on.k8s.stream.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;


/**
 * Kafka stream configuration class uses the application properties to set up the Streams runtime.
 * * @EnableKafka enables basic Kafka support in the Spring context (e.g. listeners, consumer setup).
 * * @EnableKafkaStreams turns on Kafka Streams support and tells Spring to manage the Kafka Streams lifecycle.
 */
@Configuration
@EnableKafka
@EnableKafkaStreams
@RequiredArgsConstructor
public class StreamConfig {

    private final KafkaProperties kafkaProperties;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        return new KafkaStreamsConfiguration(kafkaProperties.buildStreamsProperties());
    }
}
