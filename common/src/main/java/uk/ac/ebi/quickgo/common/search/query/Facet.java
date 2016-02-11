package uk.ac.ebi.quickgo.common.search.query;

/**
 * Expresses a view of a query result.
 *
 * A facet will break down the result set of a query into categories. Where each category is a distinct value in the
 * chosen field.
 *
 * Think of a facet, as a field in a SQL group by clause.
 */
public class Facet extends AbstractField {
        public Facet(String field) {
            super(field);
        }
}
