package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Responsible for converting {@link Annotation} instances into GPAD model instances, whilst also
 * providing a method to retrieve header information.
 *
 * Created 19/01/17
 * @author Edd
 */
public class GPADAnnotationConverter {
    public List<String> getHeaderLines(QueryResult<Annotation> result) {
        return asList("# GPAD", "# Date: "+ new Date());
    }

    public String convert(Annotation annotation) {
        return "value1\tvalue2\tvalue3\tvalue4";
    }
}