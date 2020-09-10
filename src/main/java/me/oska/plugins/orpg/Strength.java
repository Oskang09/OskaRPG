package me.oska.plugins.orpg;


public class Strength {
    private int value;

    public Strength(int value) {
        this.value = value;
    }

    public static Integer toSQL(Strength strength) {
        return strength.value;
    }

    public static Strength fromSQL(Integer sqlStrength) {
        if (sqlStrength == null) {
            return new Strength(0);
        }
        return new Strength(sqlStrength);
    }

    @Override
    public String toString() {
        return "Strength{" +
                "value=" + value +
                '}';
    }
}
