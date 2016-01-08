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
class SynonymsFieldConverter implements FieldConverter<OBOTerm.Synonym> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynonymsFieldConverter.class);
    private static final int FIELD_COMPONENT_COUNT = 2;

    @Override public Optional<OBOTerm.Synonym> apply(String s) {
        // format: name|type
        List<FlatField> fields = newFlatFieldFromDepth(2).parse(s).getFields();
        if (fields.size() == FIELD_COMPONENT_COUNT) {
            OBOTerm.Synonym synonym = new OBOTerm.Synonym();
            synonym.synonymName = cleanFieldValue(fields.get(0).buildString());
            synonym.synonymType = cleanFieldValue(fields.get(1).buildString());
            return Optional.of(synonym);
        } else {
            LOGGER.warn("Could not parse flattened synonym: {}", s);
        }
        return Optional.empty();
    }
}
