package me.oska.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class ChanceUtil {
    private static int random(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    // Default Random : Trigger based on random value
    public static boolean defaultChance(int chance) {
        return random(100) < chance;
    }

    // Pseudo Random: Must over [n] times only will trigger
    public static boolean pseudoChance(int base, int times) {
        double increment = Math.sqrt(base) * times;
        double pseudoValue = base + increment;
        return pseudoValue + random((int)pseudoValue) >= 100;
    }

    // True Random : Higher [n] times, higher chance to trigger
    public static boolean trueChance(int base, int times) {
        int trueValue = IntStream.range(1, times).map(x -> random(base)).sum();
        return base + trueValue + random(base * times) >= 100;
    }
}
