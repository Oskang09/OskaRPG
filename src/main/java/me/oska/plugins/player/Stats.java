package me.oska.plugins.player;

public class Stats {
    private int value;
    private int equipment;
    private int skill;

    public Stats() {
        this.value = 0;
        this.equipment = 0;
        this.skill = 0;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "value=" + value +
                ", equipment=" + equipment +
                ", skill=" + skill +
                '}';
    }
}
