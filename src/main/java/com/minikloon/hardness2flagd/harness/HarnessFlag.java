package com.minikloon.hardness2flagd.harness;

import com.minikloon.hardness2flagd.harness.environment.FeatureFlagEnvironment;
import com.minikloon.hardness2flagd.harness.spec.FeatureFlagSpec;

import java.util.List;

public record HarnessFlag(
        String name,
        String identifier,
        String description,
        boolean permanent,
        FeatureFlagSpec spec,
        List<FeatureFlagEnvironment> environments
) {
}
