package com.mcmiddleearth.entities.util;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

public class UuidGenerator {

    public static UUID fast_nullUUID() {
        return new UUID(0L,0L);
    }

    public static UUID fast_random() {
        Random rand = new Random();
        return new UUID(rand.nextLong(),rand.nextLong());
    }

    public static UUID slow_getRandomV1() {
        Random random = new Random();
        long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
        long variant3BitFlag = 0x8000000000000000L;
        long leastSig = random63BitLong + variant3BitFlag;

        LocalDateTime start = LocalDateTime.of(1582, 10, 15, 0, 0, 0);
        Duration duration = Duration.between(start, LocalDateTime.now());
        long seconds = duration.getSeconds();
        long nanos = duration.getNano();
        long timeForUuidIn100Nanos = seconds * 10000000 + nanos * 100;
        long least12SignificatBitOfTime = (timeForUuidIn100Nanos & 0x000000000000FFFFL) >> 4;
        long version = 1 << 12;
        long mostSig = (timeForUuidIn100Nanos & 0xFFFFFFFFFFFF0000L) + version + least12SignificatBitOfTime;

        UUID uuid = new UUID(mostSig,leastSig);
Logger.getGlobal().info("UUID version: "+uuid.version());
        return uuid;
    }

    public static UUID slow_getRandomV3(String namespace, String name) {
        byte[] bytes;
        bytes = (namespace+name).getBytes(StandardCharsets.UTF_8);

        UUID uuid = UUID.nameUUIDFromBytes(bytes);
Logger.getGlobal().info("UUID version: "+uuid.version());
        return uuid;
    }
}
