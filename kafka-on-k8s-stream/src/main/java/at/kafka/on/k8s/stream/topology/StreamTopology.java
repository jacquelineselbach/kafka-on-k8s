package at.kafka.on.k8s.stream.topology;

import at.kafka.on.k8s.shared.model.Event;
import at.kafka.on.k8s.shared.serde.JacksonSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Kafka Stream topology is the blueprint of a data processing pipeline in Kafka Streams.
 * It describes how data moves from input topics through processing steps
 * (transformations, filters, mappings, aggregations) to output topics.
 * In this case: inputTopic is the source, peek/mapValues are the processing steps outputTopic is the sink.
 */
@Slf4j
@Component
public class StreamTopology {

    @Value("${app.topic.input}")
    private String inputTopic;

    @Value("${app.topic.output}")
    private String outputTopic;

    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<Event> EVENT_SERDE = new JacksonSerde<>(Event.class);

    @Autowired
    public void buildPipeline(StreamsBuilder builder) {

        // Source processor: reads events from the input topic
        KStream<String, Event> stream = builder.stream(inputTopic, Consumed.with(STRING_SERDE, EVENT_SERDE));

        // Stream processor: transform and log events
        stream
                .peek((key, value) -> log.info("Received key={} value={} from topic={}", key, value, inputTopic))
                .mapValues(event -> Event.builder()
                        .id(event.id())
                        .message(event.message().replace("hello kafka", "hello women"))
                        .build())
                .peek((key, value) -> log.info("Transformed key={} value={} sending to topic={}", key, value, outputTopic))
                // Sink processor: write transformed events to output topic
                .to(outputTopic, Produced.with(STRING_SERDE, EVENT_SERDE));
    }
}
