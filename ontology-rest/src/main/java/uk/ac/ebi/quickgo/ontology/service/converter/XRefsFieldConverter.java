package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import uk.ac.ebi.quickgo.common.converter.FlatField;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;

/**
 * Defines the conversion of a {@link String} representing information about a cross-reference,
 * to a corresponding {@link OBOTerm.XRef} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>code|id|name</li>
 * </ul>
 * <p>
 * Created 01/12/15
 * @author Edd
 */
class XRefsFieldConverter implements FieldConverter<OBOTerm.XRef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XRefsFieldConverter.class);
    private static final int FIELD_COUNT = 3;

    @Override public Optional<OBOTerm.XRef> apply(String fieldsStr) {

        List<FlatField> fields = FlatFieldBuilder.parse(fieldsStr).getFields();
        if (fields.size() == FIELD_COUNT) {
            OBOTerm.XRef xref = new OBOTerm.XRef();
            xref.dbCode = cleanFieldValue(fields.get(0).buildString());
            xref.dbId = cleanFieldValue(fields.get(1).buildString());
            xref.name = cleanFieldValue(fields.get(2).buildString());
            return Optional.of(xref);
        } else {
            LOGGER.warn("Could not parse flattened xref: {}", fieldsStr);
        }
        return Optional.empty();
    }
}