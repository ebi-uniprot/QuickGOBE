package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverter;
import uk.ac.ebi.quickgo.rest.search.solr.AbstractSolrQueryResultConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrResponseAggregationConverter;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocumentList;

/**
 * Turns SolrDocuments into instances of the Annotation DTO.
 *
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 16:44
 * Created with IntelliJ IDEA.
 */
class SolrQueryResultConverter extends AbstractSolrQueryResultConverter<Annotation> {

    private final DocumentObjectBinder documentObjectBinder;
    private final AnnotationDocConverter annotationDocConverter;

    public SolrQueryResultConverter(DocumentObjectBinder documentObjectBinder,
            AnnotationDocConverter annotationDocConverter,
            SearchServiceConfig.AnnotationCompositeRetrievalConfig annotationRetrievalConfig) {
        super();

        Preconditions.checkArgument(documentObjectBinder != null, "Document Object Binder cannot be null");
        Preconditions.checkArgument(annotationDocConverter != null, "Annotation document converter cannot be null");
        Preconditions.checkArgument(annotationRetrievalConfig != null,
                "Annotation retrieval configuration cannot be null");

        this.documentObjectBinder = documentObjectBinder;
        this.annotationDocConverter = annotationDocConverter;

        this.setAggregationConverter(new SolrResponseAggregationConverter(annotationRetrievalConfig));
    }

    /**
     * Turns SolrDocuments into instances of the Annotation DTO.
     * @param results a list of SolrDocuments that meet search criteria (if applicable)
     * @return a corresponding list of Annotations converted from the Solr Document list argument
     */
    @Override protected List<Annotation> convertResults(SolrDocumentList results) {
        assert results != null : "Results list cannot be null";

        List<AnnotationDocument> solrTermDocs = documentObjectBinder.getBeans(AnnotationDocument.class, results);

        List<Annotation> domainObjs = solrTermDocs.stream()
                .map(annotationDocConverter::convert)
                .collect(Collectors.toList());

        assert domainObjs.size() == results.size();
        return domainObjs;
    }
}
