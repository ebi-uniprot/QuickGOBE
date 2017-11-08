package uk.ac.ebi.quickgo.ontology.model;

import uk.ac.ebi.quickgo.ontology.common.OntologyType;

import java.util.List;

/**
 * Hold the configuration for a particular ontology type.
 *
 * @author Tony Wardell
 * Date: 29/06/2017
 * Time: 09:32
 * Created with IntelliJ IDEA.
 */
public class OntologySpecifier {

    public final OntologyType ontologyType;
    public final List<OntologyRelationType> allowedRelations;

    public OntologySpecifier(OntologyType ontologyType,
            List<OntologyRelationType> allowedRelations) {
        this.ontologyType = ontologyType;
        this.allowedRelations = allowedRelations;
    }
}
