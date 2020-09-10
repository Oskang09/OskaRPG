package me.oska.plugins.orpg;

public class Accuracy {
    private int value;

    public Accuracy(int value) {
        this.value = value;
    }

    public static Integer toSQL(Accuracy accuracy) {
        return accuracy.value;
    }

    public static Strength fromSQL(Integer sqlAccuracy) {
        if (sqlAccuracy == null) {
            return new Strength(0);
        }
        return new Strength(sqlAccuracy);
    }

    @Override
    public String toString() {
        return "Accuracy{" +
                "value=" + value +
                '}';
    }
}
