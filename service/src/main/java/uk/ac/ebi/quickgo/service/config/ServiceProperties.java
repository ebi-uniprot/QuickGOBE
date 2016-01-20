package uk.ac.ebi.quickgo.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the service layer.
 *
 * Created 02/12/15
 * @author Edd
 */
@Component
public class ServiceProperties {

    private static final String COMMA = ",";
    private static final String DEFAULT_ONTOLOGY_SEARCH_RETURN_FIELDS = "id,name,ontologyType";

    // a comma delimited string of stored solr fields,
    // which ontology searches should return
    private final String[] ontologySearchSolrReturnedFields;

    @Autowired
    public ServiceProperties(
            @Value("${service.ontology.search.solr.returned.fields:" + DEFAULT_ONTOLOGY_SEARCH_RETURN_FIELDS + "}") String ontologySearchSolrReturnedFields) {
        this.ontologySearchSolrReturnedFields = ontologySearchSolrReturnedFields.split(COMMA);
    }

    public String[] getOntologySearchSolrReturnedFields() {
        return ontologySearchSolrReturnedFields;
    }
}
