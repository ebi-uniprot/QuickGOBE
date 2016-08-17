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
 * Defines the conversion of a {@link String} representing information about a synonym, to a
 * corresponding {@link OBOTerm.Synonym} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>name|type</li>
 * </ul>
 * <p>
 * Created 01/12/15
 * @author Edd
 */
class SynonymsFieldConverter implements FieldConverter<OBOTerm.Synonym> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynonymsFieldConverter.class);
    private static final int FIELD_COUNT = 2;

    @Override public Optional<OBOTerm.Synonym> apply(String fieldsStr) {

        List<FlatField> fields = FlatFieldBuilder.parse(fieldsStr).getFields();
        if (fields.size() == FIELD_COUNT) {
            OBOTerm.Synonym synonym = new OBOTerm.Synonym();
            synonym.name = cleanFieldValue(fields.get(0).buildString());
            synonym.type = cleanFieldValue(fields.get(1).buildString());
            return Optional.of(synonym);
        } else {
            LOGGER.warn("Could not parse flattened synonym: {}", fieldsStr);
        }
        return Optional.empty();
    }
}
