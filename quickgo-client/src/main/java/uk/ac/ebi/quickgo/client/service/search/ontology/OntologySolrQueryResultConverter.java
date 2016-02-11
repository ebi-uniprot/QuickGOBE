package uk.ac.ebi.quickgo.client.service.search.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.converter.ontology.ECODocConverter;
import uk.ac.ebi.quickgo.client.service.converter.ontology.GODocConverter;
import uk.ac.ebi.quickgo.common.search.solr.AbstractSolrQueryResultConverter;
import uk.ac.ebi.quickgo.common.search.solr.SolrQueryResultHighlightingConverter;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the Solr results into {@link OntologyTerm} instances.
 */
public class OntologySolrQueryResultConverter extends AbstractSolrQueryResultConverter<OntologyTerm> {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private GODocConverter goDocConverter;
    private ECODocConverter ecoDocConverter;
    private DocumentObjectBinder documentObjectBinder;

    public OntologySolrQueryResultConverter(DocumentObjectBinder documentObjectBinder,
            GODocConverter goDocConverter,
            ECODocConverter ecoDocConverter,
            Map<String, String> fieldNameMap) {
        super(new SolrQueryResultHighlightingConverter(fieldNameMap));

        Preconditions.checkArgument(documentObjectBinder != null, "Document Object Binder can not be null");
        Preconditions.checkArgument(goDocConverter != null, "Go document converter can not be null");
        Preconditions.checkArgument(ecoDocConverter != null, "ECO document converter can not be null");

        this.documentObjectBinder = documentObjectBinder;
        this.goDocConverter = goDocConverter;
        this.ecoDocConverter = ecoDocConverter;
    }

    protected List<OntologyTerm> convertResults(SolrDocumentList results) {
        assert results != null : "Results list cannot be null";

        List<OntologyDocument> solrTermDocs = documentObjectBinder.getBeans(OntologyDocument.class, results);

        List<OntologyTerm> domainTerms = new ArrayList<>(solrTermDocs.size());

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