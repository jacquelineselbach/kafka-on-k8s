package at.kafka.on.k8s.shared.model;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;


/**
 * Shared Kafka message model.
 * Defines the structure used by producer, consumer and stream.
 *
 * @param id message key
 * @param message payload text
 */
@Builder
@Jacksonized
public record Event(String id, String message) {}
