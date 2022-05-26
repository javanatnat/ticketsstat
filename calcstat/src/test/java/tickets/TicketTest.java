package tickets;

import org.junit.jupiter.api.Test;
import ru.javanatnat.ticketsstat.tickets.Ticket;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javanatnat.ticketsstat.tickets.AirportDirectory.*;

public class TicketTest {
    private static final int MINUTES_IN_HOURS = 60;

    @Test
    public void getFlightDurationInMinutesDiffRegionsTest() {
        ZonedDateTime departureZonedDate = ZonedDateTime.
                of(2020, 1,1,
                        12, 20, 0, 0,
                        ZoneId.of(VVO_AIRPORT.getTimeRegion()));
        ZonedDateTime arrivalZonedDate = departureZonedDate.withZoneSameLocal(ZoneId.of(SVO_AIRPORT.getTimeRegion()));

        Ticket ticket = new Ticket.Builder(VVO_AIRPORT, SVO_AIRPORT)
                .setDepartureDate(departureZonedDate)
                .setArrivalDate(arrivalZonedDate)
                .build();

        long hoursDiff = 7;
        assertThat(ticket.getFlightDurationInMinutes()).isEqualTo(hoursDiff * MINUTES_IN_HOURS);

        ZonedDateTime departureZonedDateBack = arrivalZonedDate.withHour(10);
        ZonedDateTime arrivalZonedDateBack = departureZonedDate.withHour(23);

        System.out.println(departureZonedDateBack);
        System.out.println(arrivalZonedDateBack);

        Ticket ticketBack = new Ticket.Builder(SVO_AIRPORT, VVO_AIRPORT)
                .setDepartureDate(departureZonedDateBack)
                .setArrivalDate(arrivalZonedDateBack)
                .build();

        hoursDiff = 6;
        assertThat(ticketBack.getFlightDurationInMinutes()).isEqualTo(hoursDiff * MINUTES_IN_HOURS);
    }

    @Test
    public void getFlightDurationInMinutesSameRegionsTest() {
        ZonedDateTime departureZonedDate = ZonedDateTime.
                of(2020, 1,1,
                        12, 20, 0, 0,
                        ZoneId.of(SVO_AIRPORT.getTimeRegion()));
        ZonedDateTime arrivalZonedDate = departureZonedDate.withHour(14);

        Ticket ticket = new Ticket.Builder(SVO_AIRPORT, SVO_AIRPORT)
                .setDepartureDate(departureZonedDate)
                .setArrivalDate(arrivalZonedDate)
                .build();

        long flightHours = 2;
        assertThat(ticket.getFlightDurationInMinutes()).isEqualTo(flightHours * MINUTES_IN_HOURS);
    }

}
