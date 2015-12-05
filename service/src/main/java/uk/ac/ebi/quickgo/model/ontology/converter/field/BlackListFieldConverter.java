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
public class BlackListFieldConverter implements FieldConverter<OBOTerm.BlacklistItem> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackListFieldConverter.class);

    @Override public Optional<OBOTerm.BlacklistItem> apply(String s) {
        // format: geneProductId|geneProductDB|reason|category|method
        OBOTerm.BlacklistItem blacklistItem = new OBOTerm.BlacklistItem();

        List<FlatField> fields = parseFlatFieldFromLevel(s, 2).getFields();

        if (fields.size() == 5) {
            blacklistItem.geneProductId = nullOrString(fields.get(0).buildStringFromLevel(2));
            blacklistItem.geneProductDb = nullOrString(fields.get(1).buildStringFromLevel(2));
            blacklistItem.reason = nullOrString(fields.get(2).buildStringFromLevel(2));
            blacklistItem.category = nullOrString(fields.get(3).buildStringFromLevel(2));
            blacklistItem.method = nullOrString(fields.get(4).buildStringFromLevel(2));
            return Optional.of(blacklistItem);
        } else {
            LOGGER.warn("Could not parse flattened blacklist: {}", s);
        }
        return Optional.empty();
    }
}
