package tickets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javanatnat.ticketsstat.tickets.AirportDirectory.*;

public class AirportDirectoryTest {
    @Test
    public void getAirportByCodeTest() {
        assertThat(getAirportByCode("VVO")).isEqualTo(VVO_AIRPORT);
        assertThat(getAirportByCode("TLV")).isEqualTo(TLV_AIRPORT);
        assertThat(getAirportByCode("TTL")).isNull();
    }
}
