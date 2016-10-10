package uk.ac.ebi.quickgo.ontology.coterms;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Tony Wardell
 * Date: 10/10/2016
 * Time: 10:50
 * Created with IntelliJ IDEA.
 */
public class CoTermLimitTest {

    CoTermLimit comTermLimit;

    @Before
    public void setup(){
        comTermLimit = new CoTermLimit(50);
    }

    @Test
    public void limitIsConstructorLimitIfRequestedLimitIsNull(){
        assertThat(comTermLimit.workoutLimit(null), is(50));
    }

    @Test
    public void limitIsConstructorLimitIfRequestedLimitIsZero(){
        assertThat(comTermLimit.workoutLimit("0"), is(50));
    }

    @Test
    public void limitIsIntegerMaxIfRequestedLimitIsFull(){
        assertThat(comTermLimit.workoutLimit("ALL"), is(Integer.MAX_VALUE));
    }

    @Test
    public void argumentOfAllIsNotCaseSensitive(){
        assertThat(comTermLimit.workoutLimit("alL"), is(Integer.MAX_VALUE));
    }

    @Test
    public void integerAsArgumentGetsCreatedAsLimit(){
        assertThat(comTermLimit.workoutLimit("37"), is(37));
    }

    @Test(expected = IllegalArgumentException.class)
    public void notANumberAsArgumentThrowsException(){
        comTermLimit.workoutLimit("boo");
    }

    @Test
    public void limitIsConstructorLimitIfRequestedLimitIsEmptyString(){
        assertThat(comTermLimit.workoutLimit("   "), is(50));
    }
}
