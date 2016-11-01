package uk.ac.ebi.quickgo.ontology.controller.validation;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Validate GO Terms.
 *
 * @author Tony Wardell
 * Date: 18/10/2016
 * Time: 10:52
 * Created with IntelliJ IDEA.
 */
public class GOTermPredicate {
    private static final Pattern GO_ID_FORMAT = Pattern.compile("^GO:[0-9]{7}$");

    /**
     * Provide a Predicate that can be used to test a String for validity as a GO Term id.
     * @return predicate to use in test.
     */
    public static Predicate<String> isValidGOTermId() {return id -> GO_ID_FORMAT.matcher(id).matches();}
}
