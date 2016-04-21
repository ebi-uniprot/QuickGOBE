package uk.ac.ebi.quickgo.annotation.common;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Annotation repository interface used to perform searches over its contents.
 *
 * Created 14/04/16
 * @author Edd
 */
public interface AnnotationRepository extends SolrCrudRepository<AnnotationDocument, String> {

}
