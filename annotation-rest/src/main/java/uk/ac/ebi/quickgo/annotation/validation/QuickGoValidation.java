package uk.ac.ebi.quickgo.annotation.validation;

import java.util.function.Function;

/**
 * @author Tony Wardell
 * Date: 10/11/2016
 * Time: 13:41
 * Created with IntelliJ IDEA.
 */
public interface QuickGoValidation {

    Function<String, String> toDb = (value) -> value.substring(0, value.indexOf(":")).toLowerCase();
}
