package me.oska.plugins.orpg;

public enum GraphicLevel {
    NONE(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    int level;
    GraphicLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}