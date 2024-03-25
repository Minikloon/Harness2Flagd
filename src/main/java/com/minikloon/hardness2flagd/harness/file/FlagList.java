package com.minikloon.hardness2flagd.harness.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minikloon.hardness2flagd.harness.HarnessFlag;

import java.util.List;

public record FlagList(
        @JsonProperty("flags") List<FlagObject> flagsList
) {
    record FlagObject(
            HarnessFlag flag
    ) {}
}
