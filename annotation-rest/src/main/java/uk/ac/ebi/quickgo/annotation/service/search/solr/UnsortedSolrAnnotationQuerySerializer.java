package uk.ac.ebi.quickgo.annotation.service.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.FieldQuery;
import uk.ac.ebi.quickgo.rest.search.solr.UnsortedSolrQuerySerializer;

import java.util.Set;

import static uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter.SOLR_FIELD_SEPARATOR;

/**
 * Specialisation of UnsortedSolrQuerySerializer that can deal with a wild card value in a search field.
 *
 * @author Tony Wardell
 * Date: 22/08/2017
 * Time: 15:26
 * Created with IntelliJ IDEA.
 */
public class UnsortedSolrAnnotationQuerySerializer extends UnsortedSolrQuerySerializer {

    private final String wildCardField;

    public UnsortedSolrAnnotationQuerySerializer(Set<String> termsQueryCompatibleFields, String wildCardField) {
        super(termsQueryCompatibleFields);
        this.wildCardField = wildCardField;
    }

    @Override
    public String visit(FieldQuery query) {
        if(isWildCardSearch(query)){
            return buildWildCardQuery(query);
        } else {
            return super.visit(query);
        }
    }

    private boolean isWildCardSearch(FieldQuery query) {
        return query.field().equals(wildCardField) && query.value().equals("*");
    }

    private String buildWildCardQuery(FieldQuery query) {
        return "(" + query.field() + SOLR_FIELD_SEPARATOR + "[ '' TO *])";
    }
}
