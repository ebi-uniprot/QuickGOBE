package uk.ac.ebi.quickgo.rest.period;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Test the methods that PeriodParser implements.
 * @author Tony Wardell
 * Date: 26/04/2017
 * Time: 09:26
 * Created with IntelliJ IDEA.
 */
class PeriodParserTest {

    private static final AlarmClock ALARM_CLOCK = target -> null;
    private final PeriodParser periodParser = getPeriodParser();

    @Test
    void parseValidInputString()  {
        Optional<AlarmClock> alarmClock = periodParser.parse("START-END");

        assertTrue(alarmClock.isPresent());
    }

    @Test
    void nullInputStringReturnsEmptyOptional(){
        Optional<AlarmClock> alarmClock = periodParser.parse(null);

        assertThat(alarmClock, equalTo(Optional.empty()));
    }

    @Test
    void zeroLengthInputStringReturnsEmptyOptional(){
        Optional<AlarmClock> alarmClock = periodParser.parse("");

        assertThat(alarmClock, equalTo(Optional.empty()));
    }

    @Test
    void toDateModifierReturnsEmptyOptional()  {
        PeriodParser periodParser = getPeriodParserToAlarmClockReturnsEmptyOptional();

        Optional<AlarmClock> alarmClock = periodParser.parse("START-END");

        assertThat(alarmClock, equalTo(Optional.empty()));
    }

    private PeriodParser getPeriodParser() {
        return new PeriodParser() {
            @Override protected Optional<AlarmClock> getPeriod(String input) {
                return Optional.of(ALARM_CLOCK);
            }
        };
    }

    private PeriodParser getPeriodParserToAlarmClockReturnsEmptyOptional() {
        return new PeriodParser() {
            @Override protected Optional<AlarmClock> getPeriod(String input) {
                return Optional.empty();
            }
        };
    }
}
