package uk.ac.ebi.quickgo.rest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rantunes on 29/11/15.
 */
public class TestUtil {
    public static <T> Set<T> asSet(T... elements) {
        return new LinkedHashSet<>(Arrays.asList(elements));
    }

    public static <T> Collection<T> convertToCollection(Stream<T> stream) {
        return stream.collect(Collectors.toList());
    }
}
