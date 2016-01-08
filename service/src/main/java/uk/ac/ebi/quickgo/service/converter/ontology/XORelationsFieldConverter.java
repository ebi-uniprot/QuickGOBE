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
 * Created 01/12/15
 * @author Edd
 */
class XORelationsFieldConverter implements FieldConverter<OBOTerm.XORelation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XORelationsFieldConverter.class);
    private static final int FIELD_COUNT = 5;

    @Override public Optional<OBOTerm.XORelation> apply(String fieldsStr) {
        // format: id|term|namespace|url|relation

        List<FlatField> fields = newFlatField().parse(fieldsStr).getFields();
        if (fields.size() == FIELD_COUNT) {
            OBOTerm.XORelation xORel = new OBOTerm.XORelation();
            xORel.id = cleanFieldValue(fields.get(0).buildString());
            xORel.term = cleanFieldValue(fields.get(1).buildString());
            xORel.namespace = cleanFieldValue(fields.get(2).buildString());
            xORel.url = cleanFieldValue(fields.get(3).buildString());
            xORel.relation = cleanFieldValue(fields.get(4).buildString());
            return Optional.of(xORel);
        } else {
            LOGGER.warn("Could not parse flattened xORel: {}", fieldsStr);
        }
        return Optional.empty();
    }
}
