package com.minikloon.hardness2flagd.harness.environment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minikloon.hardness2flagd.harness.spec.FeatureFlagDefault;

public record FeatureFlagEnvironment(
        String identifier,
        @JsonProperty("default") FeatureFlagDefault defaults,
        String state
) {
    public String computeVariant() {
        boolean on = "on".equals(state);
        return on ? defaults.onVariation() : defaults.offVariation();
    }
}
