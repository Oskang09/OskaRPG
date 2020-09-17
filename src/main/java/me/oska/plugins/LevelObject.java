package me.oska.plugins;

import me.oska.util.ExperienceUtil;

public abstract class LevelObject {

    public abstract int getLevel();
    public abstract int getExperience();

    public int getNextLevelExperience() {
        return ExperienceUtil.getXpAtLevel(getLevel() + 1);
    }

    public int getTotalExperience() {
        return ExperienceUtil.getTotalXp(getLevel(), getExperience());
    }
}
