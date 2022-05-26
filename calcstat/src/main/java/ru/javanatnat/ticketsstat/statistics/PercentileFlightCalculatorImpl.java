package ru.javanatnat.ticketsstat.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javanatnat.ticketsstat.tickets.Airport;
import ru.javanatnat.ticketsstat.tickets.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class PercentileFlightCalculatorImpl implements FlightCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(PercentileFlightCalculatorImpl.class);

    private static final PercentileMethodCalc DEFAULT_METHOD_CALC = PercentileMethodCalc.NEAREST_RANG;

    private final List<Ticket> tickets;
    private final byte percentile;
    private final Airport originAirport;
    private final Airport destinationAirport;
    private PercentileMethodCalc methodCalc;

    public PercentileFlightCalculatorImpl(
            List<Ticket> tickets,
            byte percentile,
            Airport originAirport,
            Airport destinationAirport
    ) {
        this(
                tickets,
                percentile,
                originAirport,
                destinationAirport,
                DEFAULT_METHOD_CALC
        );
    }

    public PercentileFlightCalculatorImpl(
            List<Ticket> tickets,
            byte percentile,
            Airport originAirport,
            Airport destinationAirport,
            PercentileMethodCalc methodCalc
    ) {
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.percentile = percentile;
        this.tickets = FilterTickets.getTicketsByOriginDestAirports(
                tickets,
                originAirport,
                destinationAirport
        );
        this.methodCalc = methodCalc;

        checkParams();
    }

    private void checkParams() {
        Objects.requireNonNull(tickets);
        Objects.requireNonNull(originAirport);
        Objects.requireNonNull(destinationAirport);
        Objects.requireNonNull(methodCalc);

        if (originAirport.equals(destinationAirport)) {
            throw new IllegalArgumentException("Аэропорты отправления и назначения должны различаться " +
                    "для расчёта среднего времени пути полёта!");
        }
        if (tickets.isEmpty()) {
            throw new IllegalArgumentException("Не задан список билетов для расчёта!");
        }
        if (percentile < 0 || percentile > 100) {
            throw new IllegalArgumentException("Некорректно указан перцентиль для расчёта! " +
                    "Диапазон значений перцентиля для расчёта - от 0 до 100");
        }
    }

    @Override
    public BigDecimal calcInMinutes() {
        LOG.info("start calc: tickets.count = {}, percentile = {}, originAirport = {}, " +
                        "destinationAirport = {}, methodCalc = {}",
                tickets.size(), percentile, originAirport, destinationAirport, methodCalc);

        long[] sortedTickets = getSortedTicketFlightDurations();
        if (percentile == 0) {
            return get0Percentile(sortedTickets);
        }
        if (percentile == 100) {
            return get100Percentile(sortedTickets);
        }
        if (methodCalc == PercentileMethodCalc.NEAREST_RANG) {
            return getPercentileNearestRang(sortedTickets);
        }
        if (methodCalc == PercentileMethodCalc.LINEAR_INTERPOLATION_C0) {
            return getPercentileLinearInterpolationC0(sortedTickets);
        }
        throw new IllegalArgumentException("Неверно заданы параметры для расчёта!");
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public byte getPercentile() {
        return percentile;
    }

    public void setMethodCalc(PercentileMethodCalc methodCalc) {
        this.methodCalc = methodCalc;
    }

    public PercentileMethodCalc getMethodCalc() {
        return methodCalc;
    }

    public static PercentileMethodCalc getDefaultMethodCalc() {
        return DEFAULT_METHOD_CALC;
    }

    @Override
    public String toString() {
        return "PercentileFlightCalculatorImpl{" +
                "tickets=" + tickets +
                ", percentile=" + percentile +
                '}';
    }

    @Override
    public String getStringResult(BigDecimal resultInMinutes) {
        MinutesFormatter formatter = new MinutesFormatter(resultInMinutes);

        long days = formatter.getWholeDays();
        long hours = formatter.getWholeHoursWithoutDays();
        double minutes = formatter.getDoubleMinutesRest();

        return percentile + "-й процентиль рассчитанный по методу \"" +
                methodCalc.getRuValue() +
                "\" времени полёта между городами " + originAirport.getCityName()
                + " и " + destinationAirport.getCityName() + " составляет: "
                + (days > 0 ? days + " дней " : "")
                + (hours > 0 ? hours + " часов " : "")
                + minutes + " минут";
    }

//    The nearest rank method
    public BigDecimal getPercentileNearestRang(long[] sortedTickets) {
        LOG.info("start calc for {} percentile by the nearest rank method", percentile);
        LOG.debug("tickets: {}", sortedTickets);
        BigDecimal resultRang = getPercent();
        LOG.debug("percent: {}", resultRang);
        resultRang = resultRang.multiply(BigDecimal.valueOf(sortedTickets.length));
        LOG.debug("percent*n: {}", resultRang);
        resultRang = correctResultRang(resultRang, sortedTickets);
        LOG.debug("x after correct: {}", resultRang);
        BigDecimal result = getTicketValue(resultRang, sortedTickets);
        LOG.debug("end calc unscaled result for {} percentile by the nearest rank method: {}", percentile, result);
        result = result.setScale(2, RoundingMode.HALF_UP);
        LOG.info("end calc result for {} percentile by the nearest rank method: {}", percentile, result);
        return result;
    }

//    The linear interpolation between closest ranks method
//    The primary variant recommended by NIST, C = 0, x = p(N+1)
    public BigDecimal getPercentileLinearInterpolationC0(long[] sortedTickets) {
        LOG.info("start calc for {} percentile by the linear interpolation between closest ranks method"
                , percentile);
        LOG.debug("tickets: {}", sortedTickets);
        BigDecimal x = getPercent();
        LOG.debug("percent: {}", x);
        x = x.multiply(BigDecimal.valueOf(sortedTickets.length + 1));
        LOG.debug("percent*(n+1): {}", x);
        BigDecimal xLeftPart = x.setScale(0, RoundingMode.DOWN);
        LOG.debug("whole part of x: {}", xLeftPart);
        BigDecimal xRightPart = x.remainder(BigDecimal.ONE);
        LOG.debug("fractional part of x: {}", xRightPart);
        BigDecimal n = correctResultRang(xLeftPart, sortedTickets);
        LOG.debug("after correct x: {}", n);
        BigDecimal nNext = correctResultRang(n.add(BigDecimal.ONE), sortedTickets);
        LOG.debug("(x+1): {}", nNext);
        BigDecimal valueN = getTicketValue(n, sortedTickets);
        LOG.debug("value(x): {}", valueN);
        LOG.debug("value(x+1): {}", getTicketValue(nNext, sortedTickets));
        BigDecimal result = getTicketValue(nNext, sortedTickets).add(valueN.negate());
        LOG.debug("value(x+1) - value(x): {}", result);
        result = result.multiply(xRightPart);
        LOG.debug("{x} * (value(x+1) - value(x)): {}", result);
        result = result.add(valueN);
        LOG.info("end calc unscaled result for {} percentile by the linear interpolation between closest ranks method: {}"
                , percentile, result);
        result = result.setScale(2, RoundingMode.HALF_UP);
        LOG.info("end calc result for {} percentile by the linear interpolation between closest ranks method: {}"
                , percentile, result);
        return result;
    }

    public long[] getSortedTicketFlightDurations() {
        return tickets.stream().mapToLong(Ticket::getFlightDurationInMinutes).sorted().toArray();
    }

    private BigDecimal getPercent() {
        BigDecimal result = BigDecimal.valueOf((double) percentile);
        return result.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    private static BigDecimal correctResultRang(BigDecimal result, long[] sortedTickets) {
        if (result.intValue() > sortedTickets.length) {
            return BigDecimal.valueOf(sortedTickets.length);
        }
        if (result.intValue() <= 1) {
            return BigDecimal.ONE;
        }
        return result.setScale(0, RoundingMode.DOWN);
    }

    private BigDecimal getTicketValue(BigDecimal rang, long[] sortedTickets) {
        int n = rang.intValue() - 1;
        return BigDecimal.valueOf(sortedTickets[n]);
    }

    private static BigDecimal get0Percentile(long[] sortedTickets) {
        BigDecimal result = BigDecimal.valueOf(sortedTickets[0]);
        LOG.info("end calc result for 0 percentile: {}", result);
        return result;
    }

    private static BigDecimal get100Percentile(long[] sortedTickets) {
        int size = sortedTickets.length;
        BigDecimal result = BigDecimal.valueOf(sortedTickets[size-1]);
        LOG.info("end calc result for 100 percentile: {}", result);
        return result;
    }
}
