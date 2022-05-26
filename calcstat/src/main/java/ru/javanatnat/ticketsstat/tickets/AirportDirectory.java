package ru.javanatnat.ticketsstat.tickets;

import java.util.HashMap;
import java.util.Map;

public class AirportDirectory {
    public static final Airport VVO_AIRPORT = new Airport("VVO", "Владивосток", "Asia/Vladivostok");
    public static final Airport TLV_AIRPORT = new Airport("TLV", "Тель-Авив", "Israel");
    public static final Airport SVO_AIRPORT = new Airport("SVO", "Москва","Europe/Moscow");

    private static final Map<String, Airport> airports;

    static {
        airports = new HashMap<>();
        airports.put(VVO_AIRPORT.getIATACode(), VVO_AIRPORT);
        airports.put(TLV_AIRPORT.getIATACode(), TLV_AIRPORT);
        airports.put(SVO_AIRPORT.getIATACode(), SVO_AIRPORT);
    }

    public static Airport getAirportByCode(String code) {
        return airports.get(code);
    }
}
