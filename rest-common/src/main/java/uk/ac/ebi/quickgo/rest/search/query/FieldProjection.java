package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Expresses a field that should be returned as part of the search results.
 *
 * Created 10/02/16
 * @author Edd
 */
public class FieldProjection extends AbstractField {

    public FieldProjection(String field) {
        super(field);
    }

    @Override public String toString() {
        return "FieldProjection{" +
                "field='" + field + '\'' +
                '}';
    }
}
