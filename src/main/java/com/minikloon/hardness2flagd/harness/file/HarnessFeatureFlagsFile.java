package com.minikloon.hardness2flagd.harness.file;

import com.minikloon.hardness2flagd.harness.HarnessFlag;

import java.util.stream.Stream;

public record HarnessFeatureFlagsFile(
        FlagList featureFlags
) {
    public Stream<HarnessFlag> flags() {
        return featureFlags.flagsList().stream().map(f -> f.flag());
    }
}
