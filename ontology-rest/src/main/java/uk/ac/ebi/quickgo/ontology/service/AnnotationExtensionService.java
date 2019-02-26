package uk.ac.ebi.quickgo.ontology.service;

import java.util.Map;

/**
 * Methods called in AnnotationExtensionController. See controller for detail documentation
 */
public interface AnnotationExtensionService {
    Map<String, Object> getDisplayAbleAnnotationExtensionRelationsHierarchy();

    Map<String, Object> getAllPossibleRelationsForDomain(String domain);

    Map<String, Object> isAnnotationExtensionValidForGoTerm(String candidate, String goTermId);
}
