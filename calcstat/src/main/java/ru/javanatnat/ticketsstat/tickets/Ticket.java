package ru.javanatnat.ticketsstat.tickets;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Ticket {
    private final Airport originAirport;
    private final Airport destinationAirport;
    private final ZonedDateTime departureDate;
    private final ZonedDateTime arrivalDate;
    private final String carrierCode;
    private final byte stops;
    private final int price;

    private Ticket(Builder builder) {
        originAirport = builder.originAirport;
        destinationAirport = builder.destinationAirport;
        departureDate = builder.departureDate;
        arrivalDate = builder.arrivalDate;
        carrierCode = builder.carrierCode;
        stops = builder.stops;
        price = builder.price;

        Objects.requireNonNull(originAirport);
        Objects.requireNonNull(destinationAirport);
        Objects.requireNonNull(departureDate);
        Objects.requireNonNull(arrivalDate);
    }

    public static class Builder {
        private final Airport originAirport;
        private final Airport destinationAirport;
        private ZonedDateTime departureDate;
        private ZonedDateTime arrivalDate;
        private String carrierCode;
        private byte stops;
        private int price;

        public Builder(Airport originAirport, Airport destinationAirport) {
            this.originAirport = originAirport;
            this.destinationAirport = destinationAirport;
        }

        public Builder setDepartureDate(ZonedDateTime departureDate) {
            this.departureDate = departureDate;
            return this;
        }

        public Builder setArrivalDate(ZonedDateTime arrivalDate) {
            this.arrivalDate = arrivalDate;
            return this;
        }

        public Builder setCarrierCode(String carrierCode) {
            this.carrierCode = carrierCode;
            return this;
        }

        public Builder setStops(byte stops) {
            this.stops = stops;
            return this;
        }

        public Builder setPrice(int price) {
            this.price = price;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }

    public Airport getOriginAirport() {
        return originAirport;
    }

    public Airport getDestinationAirport() {
        return destinationAirport;
    }

    public ZonedDateTime getDepartureDate() {
        return departureDate;
    }

    public ZonedDateTime getArrivalDate() {
        return arrivalDate;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public byte getStops() {
        return stops;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "originAirport=" + originAirport +
                ", destinationAirport=" + destinationAirport +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +
                ", carrierCode='" + carrierCode + '\'' +
                ", stops=" + stops +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket ticket)) {
            return false;
        }

        if (!originAirport.equals(ticket.originAirport)) {
            return false;
        }

        if (!destinationAirport.equals(ticket.destinationAirport)) {
            return false;
        }

        if (!departureDate.equals(ticket.departureDate)) {
            return false;
        }

        if (!arrivalDate.equals(ticket.arrivalDate)) {
            return false;
        }
        return Objects.equals(carrierCode, ticket.carrierCode);
    }

    @Override
    public int hashCode() {
        int result = originAirport.hashCode();
        result = 31 * result + destinationAirport.hashCode();
        result = 31 * result + departureDate.hashCode();
        result = 31 * result + arrivalDate.hashCode();
        result = 31 * result + (carrierCode != null ? carrierCode.hashCode() : 0);
        return result;
    }

    public long getFlightDurationInMinutes() {
        long until = departureDate.until(arrivalDate, ChronoUnit.MINUTES);
        if (departureDate.compareTo(arrivalDate) > 0) {
            until *= -1;
        }
        return until;
    }
}
