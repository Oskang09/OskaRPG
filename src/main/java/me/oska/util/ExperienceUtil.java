package me.oska.util;

import java.util.stream.IntStream;

public class ExperienceUtil {
    private static final int BASE_XP = 700;

    public static int getXpAtLevel(int level) {
        return (int)Math.ceil(BASE_XP * Math.sqrt(level) + ( BASE_XP * level));
    }

    public static int getTotalXp(int level, int currentXp) {
        return IntStream.range(1, level).map(ExperienceUtil::getXpAtLevel).sum() + currentXp;
    }
}
