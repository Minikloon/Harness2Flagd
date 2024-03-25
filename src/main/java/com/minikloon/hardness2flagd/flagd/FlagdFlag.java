package com.minikloon.hardness2flagd.flagd;

import java.util.Map;

public record FlagdFlag(
        String identifier,
        FlagdState state,
        Map<String, Object> variants,
        Object defaultVariant
) {
}
