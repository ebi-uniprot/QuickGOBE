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
class XORelationsFieldConverter implements FieldConverter<OBOTerm.XORelation> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(XORelationsFieldConverter.class);

    @Override public Optional<OBOTerm.XORelation> apply(String s) {
        // format: id|term|namespace|url|relation
        OBOTerm.XORelation xORel = new OBOTerm.XORelation();

        List<FlatField> fields = newFlatFieldFromDepth(2).parse(s).getFields();
        if (fields.size() == 5) {
            xORel.id = nullOrString(fields.get(0).buildString());
            xORel.term = nullOrString(fields.get(1).buildString());
            xORel.namespace = nullOrString(fields.get(2).buildString());
            xORel.url = nullOrString(fields.get(3).buildString());
            xORel.relation = nullOrString(fields.get(4).buildString());
            return Optional.of(xORel);
        } else {
            LOGGER.warn("Could not parse flattened xORel: {}", s);
        }
        return Optional.empty();
    }
}
