package com.minikloon.hardness2flagd.harness.spec.variations;

public record FeatureFlagVariation(
        String identifier,
        String value
) {
    public Object valueAsObject(String specType) {
        return switch (specType) {
            case "boolean" -> "on".equals(value);
            case "string" -> value;
            default -> throw new IllegalStateException("Unexpected specType: " + specType);
        };
    }
}
