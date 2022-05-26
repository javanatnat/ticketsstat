package ru.javanatnat.ticketsstat.tickets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ru.javanatnat.ticketsstat.tickets.AirportDirectory.*;

public class TicketParser {
    private static final Logger LOG = LoggerFactory.getLogger(TicketParser.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String TICKETS = "tickets";

    public static List<Ticket> getTickets(String fileName) {
        LOG.info("start parse tickets from file: {}", fileName);
        File file = new File(fileName);
        JsonNode tree;
        List<Ticket> resultTickets = new ArrayList<>();

        try {
            tree = OBJECT_MAPPER.readTree(file);
            JsonNode jsonTickets = tree.get(TICKETS);

            if (jsonTickets.isArray()) {
                for (JsonNode jsonTicket : jsonTickets) {

                    JsonNodeTicketParser parser = new JsonNodeTicketParser(jsonTicket);

                    Airport originAirport = parser.getOriginAirport();
                    Airport destinationAirport = parser.getDestinationAirport();

                    if (originAirport == null || destinationAirport == null) {
                        continue;
                    }

                    ZonedDateTime departureZonedDate = parser.getDepartureZonedDate(originAirport);
                    ZonedDateTime arrivalZonedDate = parser.getArrivalZonedDate(destinationAirport);

                    if (departureZonedDate == null || arrivalZonedDate == null) {
                        continue;
                    }

                    Ticket ticket = new Ticket.Builder(originAirport, destinationAirport)
                            .setDepartureDate(departureZonedDate)
                            .setArrivalDate(arrivalZonedDate)
                            .setCarrierCode(parser.getCarrier())
                            .setStops(parser.getStops())
                            .setPrice(parser.getPrice())
                            .build();

                    LOG.debug("add ticket: {}", ticket);
                    resultTickets.add(ticket);
                }
            }
        } catch (Exception e) {
            LOG.error("error while parse file {} : {}, {}",
                    fileName,
                    "Невозможно прочитать файл в формате json, формат файла нарушен",
                    e);
            throw new RuntimeException("Невозможно прочитать файл в формате json, формат файла нарушен", e);
        }

        if (resultTickets.isEmpty()) {
            LOG.error("error while parse file {} : {}", fileName,
                    "В результате чтения файла \"" + fileName + "\" не найден ни один билет!");
            throw new RuntimeException("В результате чтения файла \"" + fileName + "\" не найден ни один билет!");
        }
        LOG.info("end parse tickets from file: {}, ticket.count = {}", fileName, resultTickets.size());
        return resultTickets;
    }

    private static class JsonNodeTicketParser {
        private static final String ORIGIN = "origin";
        private static final String ORIGIN_NAME = "origin_name";
        private static final String DESTINATION = "destination";
        private static final String DESTINATION_NAME = "destination_name";
        private static final String DEPARTURE_DATE = "departure_date";
        private static final String DEPARTURE_TIME = "departure_time";
        private static final String ARRIVAL_DATE = "arrival_date";
        private static final String ARRIVAL_TIME = "arrival_time";
        private static final String CARRIER = "carrier";
        private static final String STOPS = "stops";
        private static final String PRICE = "price";

        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy");
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
        private final JsonNode jsonTicket;

        JsonNodeTicketParser(JsonNode jsonTicket) {
            this.jsonTicket = jsonTicket;
        }

        Airport getOriginAirport() {
            return getAirport(ORIGIN);
        }

        Airport getDestinationAirport() {
            return getAirport(DESTINATION);
        }

        private Airport getAirport(String fieldAirportCode) {
            if (hasNode(fieldAirportCode)) {
                String airportCode = getStringValue(fieldAirportCode);
                return getAirportByCode(airportCode);
            }
            return null;
        }

        ZonedDateTime getDepartureZonedDate(Airport airport) {
            return getZonedDate(DEPARTURE_DATE, DEPARTURE_TIME, airport);
        }

        ZonedDateTime getArrivalZonedDate(Airport airport) {
            return getZonedDate(ARRIVAL_DATE, ARRIVAL_TIME, airport);
        }

        private ZonedDateTime getZonedDate(
                String dateField,
                String timeField,
                Airport airport
        ) {
            if (hasNode(dateField) && hasNode(timeField)) {
                if (isDate(dateField) && isTime(timeField)) {
                    String valueDate = getStringValue(dateField);
                    String valueTime = getStringValue(timeField);

                    return getZoneDateTime(
                            valueDate,
                            valueTime,
                            airport.getTimeRegion());
                }
            }
            return null;
        }

        String getCarrier() {
            if (hasNode(CARRIER)) {
                return getStringValue(CARRIER);
            }
            return "";
        }

        byte getStops() {
            if (hasNode(STOPS)) {
                return getByteValue(STOPS);
            }
            return 0;
        }

        int getPrice() {
            if (hasNode(PRICE)) {
                return getIntValue(PRICE);
            }
            return 0;
        }

        private String getStringValue(String field) {
            return jsonTicket.get(field).asText();
        }

        private boolean hasNode(String field) {
            JsonNode value = jsonTicket.get(field);
            return !(value == null || value.isNull());
        }

        private int getIntValue(String field) {
            return jsonTicket.get(field).asInt();
        }

        private byte getByteValue(String field) {
            return (byte) getIntValue(field);
        }

        private boolean isDate(String field) {
            return isByFormat(field, DATE_FORMATTER);
        }

        private boolean isTime(String field) {
            return isByFormat(field, TIME_FORMATTER);
        }

        private boolean isByFormat(String field, DateTimeFormatter formatter) {
            String value = getStringValue(field);
            try {
                formatter.parse(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private static ZonedDateTime getZoneDateTime(
                String dateValue,
                String timeValue,
                String timeRegion
        ) {
            String dateTimeValue = dateValue + " " + timeValue;
            return getZoneDateTime(dateTimeValue, timeRegion);
        }

        private static ZonedDateTime getZoneDateTime(String dateTimeValue, String timeRegion) {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeValue, DATE_TIME_FORMATTER);
            return ZonedDateTime.of(localDateTime, ZoneId.of(timeRegion));
        }
    }
}
