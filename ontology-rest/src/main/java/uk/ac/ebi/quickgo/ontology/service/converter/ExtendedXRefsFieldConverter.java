package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import uk.ac.ebi.quickgo.common.converter.FlatField;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the conversion of a {@link String} representing information about an extended cross-reference,
 * to a corresponding {@link uk.ac.ebi.quickgo.ontology.model.GOTerm.ExtendedXRef} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>code|id|symbol|name</li>
 * </ul>
 *
 * @author Ricardo Antunes
 */
class ExtendedXRefsFieldConverter implements FieldConverter<GOTerm.ExtendedXRef> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedXRefsFieldConverter.class);
    private static final int FIELD_COUNT = 4;

    @Override public Optional<GOTerm.ExtendedXRef> apply(String fieldsStr) {
        List<FlatField> fields = FlatFieldBuilder.parse(fieldsStr).getFields();

        if (fields.size() == FIELD_COUNT) {
            GOTerm.ExtendedXRef xref = new GOTerm.ExtendedXRef();
            xref.dbCode = cleanFieldValue(fields.get(0).buildString());
            xref.dbId = cleanFieldValue(fields.get(1).buildString());
            xref.symbol = cleanFieldValue(fields.get(2).buildString());
            xref.name = cleanFieldValue(fields.get(3).buildString());

            return Optional.of(xref);
        } else {
            LOGGER.warn("Could not parse flattened extended xref: {}", fieldsStr);
        }
        return Optional.empty();
    }
}