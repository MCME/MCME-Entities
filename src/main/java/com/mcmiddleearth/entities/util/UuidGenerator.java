package com.mcmiddleearth.entities.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

public class UuidGenerator {

    public static UUID getRandomV1() {
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

    public static UUID getRandomV2() {
        UUID uuid = Generators.timeBasedGenerator(EthernetAddress.fromInterface()).generate();
Logger.getGlobal().info("UUID version: "+uuid.version());
        return uuid;
    }

    public static UUID getRandomV3(String namespace, String name) {
        byte[] bytes = new byte[0];
        try {
            bytes = (namespace+name).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        UUID uuid = UUID.nameUUIDFromBytes(bytes);
Logger.getGlobal().info("UUID version: "+uuid.version());
        return uuid;
    }
}
