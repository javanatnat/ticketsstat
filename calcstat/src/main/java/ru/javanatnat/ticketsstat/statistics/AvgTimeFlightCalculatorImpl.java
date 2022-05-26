package ru.javanatnat.ticketsstat.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javanatnat.ticketsstat.tickets.Airport;
import ru.javanatnat.ticketsstat.tickets.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AvgTimeFlightCalculatorImpl implements FlightCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(AvgTimeFlightCalculatorImpl.class);

    private final List<Ticket> tickets;
    private final Airport originAirport;
    private final Airport destinationAirport;

    public AvgTimeFlightCalculatorImpl(
            List<Ticket> tickets,
            Airport originAirport,
            Airport destinationAirport
    ) {
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.tickets = FilterTickets.getTicketsByOriginDestAirports(tickets, originAirport, destinationAirport);
        checkParams();
    }

    private void checkParams() {
        Objects.requireNonNull(tickets);
        Objects.requireNonNull(originAirport);
        Objects.requireNonNull(destinationAirport);

        if (originAirport.equals(destinationAirport)) {
            throw new IllegalArgumentException("Аэропорты отправления и назначения должны различаться " +
                    "для расчёта среднего времени пути полёта!");
        }
        if (tickets.isEmpty()) {
            throw new IllegalArgumentException("Не задан список билетов для расчёта!");
        }
    }

    @Override
    public BigDecimal calcInMinutes() {
        LOG.info("start calc: tickets.count = {}, originAirport = {}, destinationAirport = {}",
                tickets.size(), originAirport, destinationAirport);
        LOG.debug("tickets: {}", tickets);
        LOG.debug("tickets durations: {}", Arrays.toString(
                tickets.stream().mapToLong(Ticket::getFlightDurationInMinutes).toArray()));
        BigDecimal result = new BigDecimal("0.0");
        for (Ticket ticket : tickets) {
            result = result.add(BigDecimal.valueOf(ticket.getFlightDurationInMinutes()));
        }
        LOG.debug("sum of durations: {}", result);
        result = result.divide(BigDecimal.valueOf(tickets.size()), RoundingMode.DOWN);
        LOG.debug("end calc unscaled result: {}", result);
        result = result.setScale(2, RoundingMode.HALF_UP);
        LOG.info("end calc: {}", result);
        return result;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    @Override
    public String toString() {
        return "AvgTimeFlightCalculatorImpl{" +
                "tickets=" + tickets +
                '}';
    }

    @Override
    public String getStringResult(BigDecimal resultInMinutes) {
        MinutesFormatter formatter = new MinutesFormatter(resultInMinutes);

        long days = formatter.getWholeDays();
        long hours = formatter.getWholeHoursWithoutDays();
        double minutes = formatter.getDoubleMinutesRest();

        return "среднее время полёта между городами " + originAirport.getCityName()
                + " и " + destinationAirport.getCityName() + " составляет: "
                + (days > 0 ? days + " дней " : "")
                + (hours > 0 ? hours + " часов " : "")
                + minutes + " минут";
    }
}
