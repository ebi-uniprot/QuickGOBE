package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import uk.ac.ebi.quickgo.common.converter.FlatField;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;

/**
 * Defines the conversion of a {@link String} representing black list information, to a
 * corresponding {@link OBOTerm.BlacklistItem} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>geneProductId|geneProductDB|reason|category|method</li>
 * </ul>
 * <p>
 * Created 01/12/15
 * @author Edd
 */
class BlackListFieldConverter implements FieldConverter<OBOTerm.BlacklistItem> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackListFieldConverter.class);
    private static final int FIELD_COUNT = 9;

    @Override public Optional<OBOTerm.BlacklistItem> apply(String fieldsStr) {
        List<FlatField> fields = newFlatField().parse(fieldsStr).getFields();

        if (fields.size() == FIELD_COUNT) {
            OBOTerm.BlacklistItem blacklistItem = new OBOTerm.BlacklistItem();
            blacklistItem.geneProductId = cleanFieldValue(fields.get(0).buildString());
            blacklistItem.category = cleanFieldValue(fields.get(1).buildString());
            blacklistItem.entityType = cleanFieldValue(fields.get(2).buildString());
            blacklistItem.entityId = cleanFieldValue(fields.get(3).buildString());
            blacklistItem.taxonId = cleanFieldValue(fields.get(4).buildString());
            blacklistItem.entityName = cleanFieldValue(fields.get(5).buildString());
            blacklistItem.ancestorGoId = cleanFieldValue(fields.get(6).buildString());
            blacklistItem.reason = cleanFieldValue(fields.get(7).buildString());
            blacklistItem.method = cleanFieldValue(fields.get(8).buildString());
            return Optional.of(blacklistItem);
        } else {
            LOGGER.warn("Could not parse flattened blacklist: {}", fieldsStr);
        }

        return Optional.empty();
    }
}