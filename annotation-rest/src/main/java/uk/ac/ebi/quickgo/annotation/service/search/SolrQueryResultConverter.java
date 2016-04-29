package uk.ac.ebi.quickgo.annotation.service.search;

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

/**
 * Turns SolrDocuments into instances of the Annotation DTO.
 *
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 16:44
 * Created with IntelliJ IDEA.
 */
public class SolrQueryResultConverter extends AbstractSolrQueryResultConverter<Annotation> {

    private final DocumentObjectBinder documentObjectBinder;
    private final AnnotationDocConverter annotationDocConverter;

    private static final Map<String,String> emptyMap = new HashedMap();

    public SolrQueryResultConverter(DocumentObjectBinder documentObjectBinder,
            AnnotationDocConverter annotationDocConverter){
        super(new SolrQueryResultHighlightingConverter(emptyMap));

        Preconditions.checkArgument(documentObjectBinder != null, "Document Object Binder cannot be null");
        Preconditions.checkArgument(annotationDocConverter != null, "Gene product document converter cannot be null");

        this.documentObjectBinder = documentObjectBinder;
        this.annotationDocConverter = annotationDocConverter;
    }

    /**
     * Turns SolrDocuments into instances of the Annotation DTO.
     * @param results
     * @return
     */
    @Override protected List<Annotation> convertResults(SolrDocumentList results) {
        assert results != null : "Results list cannot be null";

        List<AnnotationDocument> solrTermDocs = documentObjectBinder.getBeans(AnnotationDocument.class, results);
        List<Annotation> domainObjs = new ArrayList<>(solrTermDocs.size());

        solrTermDocs.stream()
                .map(annotationDocConverter::convert)
                .forEach(domainObjs::add);

        assert domainObjs.size() == results.size();
        return domainObjs;
    }
}
