package com.minikloon.hardness2flagd.harness.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.math.DoubleMath;
import com.google.common.primitives.Doubles;
import com.minikloon.hardness2flagd.harness.spec.variations.FeatureFlagVariation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record FeatureFlagSpec(
        String type,
        @JsonProperty("default") FeatureFlagDefault defaults,
        List<FeatureFlagVariation> variations
) {
    public Map<String, Object> variationsAsMap() {
        Map<String, Object> variationsMap = new HashMap<>();
        variations().forEach(var -> {
            Object value = parseFromType(var.value(), type);
            variationsMap.put(var.identifier(), value);
        });
        return variationsMap;
    }

    private Object parseFromType(String value, String type) {
        return switch (type) {
            case "boolean" -> "true".equals(value);
            case "string" -> {
                Double num = Doubles.tryParse(value);
                if (num == null) {
                    yield value;
                }
                yield DoubleMath.isMathematicalInteger(num) ? num.intValue() : num;
            }
            default -> throw new IllegalStateException("Unknown spec type " + type + "(value=" + value + ")");
        };
    }
}
