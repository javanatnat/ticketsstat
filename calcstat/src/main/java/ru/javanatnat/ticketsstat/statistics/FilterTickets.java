package ru.javanatnat.ticketsstat.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javanatnat.ticketsstat.tickets.Airport;
import ru.javanatnat.ticketsstat.tickets.Ticket;

import java.util.List;
import java.util.Objects;

public class FilterTickets {
    private static final Logger LOG = LoggerFactory.getLogger(FilterTickets.class);

    public static List<Ticket> getTicketsByOriginDestAirports(
            List<Ticket> tickets,
            Airport originAirport,
            Airport destinationAirport
    ) {
        Objects.requireNonNull(tickets);
        Objects.requireNonNull(originAirport);
        Objects.requireNonNull(destinationAirport);

        LOG.info("start filter: tickets.count = {}, originAirport = {}, destinationAirport = {}",
                tickets.size(), originAirport, destinationAirport);

        List<Ticket> result = tickets.stream()
                .filter(t -> t.getOriginAirport().equals(originAirport)
                        && t.getDestinationAirport().equals(destinationAirport))
                .toList();

        LOG.info("end filter: tickets.count = {}", tickets.size());
        return result;
    }
}
