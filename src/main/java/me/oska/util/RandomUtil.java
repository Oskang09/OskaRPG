package me.oska.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    private static int random(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static int randomInteger(int bound) {
        return random(bound);
    }
}
