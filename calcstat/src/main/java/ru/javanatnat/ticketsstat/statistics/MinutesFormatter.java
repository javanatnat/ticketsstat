package ru.javanatnat.ticketsstat.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MinutesFormatter {
    private static final Logger LOG = LoggerFactory.getLogger(MinutesFormatter.class);

    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;

    private final BigDecimal valueInMinutes;

    public MinutesFormatter(BigDecimal valueInMinutes) {
        this.valueInMinutes = valueInMinutes.abs();
    }

    public long getWholeDays() {
        return getWholeDaysBigDecimal().longValue();
    }

    private BigDecimal getWholeDaysBigDecimal() {
        LOG.debug("getWholeDaysBigDecimal: valueInMinutes = {}", valueInMinutes);
        BigDecimal hours = valueInMinutes.divide(BigDecimal.valueOf(MINUTES_IN_HOUR), RoundingMode.DOWN);
        LOG.debug("getWholeDaysBigDecimal: hours = {}", hours);
        if (hours.compareTo(BigDecimal.valueOf(HOURS_IN_DAY)) >= 0) {
            BigDecimal days = hours.divide(BigDecimal.valueOf(HOURS_IN_DAY), RoundingMode.DOWN);
            LOG.debug("getWholeDaysBigDecimal: days = {}", days);
            return days;
        }
        LOG.debug("getWholeDaysBigDecimal: days = 0");
        return BigDecimal.ZERO;
    }

    public long getWholeHoursWithoutDays() {
        return getWholeHoursWithoutDaysBigDecimal().longValue();
    }

    private BigDecimal getWholeHoursWithoutDaysBigDecimal() {
        LOG.debug("getWholeHoursWithoutDaysBigDecimal: valueInMinutes = {}", valueInMinutes);
        BigDecimal hours = valueInMinutes.divide(BigDecimal.valueOf(MINUTES_IN_HOUR), RoundingMode.DOWN);
        LOG.debug("getWholeHoursWithoutDaysBigDecimal: hours = {}", hours);
        if (hours.compareTo(BigDecimal.valueOf(HOURS_IN_DAY)) >= 0) {
            hours = hours.remainder(BigDecimal.valueOf(HOURS_IN_DAY));
            LOG.debug("getWholeHoursWithoutDaysBigDecimal: hours = {}", hours);
        }
        hours = hours.setScale(0, RoundingMode.DOWN);

        LOG.debug("getWholeHoursWithoutDaysBigDecimal: hours = {}", hours);
        return hours;
    }

    public double getDoubleMinutesRest() {
        return getMinutesRest().doubleValue();
    }

    public long getLongMinutesRest() {
        return getMinutesRest().longValue();
    }

    private BigDecimal getMinutesRest() {
        LOG.debug("getMinutesRest: valueInMinutes = {}", valueInMinutes);
        BigDecimal minutesInHours = BigDecimal.valueOf(MINUTES_IN_HOUR);
        BigDecimal daysInMinutes = getWholeDaysBigDecimal()
                .multiply(BigDecimal.valueOf(HOURS_IN_DAY))
                .multiply(minutesInHours);
        LOG.debug("getMinutesRest: daysInMinutes = {}", daysInMinutes);
        BigDecimal hoursInMinutes = getWholeHoursWithoutDaysBigDecimal()
                .multiply(minutesInHours);
        LOG.debug("getMinutesRest: hoursInMinutes = {}", hoursInMinutes);
        return valueInMinutes
                .add(daysInMinutes.negate())
                .add(hoursInMinutes.negate());
    }
}
