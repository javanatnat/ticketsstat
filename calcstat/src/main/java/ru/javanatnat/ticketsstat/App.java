package ru.javanatnat.ticketsstat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javanatnat.ticketsstat.statistics.AvgTimeFlightCalculatorImpl;
import ru.javanatnat.ticketsstat.statistics.FlightCalculator;
import ru.javanatnat.ticketsstat.statistics.PercentileFlightCalculatorImpl;
import ru.javanatnat.ticketsstat.statistics.PercentileMethodCalc;
import ru.javanatnat.ticketsstat.tickets.Ticket;
import ru.javanatnat.ticketsstat.tickets.TicketParser;

import java.util.Arrays;
import java.util.List;

import static ru.javanatnat.ticketsstat.tickets.AirportDirectory.*;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOG.info("app run with args: {}", Arrays.toString(args));
        String fileName = getFileName(args);
        LOG.info("filename: {}", fileName);

        List<Ticket> tickets = TicketParser.getTickets(fileName);
        List<FlightCalculator> flightCalculators = List.of(
                new AvgTimeFlightCalculatorImpl(tickets, VVO_AIRPORT, TLV_AIRPORT),
                new PercentileFlightCalculatorImpl(tickets, (byte) 90, VVO_AIRPORT, TLV_AIRPORT),
                new PercentileFlightCalculatorImpl(tickets, (byte) 90, VVO_AIRPORT, TLV_AIRPORT,
                        PercentileMethodCalc.LINEAR_INTERPOLATION_C0)
        );

        flightCalculators.forEach(fc -> System.out.println(fc.getStringResult(fc.calcInMinutes())));
    }

    private static String getFileName(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("Не задан файл для считывания данных по билетам!");
        }

        return args[0];
    }
}
