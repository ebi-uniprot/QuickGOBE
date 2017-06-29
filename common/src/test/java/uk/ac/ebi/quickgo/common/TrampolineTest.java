package uk.ac.ebi.quickgo.common;

import java.util.Optional;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Tony Wardell
 * Date: 29/06/2017
 * Time: 11:04
 * Created with IntelliJ IDEA.
 */
public class TrampolineTest {

    @Test
    public void computeWithTrampoline() {
        int factorial = Factorial.createTrampoline(4, 1).compute();
        assertThat(factorial, equalTo(24));
    }
}

final class Factorial {
    static Trampoline<Integer> createTrampoline(final int n, final int sum) {
        if (n == 1) {
            return new Trampoline<Integer>() {
                public Integer getValue() { return sum; }
            };
        }

        return new Trampoline<Integer>() {
            public Optional<Trampoline<Integer>> nextTrampoline() {
                return Optional.of(createTrampoline(n - 1, sum * n));
            }
        };
    }
}

