package uk.ac.ebi.quickgo.model.ontology.converter.field;

import uk.ac.ebi.quickgo.ff.delim.FlatField;
import uk.ac.ebi.quickgo.model.FieldConverter;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.parseFlatField;
import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.parseFlatFieldFromLevel;

/**
 * Created 01/12/15
 * @author Edd
 */
public class AnnotationGuideLineFieldConverter implements FieldConverter<OBOTerm.AnnotationGuideLine> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationGuideLineFieldConverter.class);

    @Override public Optional<OBOTerm.AnnotationGuideLine> apply(String g) {
        // format: geneProductId|geneProductDB|reason|category|method
        OBOTerm.AnnotationGuideLine ag = new OBOTerm.AnnotationGuideLine();

        List<FlatField> fields = parseFlatFieldFromLevel(g, 2).getFields();
        if (fields.size() == 2) {
            ag.description = nullOrString(fields.get(0).buildStringFromLevel(2));
            ag.url = nullOrString(fields.get(1).buildStringFromLevel(2));
            return Optional.of(ag);
        } else {
            LOGGER.warn("Could not parse flattened annotationGuidelines: {}", g);
        }
        return Optional.empty();
    }

}
