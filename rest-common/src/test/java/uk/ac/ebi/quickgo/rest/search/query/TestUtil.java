package uk.ac.ebi.quickgo.rest.search.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by rantunes on 29/11/15.
 */
public class TestUtil {
    public static <T> Set<T> asSet(T... elements) {
        return new LinkedHashSet<>(Arrays.asList(elements));
    }
}
