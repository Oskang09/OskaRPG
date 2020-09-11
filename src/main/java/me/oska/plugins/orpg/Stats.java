package me.oska.plugins.orpg;

import me.oska.minecraft.OskaRPG;

public class Stats {
    private int value;
    private int equipment;
    private int skill;

    public Stats() {
        this.value = 0;
        this.equipment = 0;
        this.skill = 0;
    }

    public static String toSQL(Stats stat) {
        return OskaRPG.getGson().toJson(stat);
    }

    public static Stats fromSQL(String sqlJSON) {
        if (sqlJSON == null) {
            return new Stats();
        }
        return OskaRPG.getGson().fromJson(sqlJSON, Stats.class);
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
