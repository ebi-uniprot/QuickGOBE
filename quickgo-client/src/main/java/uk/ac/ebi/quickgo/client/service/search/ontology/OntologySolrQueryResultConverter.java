package uk.ac.ebi.quickgo.client.service.search.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.converter.ontology.ECODocConverter;
import uk.ac.ebi.quickgo.client.service.converter.ontology.GODocConverter;
import uk.ac.ebi.quickgo.common.search.solr.AbstractSolrQueryResultConverter;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

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
            ECODocConverter ecoDocConverter) {
        this.documentObjectBinder = requireNonNull(documentObjectBinder);
        this.goDocConverter = requireNonNull(goDocConverter);
        this.ecoDocConverter = requireNonNull(ecoDocConverter);
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