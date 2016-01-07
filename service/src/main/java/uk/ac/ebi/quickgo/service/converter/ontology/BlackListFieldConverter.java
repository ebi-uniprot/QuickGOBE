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
class BlackListFieldConverter implements FieldConverter<OBOTerm.BlacklistItem> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackListFieldConverter.class);

    @Override public Optional<OBOTerm.BlacklistItem> apply(String s) {
        // format: geneProductId|geneProductDB|reason|category|method
        OBOTerm.BlacklistItem blacklistItem = new OBOTerm.BlacklistItem();

        List<FlatField> fields = newFlatFieldFromDepth(2).parse(s).getFields();

        if (fields.size() == 5) {
            blacklistItem.geneProductId = nullOrString(fields.get(0).buildString());
            blacklistItem.geneProductDb = nullOrString(fields.get(1).buildString());
            blacklistItem.reason = nullOrString(fields.get(2).buildString());
            blacklistItem.category = nullOrString(fields.get(3).buildString());
            blacklistItem.method = nullOrString(fields.get(4).buildString());
            return Optional.of(blacklistItem);
        } else {
            LOGGER.warn("Could not parse flattened blacklist: {}", s);
        }
        return Optional.empty();
    }
}
