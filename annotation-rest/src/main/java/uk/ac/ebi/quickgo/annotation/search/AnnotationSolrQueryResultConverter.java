package uk.ac.ebi.quickgo.annotation.search;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverter;
import uk.ac.ebi.quickgo.rest.search.solr.AbstractSolrQueryResultConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrQueryResultHighlightingConverter;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.HashedMap;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 16:44
 * Created with IntelliJ IDEA.
 */
public class AnnotationSolrQueryResultConverter extends AbstractSolrQueryResultConverter<Annotation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationSolrQueryResultConverter.class);

    private final DocumentObjectBinder documentObjectBinder;
    private final AnnotationDocConverter annotationDocConverter;

    private static final Map<String,String> emptyMap = new HashedMap();

    public AnnotationSolrQueryResultConverter(DocumentObjectBinder documentObjectBinder,
            AnnotationDocConverter annotationDocConverter){
        super(new SolrQueryResultHighlightingConverter(emptyMap));

        Preconditions.checkArgument(documentObjectBinder != null, "Document Object Binder cannot be null");
        Preconditions.checkArgument(annotationDocConverter != null, "Gene product document converter cannot be null");

        this.documentObjectBinder = documentObjectBinder;
        this.annotationDocConverter = annotationDocConverter;
    }

    @Override protected List<Annotation> convertResults(SolrDocumentList results) {
        assert results != null : "Results list cannot be null";

        List<AnnotationDocument> solrTermDocs = documentObjectBinder.getBeans(AnnotationDocument.class, results);

        List<Annotation> domainTerms = new ArrayList<>(solrTermDocs.size());

        solrTermDocs.stream()
                .map(annotationDocConverter::convert)
                .forEach(domainTerms::add);

        assert domainTerms.size() == results.size();

        return domainTerms;
    }
}
