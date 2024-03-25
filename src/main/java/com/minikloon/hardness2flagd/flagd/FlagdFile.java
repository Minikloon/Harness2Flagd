package com.minikloon.hardness2flagd.flagd;

import java.util.Map;

public record FlagdFile(
        String environment,
        Map<String, FlagdFlag> flags
) {
}
