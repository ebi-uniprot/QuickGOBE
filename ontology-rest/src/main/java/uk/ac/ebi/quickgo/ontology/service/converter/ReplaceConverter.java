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
 * Defines the conversion of a {@link String} representing term replacement, to a
 * corresponding {@link uk.ac.ebi.quickgo.ontology.model.OBOTerm.Replace} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>goId|replacementType</li>
 * </ul>
 * <p>
 * @author Ricardo Antunes
 */
class ReplaceConverter implements FieldConverter<OBOTerm.Replace> {
    private static final Logger logger = LoggerFactory.getLogger(ReplaceConverter.class);

    private static final int FIELD_COUNT = 2;

    @Override public Optional<OBOTerm.Replace> apply(String replaceString) {
        List<FlatField> fields = FlatFieldBuilder.newFlatField().parse(replaceString).getFields();

        if(fields.size() == FIELD_COUNT) {
            OBOTerm.Replace replace = new OBOTerm.Replace();
            replace.id = cleanFieldValue(fields.get(0).buildString());
            replace.type = cleanFieldValue(fields.get(1).buildString());

            return Optional.of(replace);
        } else {
            logger.warn("Could not parse flattened replace: {}", replaceString);
        }

        return Optional.empty();
    }
}
