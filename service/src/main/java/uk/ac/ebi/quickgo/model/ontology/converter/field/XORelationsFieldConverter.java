package uk.ac.ebi.quickgo.model.ontology.converter.field;

import uk.ac.ebi.quickgo.ff.delim.FlatField;
import uk.ac.ebi.quickgo.model.FieldConverter;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.parseFlatField;

/**
 * Created 01/12/15
 * @author Edd
 */
public class XORelationsFieldConverter implements FieldConverter<OBOTerm.XORelation> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(XORelationsFieldConverter.class);

    @Override public Optional<OBOTerm.XORelation> apply(String s) {
        // format: id|term|namespace|url|relation
        OBOTerm.XORelation xORel = new OBOTerm.XORelation();

        List<FlatField> fields = parseFlatField(s).getFields();
        if (fields.size() == 5) {
            xORel.id = nullOrString(fields.get(0).buildStringFromLevel(1));
            xORel.term = nullOrString(fields.get(1).buildStringFromLevel(1));
            xORel.namespace = nullOrString(fields.get(2).buildStringFromLevel(1));
            xORel.url = nullOrString(fields.get(3).buildStringFromLevel(1));
            xORel.relation = nullOrString(fields.get(4).buildStringFromLevel(1));
            return Optional.of(xORel);
        } else {
            LOGGER.warn("Could not parse flattened xORel: {}", s);
        }
        return Optional.empty();
    }
}
