package ru.javanatnat.ticketsstat.statistics;

public enum PercentileMethodCalc {
    NEAREST_RANG("метод ближайшего ранга"),
    LINEAR_INTERPOLATION_C0("метод линейной интерполяции между соседними значениями в наборе данных");

    private final String ruValue;

    PercentileMethodCalc(String ruValue) {
        this.ruValue = ruValue;
    }

    public String getRuValue() {
        return ruValue;
    }
}
