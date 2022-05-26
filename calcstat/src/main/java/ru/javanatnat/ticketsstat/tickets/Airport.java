package ru.javanatnat.ticketsstat.tickets;

public class Airport {
    private final String IATACode;
    private final String cityName;
    private final String timeRegion;

    public Airport(String IATACode, String cityName, String timeRegion) {
        this.IATACode = IATACode;
        this.cityName = cityName;
        this.timeRegion = timeRegion;
    }

    public String getIATACode() {
        return IATACode;
    }

    public String getCityName() {
        return cityName;
    }

    public String getTimeRegion() {
        return timeRegion;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "IATA code='" + IATACode + '\'' +
                ", cityName='" + cityName + '\'' +
                ", timeRegion='" + timeRegion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Airport airport)) {
            return false;
        }

        if (!IATACode.equals(airport.IATACode)) {
            return false;
        }

        return cityName.equals(airport.cityName);
    }

    @Override
    public int hashCode() {
        int result = IATACode.hashCode();
        result = 31 * result + cityName.hashCode();
        return result;
    }
}
