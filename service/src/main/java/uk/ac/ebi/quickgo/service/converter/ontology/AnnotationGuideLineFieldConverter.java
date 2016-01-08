package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.ff.flatfield.FlatField;
import uk.ac.ebi.quickgo.service.converter.FieldConverter;
import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatFieldFromDepth;

/**
 * Created 01/12/15
 * @author Edd
 */
class AnnotationGuideLineFieldConverter implements FieldConverter<OBOTerm.AnnotationGuideLine> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationGuideLineFieldConverter.class);
    private static final int FIELD_COMPONENT_COUNT = 2;

    @Override public Optional<OBOTerm.AnnotationGuideLine> apply(String g) {
        // format: description|url

        List<FlatField> fields = newFlatFieldFromDepth(2).parse(g).getFields();
        if (fields.size() == FIELD_COMPONENT_COUNT) {
            OBOTerm.AnnotationGuideLine ag = new OBOTerm.AnnotationGuideLine();
            ag.description = cleanFieldValue(fields.get(0).buildString());
            ag.url = cleanFieldValue(fields.get(1).buildString());
            return Optional.of(ag);
        } else {
            LOGGER.warn("Could not parse flattened annotationGuidelines: {}", g);
        }
        return Optional.empty();
    }

}
