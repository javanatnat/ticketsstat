package statistics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.javanatnat.ticketsstat.statistics.FilterTickets.getTicketsByOriginDestAirports;
import static ru.javanatnat.ticketsstat.tickets.AirportDirectory.*;
import ru.javanatnat.ticketsstat.tickets.Ticket;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class FilterTicketsTest {
    @Test
    public void testNullParams() {
        assertThatThrownBy(() -> getTicketsByOriginDestAirports(null, VVO_AIRPORT, SVO_AIRPORT))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> getTicketsByOriginDestAirports(new ArrayList<>(), null, SVO_AIRPORT))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> getTicketsByOriginDestAirports(new ArrayList<>(), VVO_AIRPORT, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testEmptyTickets() {
        List<Ticket> result = getTicketsByOriginDestAirports(new ArrayList<>(), VVO_AIRPORT, SVO_AIRPORT);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    public void testOnlySuitableTickets() {
        ZonedDateTime departDate = ZonedDateTime.of(2020, 5, 11,
                12, 10, 0, 0, ZoneId.of(SVO_AIRPORT.getTimeRegion()));
        ZonedDateTime arrivDate = ZonedDateTime.of(2020, 5, 11,
                12, 10, 0, 0, ZoneId.of(VVO_AIRPORT.getTimeRegion()));

        Ticket.Builder ticketBuilder = new Ticket.Builder(SVO_AIRPORT, VVO_AIRPORT)
                .setDepartureDate(departDate)
                .setArrivalDate(arrivDate);

        List<Ticket> tickets = List.of(
                ticketBuilder.build(),
                ticketBuilder.build()
        );

        List<Ticket> result = getTicketsByOriginDestAirports(tickets, SVO_AIRPORT, VVO_AIRPORT);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).containsAll(tickets);
    }

    @Test
    public void testDiffTickets() {
        ZonedDateTime departDate = ZonedDateTime.of(2020, 5, 11,
                12, 10, 0, 0, ZoneId.of(SVO_AIRPORT.getTimeRegion()));
        ZonedDateTime arrivDate = ZonedDateTime.of(2020, 5, 11,
                12, 10, 0, 0, ZoneId.of(VVO_AIRPORT.getTimeRegion()));

        Ticket.Builder ticketBuilder = new Ticket.Builder(SVO_AIRPORT, VVO_AIRPORT)
                .setDepartureDate(departDate)
                .setArrivalDate(arrivDate);
        Ticket suitableTicket1 = ticketBuilder.build();
        Ticket suitableTicket2 = ticketBuilder.build();

        List<Ticket> tickets = List.of(
                suitableTicket1,
                suitableTicket2,
                new Ticket.Builder(TLV_AIRPORT, SVO_AIRPORT)
                        .setDepartureDate(
                                departDate.withZoneSameLocal(
                                        ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                        .setArrivalDate(
                                arrivDate.withZoneSameLocal(
                                        ZoneId.of(SVO_AIRPORT.getTimeRegion())))
                        .build()
        );

        List<Ticket> result = getTicketsByOriginDestAirports(tickets, SVO_AIRPORT, VVO_AIRPORT);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).containsExactly(suitableTicket1, suitableTicket2);
    }

    @Test void testOnlyUnSuitableTickets() {
        ZonedDateTime departDate = ZonedDateTime.of(2020, 5, 11,
                12, 10, 0, 0, ZoneId.of(SVO_AIRPORT.getTimeRegion()));
        ZonedDateTime arrivDate = ZonedDateTime.of(2020, 5, 11,
                12, 10, 0, 0, ZoneId.of(VVO_AIRPORT.getTimeRegion()));

        Ticket.Builder ticketBuilder = new Ticket.Builder(SVO_AIRPORT, VVO_AIRPORT)
                .setDepartureDate(departDate)
                .setArrivalDate(arrivDate);
        Ticket unsuitableTicket1 = ticketBuilder.build();
        Ticket unsuitableTicket2 = ticketBuilder.build();

        List<Ticket> tickets = List.of(
                unsuitableTicket1,
                unsuitableTicket2
        );

        List<Ticket> result = getTicketsByOriginDestAirports(tickets, TLV_AIRPORT, VVO_AIRPORT);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        result = getTicketsByOriginDestAirports(tickets, TLV_AIRPORT, SVO_AIRPORT);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        result = getTicketsByOriginDestAirports(tickets, SVO_AIRPORT, TLV_AIRPORT);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}
