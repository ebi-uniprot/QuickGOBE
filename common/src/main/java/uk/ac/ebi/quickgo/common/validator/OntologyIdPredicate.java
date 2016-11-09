package uk.ac.ebi.quickgo.common.validator;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Validate ontology terms identifiers.
 *
 * @author Tony Wardell
 * Date: 18/10/2016
 * Time: 10:52
 * Created with IntelliJ IDEA.
 */
public class OntologyIdPredicate {
    private static final Pattern GO_ID_FORMAT = Pattern.compile("^GO:[0-9]{7}$", CASE_INSENSITIVE);
    private static final Pattern ECO_ID_FORMAT = Pattern.compile("^ECO:[0-9]{7}$", CASE_INSENSITIVE);

    /**
     * Provide a Predicate that can be used to test a String for validity as a GO Term id.
     * @return predicate to use in test.
     */
    public static Predicate<String> isValidGOTermId() {return id -> GO_ID_FORMAT.matcher(id).matches();}

    public static Predicate<String> isValidECOTermId() {return id -> ECO_ID_FORMAT.matcher(id).matches();}
}