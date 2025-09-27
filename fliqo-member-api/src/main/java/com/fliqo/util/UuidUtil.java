package com.fliqo.util;

import java.util.UUID;

public final class UuidUtil {
    private UuidUtil() {}

    public static String newUuid() {
        return UUID.randomUUID().toString();
    }
}
