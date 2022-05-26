package ru.javanatnat.ticketsstat.statistics;

import java.math.BigDecimal;

public interface FlightCalculator {
    BigDecimal calcInMinutes();

    String getStringResult(BigDecimal resultInMinutes);
}
