package me.oska.plugins.orpg;

public class Dexterity {
    private int value;

    public Dexterity(int value) {
        this.value = value;
    }

    public static Integer toSQL(Dexterity dexterity) {
        return dexterity.value;
    }

    public static Strength fromSQL(Integer sqlDexterity) {
        if (sqlDexterity == null) {
            return new Strength(0);
        }
        return new Strength(sqlDexterity);
    }

    @Override
    public String toString() {
        return "Dexterity{" +
                "value=" + value +
                '}';
    }
}
