package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.ff.flatfield.FlatField;
import uk.ac.ebi.quickgo.service.converter.FieldConverter;
import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatField;

/**
 * Defines the conversion of a {@link String} representing an audit record, to a
 * corresponding {@link uk.ac.ebi.quickgo.service.model.ontology.OBOTerm.History} instance.
 *
 * Created 01/12/15
 * @author Edd
 */
class HistoryFieldConverter implements FieldConverter<OBOTerm.History> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryFieldConverter.class);
    private static final int FIELD_COUNT = 5;

    @Override public Optional<OBOTerm.History> apply(String fieldsStr) {
        // format: name|timestamp|action|category|text

        List<FlatField> fields = newFlatField().parse(fieldsStr).getFields();
        if (fields.size() == FIELD_COUNT) {
            OBOTerm.History historicalInfo = new OBOTerm.History();
            historicalInfo.name = cleanFieldValue(fields.get(0).buildString());
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
