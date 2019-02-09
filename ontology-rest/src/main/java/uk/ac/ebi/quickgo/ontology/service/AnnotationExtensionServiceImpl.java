package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ff.loader.ontology.OntologyGraphicsSourceLoader;
import uk.ac.ebi.quickgo.model.ontology.go.AnnotationExtensionRelations;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Below implementation is just delegating the calls to legacy code.
 */
public class AnnotationExtensionServiceImpl implements AnnotationExtensionService {

    private AnnotationExtensionRelations goAnnotationExtensionRelations;

    @Autowired
    public AnnotationExtensionServiceImpl(OntologyGraphicsSourceLoader ontologyGraphicsSourceLoader) {
        goAnnotationExtensionRelations = ontologyGraphicsSourceLoader.getGoAnnotationExtensionRelations();
    }

    public Map<String, Object> getDisplayAbleAnnotationExtensionRelationsHierarchy() {
        return (Map<String, Object>) goAnnotationExtensionRelations.toGraph();
    }

    public Map<String, Object> getAllPossibleRelationsForDomain(String domain) {
        return goAnnotationExtensionRelations.forDomain(domain);
    }

    public Map<String, Object> isAnnotationExtensionValidForGoTerm(String candidate, String goTermId) {
        boolean isValid=true;
        String message="";

        try {
            goAnnotationExtensionRelations.validate(goTermId,candidate);
        } catch (Exception e) {
            isValid=false;
            message=e.getMessage();
        }

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("valid", isValid);
        retMap.put("message",message);

        return retMap;
    }
}
