package at.kafka.on.k8s.shared.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Generic JSON Serde for Kafka messages.
 * Used to serialize objects to JSON bytes (producer) and
 * deserialize JSON bytes back to objects (consumer).
 * Implements Kafka's Serde interface.
 */
@RequiredArgsConstructor
public class JacksonSerde<T> implements Serde<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> targetType;

    @Override
    public Serializer<T> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException("Serialization error", e);
            }
        };
    }


    @Override
    public Deserializer<T> deserializer() {
        return (topic, bytes) -> {
            try {
                return objectMapper.readValue(bytes, targetType);
            } catch (Exception e) {
                throw new RuntimeException("Deserialization error", e);
            }
        };
    }
}
