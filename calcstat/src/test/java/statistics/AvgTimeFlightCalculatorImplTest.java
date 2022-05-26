package statistics;

import org.junit.jupiter.api.Test;
import ru.javanatnat.ticketsstat.statistics.AvgTimeFlightCalculatorImpl;
import ru.javanatnat.ticketsstat.tickets.Ticket;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.javanatnat.ticketsstat.tickets.AirportDirectory.*;

public class AvgTimeFlightCalculatorImplTest {
    @Test
    public void testCalcInMinutesDoubleResult() {
        List<Ticket> tickets = List.of(
                new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                .setDepartureDate(
                        ZonedDateTime.of(2020, 5,12,
                                16, 20, 0, 0,
                                ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                .setArrivalDate(
                        ZonedDateTime.of(2020, 5, 12,
                                22, 40, 0, 0,
                                ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                .setCarrierCode("TK")
                .setStops((byte) 3)
                .setPrice(21400)
                .build(),
                new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                        .setDepartureDate(
                                ZonedDateTime.of(2020, 5,12,
                                        16, 20, 0, 0,
                                        ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                        .setArrivalDate(
                                ZonedDateTime.of(2020, 5, 12,
                                        19, 20, 0, 0,
                                        ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                        .setCarrierCode("TK")
                        .setStops((byte) 2)
                        .setPrice(15400)
                        .build(),
                new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                        .setDepartureDate(
                                ZonedDateTime.of(2020, 5,12,
                                        16, 20, 0, 0,
                                        ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                        .setArrivalDate(
                                ZonedDateTime.of(2020, 5, 12,
                                        17, 40, 0, 0,
                                        ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                        .setCarrierCode("TK")
                        .setStops((byte) 1)
                        .setPrice(12400)
                        .build()
        );
        AvgTimeFlightCalculatorImpl calculator = new AvgTimeFlightCalculatorImpl(tickets, VVO_AIRPORT, TLV_AIRPORT);
        BigDecimal avgDuration = calculator.calcInMinutes();
        assertThat(avgDuration.longValue()).isEqualTo(633);
        assertThat(avgDuration.doubleValue()).isEqualTo(633.3);
    }

    @Test
    public void testCalcInMinutesLongResult() {
        List<Ticket> tickets = List.of(
                new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                        .setDepartureDate(
                                ZonedDateTime.of(2020, 5,12,
                                        16, 20, 0, 0,
                                        ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                        .setArrivalDate(
                                ZonedDateTime.of(2020, 5, 12,
                                        22, 40, 0, 0,
                                        ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                        .setCarrierCode("TK")
                        .setStops((byte) 3)
                        .setPrice(21400)
                        .build(),
                new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                        .setDepartureDate(
                                ZonedDateTime.of(2020, 5,12,
                                        16, 20, 0, 0,
                                        ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                        .setArrivalDate(
                                ZonedDateTime.of(2020, 5, 12,
                                        19, 20, 0, 0,
                                        ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                        .setCarrierCode("TK")
                        .setStops((byte) 2)
                        .setPrice(15400)
                        .build()
        );
        AvgTimeFlightCalculatorImpl calculator = new AvgTimeFlightCalculatorImpl(tickets, VVO_AIRPORT, TLV_AIRPORT);
        BigDecimal avgDuration = calculator.calcInMinutes();
        assertThat(avgDuration.longValue()).isEqualTo(700);
        assertThat(avgDuration.doubleValue()).isEqualTo(700.0);
    }

    @Test
    public void testIncorrectParams() {
        assertThatThrownBy(() -> new AvgTimeFlightCalculatorImpl(null, VVO_AIRPORT, SVO_AIRPORT))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AvgTimeFlightCalculatorImpl(new ArrayList<>(), null, SVO_AIRPORT))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AvgTimeFlightCalculatorImpl(new ArrayList<>(), VVO_AIRPORT, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AvgTimeFlightCalculatorImpl(new ArrayList<>(), VVO_AIRPORT, SVO_AIRPORT))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AvgTimeFlightCalculatorImpl(
                List.of(
                        new Ticket.Builder(VVO_AIRPORT, VVO_AIRPORT)
                                .setDepartureDate(
                                        ZonedDateTime.of(2020, 5,12,
                                                16, 20, 0, 0,
                                                ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                                .setArrivalDate(
                                        ZonedDateTime.of(2020, 5, 12,
                                                17, 10, 0, 0,
                                                ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                                .setCarrierCode("TK")
                                .setStops((byte) 0)
                                .setPrice(12400)
                                .build()
                ),
                VVO_AIRPORT,
                VVO_AIRPORT))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
