package at.kafka.on.k8s.shared.model;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record Event(String id, String message) {}
