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
 * Defines the conversion of a {@link String} representing an audit record, to a
 * corresponding {@link OBOTerm.History} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>name|timestamp|action|category|text</li>
 * </ul>
 * <p>
 * Created 01/12/15
 * @author Edd
 */
class HistoryFieldConverter implements FieldConverter<OBOTerm.History> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryFieldConverter.class);
    private static final int FIELD_COUNT = 5;

    @Override public Optional<OBOTerm.History> apply(String fieldsStr) {
        List<FlatField> fields = FlatFieldBuilder.newFlatField().parse(fieldsStr).getFields();

        if (fields.size() == FIELD_COUNT) {
            OBOTerm.History historicalInfo = new OBOTerm.History();
            historicalInfo.timestamp = cleanFieldValue(fields.get(1).buildString());
            historicalInfo.action = cleanFieldValue(fields.get(2).buildString());
            historicalInfo.category = cleanFieldValue(fields.get(3).buildString());
            historicalInfo.text = cleanFieldValue(fields.get(4).buildString());
            return Optional.of(historicalInfo);
        } else {
            LOGGER.warn("Could not parse flattened history: {}", fieldsStr);
        }

        return Optional.empty();
    }
}
