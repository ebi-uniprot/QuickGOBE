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
 * Defines the conversion of a {@link String} representing black list information, to a
 * corresponding {@link uk.ac.ebi.quickgo.service.model.ontology.OBOTerm.BlacklistItem} instance.
 *
 * Created 01/12/15
 * @author Edd
 */
class BlackListFieldConverter implements FieldConverter<OBOTerm.BlacklistItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlackListFieldConverter.class);
    private static final int FIELD_COUNT = 5;

    @Override public Optional<OBOTerm.BlacklistItem> apply(String fieldsStr) {
        // format: geneProductId|geneProductDB|reason|category|method

        List<FlatField> fields = newFlatField().parse(fieldsStr).getFields();

        if (fields.size() == FIELD_COUNT) {
            OBOTerm.BlacklistItem blacklistItem = new OBOTerm.BlacklistItem();
            blacklistItem.geneProductId = cleanFieldValue(fields.get(0).buildString());
            blacklistItem.geneProductDb = cleanFieldValue(fields.get(1).buildString());
            blacklistItem.reason = cleanFieldValue(fields.get(2).buildString());
            blacklistItem.category = cleanFieldValue(fields.get(3).buildString());
            blacklistItem.method = cleanFieldValue(fields.get(4).buildString());
            return Optional.of(blacklistItem);
        } else {
            LOGGER.warn("Could not parse flattened blacklist: {}", fieldsStr);
        }
        return Optional.empty();
    }
}
