package uk.ac.ebi.quickgo.model.ontology.converter.field;

import uk.ac.ebi.quickgo.document.FlatField;
import uk.ac.ebi.quickgo.model.FieldConverter;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.document.FlatFieldBuilder.parseFlatField;

/**
 * Created 01/12/15
 * @author Edd
 */
public class XRefsFieldConverter implements FieldConverter<OBOTerm.XRef> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(XRefsFieldConverter.class);

    @Override public Optional<OBOTerm.XRef> apply(String s) {
        // format: code|id|name
        OBOTerm.XRef xref = new OBOTerm.XRef();

        List<FlatField> fields = parseFlatField(s).getFields();
        if (fields.size() == 3) {
            xref.dbCode = nullOrString(fields.get(0).buildString());
            xref.dbId = nullOrString(fields.get(1).buildString());
            xref.name = nullOrString(fields.get(2).buildString());
            return Optional.of(xref);
        } else {
            LOGGER.warn("Could not parse flattened xref: {}", s);
        }
        return Optional.empty();
    }
}
