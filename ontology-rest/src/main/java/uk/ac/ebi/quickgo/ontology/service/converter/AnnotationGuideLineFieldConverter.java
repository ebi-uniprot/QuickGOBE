package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import uk.ac.ebi.quickgo.common.converter.FlatField;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the conversion of a {@link String} representing an annotation guideline, to a
 * corresponding {@link OBOTerm.AnnotationGuideLine} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>description|url</li>
 * </ul>
 * <p>
 * Created 01/12/15
 * @author Edd
 */
class AnnotationGuideLineFieldConverter implements FieldConverter<OBOTerm.AnnotationGuideLine> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationGuideLineFieldConverter.class);
    private static final int FIELD_COUNT = 2;

    @Override public Optional<OBOTerm.AnnotationGuideLine> apply(String fieldsStr) {
        List<FlatField> fields = FlatFieldBuilder.newFlatField().parse(fieldsStr).getFields();

        if (fields.size() == FIELD_COUNT) {
            OBOTerm.AnnotationGuideLine ag = new OBOTerm.AnnotationGuideLine();
            ag.description = cleanFieldValue(fields.get(0).buildString());
            ag.url = cleanFieldValue(fields.get(1).buildString());
            return Optional.of(ag);
        } else {
            LOGGER.warn("Could not parse flattened annotationGuidelines: {}", fieldsStr);
        }
        return Optional.empty();
    }
}