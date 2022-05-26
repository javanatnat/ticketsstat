package statistics;

import org.junit.jupiter.api.Test;
import ru.javanatnat.ticketsstat.statistics.PercentileFlightCalculatorImpl;
import ru.javanatnat.ticketsstat.statistics.PercentileMethodCalc;
import ru.javanatnat.ticketsstat.tickets.Ticket;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.javanatnat.ticketsstat.tickets.AirportDirectory.*;
import static ru.javanatnat.ticketsstat.tickets.AirportDirectory.TLV_AIRPORT;

public class PercentileFlightCalculatorImplTest {
    private static final List<Ticket> MANY_TICKETS = List.of(
            new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                    .setDepartureDate(
                            ZonedDateTime.of(2020, 5,12,
                                    16, 20, 0, 0,
                                    ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                    .setArrivalDate(
                            ZonedDateTime.of(2020, 5, 12,
                                    22, 40, 0, 0,
                                    ZoneId.of(TLV_AIRPORT.getTimeRegion())))
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
                    .build(),
            new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                    .setDepartureDate(
                            ZonedDateTime.of(2020, 5,12,
                                    16, 20, 0, 0,
                                    ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                    .setArrivalDate(
                            ZonedDateTime.of(2020, 5, 12,
                                    17, 20, 0, 0,
                                    ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                    .build(),
            new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                    .setDepartureDate(
                            ZonedDateTime.of(2020, 5,12,
                                    16, 20, 0, 0,
                                    ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                    .setArrivalDate(
                            ZonedDateTime.of(2020, 5, 12,
                                    19, 12, 0, 0,
                                    ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                    .build(),
            new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                    .setDepartureDate(
                            ZonedDateTime.of(2020, 5,12,
                                    16, 20, 0, 0,
                                    ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                    .setArrivalDate(
                            ZonedDateTime.of(2020, 5, 12,
                                    20, 25, 0, 0,
                                    ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                    .build(),
            new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                    .setDepartureDate(
                            ZonedDateTime.of(2020, 5,12,
                                    16, 20, 0, 0,
                                    ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                    .setArrivalDate(
                            ZonedDateTime.of(2020, 5, 12,
                                    23, 11, 0, 0,
                                    ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                    .build(),
            new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                    .setDepartureDate(
                            ZonedDateTime.of(2020, 5,12,
                                    16, 20, 0, 0,
                                    ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                    .setArrivalDate(
                            ZonedDateTime.of(2020, 5, 12,
                                    23, 11, 0, 0,
                                    ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                    .build(),
            new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                    .setDepartureDate(
                            ZonedDateTime.of(2020, 5,12,
                                    16, 20, 0, 0,
                                    ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                    .setArrivalDate(
                            ZonedDateTime.of(2020, 5, 12,
                                    23, 22, 0, 0,
                                    ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                    .build(),
            new Ticket.Builder(VVO_AIRPORT, TLV_AIRPORT)
                    .setDepartureDate(
                            ZonedDateTime.of(2020, 5,12,
                                    16, 20, 0, 0,
                                    ZoneId.of(VVO_AIRPORT.getTimeRegion())))
                    .setArrivalDate(
                            ZonedDateTime.of(2020, 5, 12,
                                    23, 50, 0, 0,
                                    ZoneId.of(TLV_AIRPORT.getTimeRegion())))
                    .build()
    );

    @Test
    public void testDefMethodCalc() {
        assertThat(PercentileFlightCalculatorImpl.getDefaultMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
    }

    @Test
    public void testCalcInMinutes1Element() {
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
                        .build()
        );

        PercentileFlightCalculatorImpl calculator50p = new PercentileFlightCalculatorImpl(
                tickets, (byte) 50, VVO_AIRPORT, TLV_AIRPORT);

        assertThat(calculator50p.getMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
        BigDecimal result = calculator50p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(800);

        PercentileFlightCalculatorImpl calculator90p = new PercentileFlightCalculatorImpl(
                tickets, (byte) 90, VVO_AIRPORT, TLV_AIRPORT);

        assertThat(calculator90p.getMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
        result = calculator90p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(800);
    }

    @Test
    public void testCalcInMinutes2Elements() {
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

        PercentileFlightCalculatorImpl calculator50p = new PercentileFlightCalculatorImpl(
                tickets, (byte) 50, VVO_AIRPORT, TLV_AIRPORT);

        assertThat(calculator50p.getMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
        BigDecimal result = calculator50p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(600);
        assertThat(result.doubleValue()).isEqualTo(600.0);

        PercentileFlightCalculatorImpl calculator90p = new PercentileFlightCalculatorImpl(
                tickets, (byte) 90, VVO_AIRPORT, TLV_AIRPORT);

        assertThat(calculator90p.getMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
        result = calculator90p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(600);
        assertThat(result.doubleValue()).isEqualTo(600.0);
    }

    @Test
    public void testCalcInMinutes4Many() {
        PercentileFlightCalculatorImpl calculator50p = new PercentileFlightCalculatorImpl(
                MANY_TICKETS, (byte) 50, VVO_AIRPORT, TLV_AIRPORT);

        assertThat(calculator50p.getMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
        BigDecimal result = calculator50p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(665);
        assertThat(result.doubleValue()).isEqualTo(665);

        PercentileFlightCalculatorImpl calculator90p = new PercentileFlightCalculatorImpl(
                MANY_TICKETS, (byte) 90, VVO_AIRPORT, TLV_AIRPORT);

        assertThat(calculator90p.getMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
        result = calculator90p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(842);
        assertThat(result.doubleValue()).isEqualTo(842.0);

        PercentileFlightCalculatorImpl calculator99p = new PercentileFlightCalculatorImpl(
                MANY_TICKETS, (byte) 99, VVO_AIRPORT, TLV_AIRPORT);

        assertThat(calculator99p.getMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
        result = calculator99p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(870);
        assertThat(result.doubleValue()).isEqualTo(870);
    }

    @Test
    public void testCalcInMinutes4ManyMethodLinear() {
        PercentileFlightCalculatorImpl calculator50p = new PercentileFlightCalculatorImpl(
                MANY_TICKETS, (byte) 50, VVO_AIRPORT, TLV_AIRPORT, PercentileMethodCalc.LINEAR_INTERPOLATION_C0);

        assertThat(calculator50p.getMethodCalc()).isEqualTo(PercentileMethodCalc.LINEAR_INTERPOLATION_C0);
        BigDecimal result = calculator50p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(732);
        assertThat(result.doubleValue()).isEqualTo(732.5);

        PercentileFlightCalculatorImpl calculator90p = new PercentileFlightCalculatorImpl(
                MANY_TICKETS, (byte) 90, VVO_AIRPORT, TLV_AIRPORT, PercentileMethodCalc.LINEAR_INTERPOLATION_C0);

        assertThat(calculator90p.getMethodCalc()).isEqualTo(PercentileMethodCalc.LINEAR_INTERPOLATION_C0);
        result = calculator90p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(867);
        assertThat(result.doubleValue()).isEqualTo(867.2);
    }

    @Test
    public void testCalcInMinutes4ManyZeroPercentile() {
        PercentileFlightCalculatorImpl calculator0p = new PercentileFlightCalculatorImpl(
                MANY_TICKETS, (byte) 0, VVO_AIRPORT, TLV_AIRPORT);

        assertThat(calculator0p.getMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
        BigDecimal result = calculator0p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(480);
        assertThat(result.doubleValue()).isEqualTo(480.0);
    }

    @Test
    public void testCalcInMinutes4Many100Percentile() {
        PercentileFlightCalculatorImpl calculator100p = new PercentileFlightCalculatorImpl(
                MANY_TICKETS, (byte) 100, VVO_AIRPORT, TLV_AIRPORT);

        assertThat(calculator100p.getMethodCalc()).isEqualTo(PercentileMethodCalc.NEAREST_RANG);
        BigDecimal result = calculator100p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(870);
        assertThat(result.doubleValue()).isEqualTo(870.0);
    }

    @Test
    public void testCalcInMinutes4ManyZeroPercentileLinear() {
        PercentileFlightCalculatorImpl calculator0p = new PercentileFlightCalculatorImpl(
                MANY_TICKETS, (byte) 0, VVO_AIRPORT, TLV_AIRPORT, PercentileMethodCalc.LINEAR_INTERPOLATION_C0);

        assertThat(calculator0p.getMethodCalc()).isEqualTo(PercentileMethodCalc.LINEAR_INTERPOLATION_C0);
        BigDecimal result = calculator0p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(480);
        assertThat(result.doubleValue()).isEqualTo(480.0);
    }

    @Test
    public void testCalcInMinutes4Many100PercentileLinear() {
        PercentileFlightCalculatorImpl calculator100p = new PercentileFlightCalculatorImpl(
                MANY_TICKETS, (byte) 100, VVO_AIRPORT, TLV_AIRPORT, PercentileMethodCalc.LINEAR_INTERPOLATION_C0);

        assertThat(calculator100p.getMethodCalc()).isEqualTo(PercentileMethodCalc.LINEAR_INTERPOLATION_C0);
        BigDecimal result = calculator100p.calcInMinutes();
        assertThat(result.longValue()).isEqualTo(870);
        assertThat(result.doubleValue()).isEqualTo(870.0);
    }

    @Test
    public void testIncorrectParams() {
        assertThatThrownBy(() -> new PercentileFlightCalculatorImpl(null, (byte) 50, VVO_AIRPORT, SVO_AIRPORT))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PercentileFlightCalculatorImpl(new ArrayList<>(), (byte) 50, null, SVO_AIRPORT))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PercentileFlightCalculatorImpl(new ArrayList<>(), (byte) 50, VVO_AIRPORT, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PercentileFlightCalculatorImpl(
                new ArrayList<>(), (byte) 50, VVO_AIRPORT, SVO_AIRPORT, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PercentileFlightCalculatorImpl(new ArrayList<>(), (byte) 50, VVO_AIRPORT, SVO_AIRPORT))
                .isInstanceOf(IllegalArgumentException.class);

        Ticket ticket = new Ticket.Builder(VVO_AIRPORT, VVO_AIRPORT)
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
                .build();

        assertThatThrownBy(() -> new PercentileFlightCalculatorImpl(
                List.of(ticket),
                (byte) 50,
                VVO_AIRPORT,
                VVO_AIRPORT))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PercentileFlightCalculatorImpl(
                List.of(ticket),
                (byte) -50,
                VVO_AIRPORT,
                VVO_AIRPORT))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PercentileFlightCalculatorImpl(
                List.of(ticket),
                (byte) 102,
                VVO_AIRPORT,
                VVO_AIRPORT))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
