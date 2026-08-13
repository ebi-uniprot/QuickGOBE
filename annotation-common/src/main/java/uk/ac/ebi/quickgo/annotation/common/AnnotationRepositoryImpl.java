package uk.ac.ebi.quickgo.annotation.common;

import org.apache.solr.client.solrj.SolrClient;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.common.SolrCrudRepositoryImpl;

public class AnnotationRepositoryImpl extends SolrCrudRepositoryImpl<AnnotationDocument> implements AnnotationRepository {
    public AnnotationRepositoryImpl(SolrClient solrClient) {
        super(solrClient, SolrCollectionName.ANNOTATION, AnnotationDocument.class);
    }
}