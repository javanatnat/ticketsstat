package statistics;

import org.junit.jupiter.api.Test;
import ru.javanatnat.ticketsstat.statistics.MinutesFormatter;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class MinutesFormatterTest {
    @Test
    public void parseCountWithOnlyOneDay() {
        MinutesFormatter formatter = new MinutesFormatter(BigDecimal.valueOf(1440));
        assertThat(formatter.getWholeDays()).isEqualTo(1);
        assertThat(formatter.getWholeHoursWithoutDays()).isEqualTo(0);
        assertThat(formatter.getLongMinutesRest()).isEqualTo(0);
    }

    @Test
    public void parseCountWithDays() {
        MinutesFormatter formatter = new MinutesFormatter(BigDecimal.valueOf(1530));
        assertThat(formatter.getWholeDays()).isEqualTo(1);
        assertThat(formatter.getWholeHoursWithoutDays()).isEqualTo(1);
        assertThat(formatter.getLongMinutesRest()).isEqualTo(30);
    }

    @Test
    public void parseCountWith2Hours() {
        MinutesFormatter formatter = new MinutesFormatter(BigDecimal.valueOf(120));
        assertThat(formatter.getWholeDays()).isEqualTo(0);
        assertThat(formatter.getWholeHoursWithoutDays()).isEqualTo(2);
        assertThat(formatter.getLongMinutesRest()).isEqualTo(0);
    }

    @Test
    public void parseCountWithHours() {
        MinutesFormatter formatter = new MinutesFormatter(BigDecimal.valueOf(121.5));
        assertThat(formatter.getWholeDays()).isEqualTo(0);
        assertThat(formatter.getWholeHoursWithoutDays()).isEqualTo(2);
        assertThat(formatter.getLongMinutesRest()).isEqualTo(1);
        assertThat(formatter.getDoubleMinutesRest()).isEqualTo(1.5);
    }

    @Test
    public void parseCountWithOnlyMinutes() {
        MinutesFormatter formatter = new MinutesFormatter(BigDecimal.valueOf(53.7));
        assertThat(formatter.getWholeDays()).isEqualTo(0);
        assertThat(formatter.getWholeHoursWithoutDays()).isEqualTo(0);
        assertThat(formatter.getLongMinutesRest()).isEqualTo(53);
        assertThat(formatter.getDoubleMinutesRest()).isEqualTo(53.7);
    }

    @Test
    public void parseCountWithNegateValue() {
        MinutesFormatter formatter = new MinutesFormatter(BigDecimal.valueOf(-53.7));
        assertThat(formatter.getWholeDays()).isEqualTo(0);
        assertThat(formatter.getWholeHoursWithoutDays()).isEqualTo(0);
        assertThat(formatter.getLongMinutesRest()).isEqualTo(53);
        assertThat(formatter.getDoubleMinutesRest()).isEqualTo(53.7);
    }

    @Test
    public void parseZeroCount() {
        MinutesFormatter formatter = new MinutesFormatter(BigDecimal.valueOf(0));
        assertThat(formatter.getWholeDays()).isEqualTo(0);
        assertThat(formatter.getWholeHoursWithoutDays()).isEqualTo(0);
        assertThat(formatter.getLongMinutesRest()).isEqualTo(0);
        assertThat(formatter.getDoubleMinutesRest()).isEqualTo(0);
    }
}
