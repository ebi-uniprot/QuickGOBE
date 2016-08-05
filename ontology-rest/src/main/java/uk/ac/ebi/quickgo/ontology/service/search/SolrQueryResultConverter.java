package uk.ac.ebi.quickgo.ontology.service.search;

import uk.ac.ebi.quickgo.rest.search.solr.AbstractSolrQueryResultConverter;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrQueryResultHighlightingConverter;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the Solr results into {@link OBOTerm} instances.
 */
public class SolrQueryResultConverter extends AbstractSolrQueryResultConverter<OBOTerm> {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private GODocConverter goDocConverter;
    private ECODocConverter ecoDocConverter;
    private DocumentObjectBinder documentObjectBinder;

    public SolrQueryResultConverter(DocumentObjectBinder documentObjectBinder,
            GODocConverter goDocConverter,
            ECODocConverter ecoDocConverter,
            Map<String, String> fieldNameMap) {

        Preconditions.checkArgument(documentObjectBinder != null, "Document Object Binder can not be null");
        Preconditions.checkArgument(goDocConverter != null, "Go document converter can not be null");
        Preconditions.checkArgument(ecoDocConverter != null, "ECO document converter can not be null");

        this.documentObjectBinder = documentObjectBinder;
        this.goDocConverter = goDocConverter;
        this.ecoDocConverter = ecoDocConverter;

        this.setQueryResultHighlightingConverter(new SolrQueryResultHighlightingConverter(fieldNameMap));
    }

    @Override protected List<OBOTerm> convertResults(SolrDocumentList results) {
        assert results != null : "Results list cannot be null";

        List<OntologyDocument> solrTermDocs = documentObjectBinder.getBeans(OntologyDocument.class, results);

        List<OBOTerm> domainTerms = new ArrayList<>(solrTermDocs.size());

        for (OntologyDocument ontologyDoc : solrTermDocs) {
            switch (ontologyDoc.ontologyType) {
                case "GO":
                    domainTerms.add(goDocConverter.convert(ontologyDoc));
                    break;
                case "ECO":
                    domainTerms.add(ecoDocConverter.convert(ontologyDoc));
                    break;
                default:
                    LOGGER.error("Unable to convert Solr document ({}) --> domain DTO {}",
                            ontologyDoc.id, ontologyDoc.ontologyType);
            }
        }

        assert domainTerms.size() == results.size() : "Number of converted terms does not match number of solr docs";

        return domainTerms;
    }
}