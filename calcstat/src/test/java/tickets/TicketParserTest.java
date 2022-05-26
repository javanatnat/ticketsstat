package tickets;

import org.junit.jupiter.api.Test;
import ru.javanatnat.ticketsstat.tickets.Ticket;
import ru.javanatnat.ticketsstat.tickets.TicketParser;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.javanatnat.ticketsstat.tickets.AirportDirectory.*;

public class TicketParserTest {
    private static final String CORRECT_FILE = "src/test/resources/data/tickets.json";
    private static final String CORRECT_ONE_TICKET_FILE = "src/test/resources/data/ticketOneCorrect.json";
    private static final String BREAK_FILE = "src/test/resources/data/ticketsBreak.json";
    private static final String EMPTY_FILE = "src/test/resources/data/ticketsEmpty.json";
    private static final String NOT_CORRECT_FILE = "src/test/resources/data/ticketsNotCorrect.json";

    @Test
    public void testCorrectFile() {
        List<Ticket> tickets = TicketParser.getTickets(CORRECT_FILE);
        assertThat(tickets).isNotEmpty();
        assertThat(tickets.size()).isEqualTo(2);
        assertThat(tickets).contains(
                new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                        .setDepartureDate(
                                ZonedDateTime.of(2020, 5,12,
                                        16, 20, 0, 0,
                                        ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                        .setArrivalDate(
                                ZonedDateTime.of(2020, 5, 12,
                                        22, 10, 0, 0,
                                        ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                        .setCarrierCode("TK")
                        .setStops((byte) 3)
                        .setPrice(12400)
                        .build(),
                new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                        .setDepartureDate(
                                ZonedDateTime.of(2020, 5,12,
                                        17, 20, 0, 0,
                                        ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                        .setArrivalDate(
                                ZonedDateTime.of(2020, 5, 12,
                                        23, 50, 0, 0,
                                        ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                        .setCarrierCode("S7")
                        .setStops((byte) 1)
                        .setPrice(13100)
                        .build()
        );
    }

    @Test
    public void testCorrectOneTicketFile() {
        List<Ticket> tickets = TicketParser.getTickets(CORRECT_ONE_TICKET_FILE);
        assertThat(tickets).isNotEmpty();
        assertThat(tickets.size()).isEqualTo(1);
        assertThat(tickets).contains(
                new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                        .setDepartureDate(
                                ZonedDateTime.of(2020, 5,12,
                                        16, 20, 0, 0,
                                        ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                        .setArrivalDate(
                                ZonedDateTime.of(2020, 5, 12,
                                        22, 10, 0, 0,
                                        ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                        .setCarrierCode("TK")
                        .setStops((byte) 3)
                        .setPrice(12400)
                        .build()
        );
    }

    @Test
    public void testBreakFile() {
        assertThatThrownBy(() -> TicketParser.getTickets(BREAK_FILE))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEmptyFile() {
        assertThatThrownBy(() -> TicketParser.getTickets(EMPTY_FILE))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testNotCorrectFile() {
        assertThatThrownBy(() -> TicketParser.getTickets(NOT_CORRECT_FILE))
                .isInstanceOf(RuntimeException.class);
    }
}
