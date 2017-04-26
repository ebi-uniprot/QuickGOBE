package uk.ac.ebi.quickgo.rest.period;

import java.util.Optional;

import org.junit.Test;


import static org.hamcrest.Matchers.equalTo;


import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Test the methods that PeriodParser implements.
 * @author Tony Wardell
 * Date: 26/04/2017
 * Time: 09:26
 * Created with IntelliJ IDEA.
 */
public class PeriodParserTest {

    private static final DateModifier DATE_MODIFIER = target -> null;
    private final PeriodParser periodParser = getPeriodParser();

    @Test
    public void parseValidInputString() throws Exception {
        AlarmClock testAlarmClock = new AlarmClockImpl(DATE_MODIFIER, DATE_MODIFIER);

        Optional<AlarmClock> alarmClock = periodParser.parse("START-END");

        assertThat(alarmClock.get(), equalTo(testAlarmClock));
    }

    @Test
    public void nullInputStringReturnsEmptyOptional(){
        Optional<AlarmClock> alarmClock = periodParser.parse(null);

        assertThat(alarmClock, equalTo(Optional.empty()));
    }

    @Test
    public void zeroLengthInputStringReturnsEmptyOptional(){
        Optional<AlarmClock> alarmClock = periodParser.parse("");

        assertThat(alarmClock, equalTo(Optional.empty()));
    }

    @Test
    public void tooManyToSymbolsReturnsEmptyOptional(){
        Optional<AlarmClock> alarmClock = periodParser.parse("YA-BOO-SUCKS");

        assertThat(alarmClock, equalTo(Optional.empty()));
    }

    @Test
    public void toDateModifierReturnsEmptyOptional() throws Exception {
        PeriodParser periodParser = getPeriodParserToDateModifierReturnsEmptyOptional();

        Optional<AlarmClock> alarmClock = periodParser.parse("START-END");

        assertThat(alarmClock, equalTo(Optional.empty()));
    }

    private PeriodParser getPeriodParser() {
        return new PeriodParser() {
            @Override protected Optional<DateModifier> toDateModifier(String input) {
                return Optional.of(DATE_MODIFIER);
            }
        };
    }

    private PeriodParser getPeriodParserToDateModifierReturnsEmptyOptional() {
        return new PeriodParser() {
            @Override protected Optional<DateModifier> toDateModifier(String input) {
                return Optional.empty();
            }
        };
    }
}
