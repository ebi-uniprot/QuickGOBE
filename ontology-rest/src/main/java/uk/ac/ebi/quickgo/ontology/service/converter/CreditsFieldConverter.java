package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import uk.ac.ebi.quickgo.common.converter.FlatField;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;

/**
 * Defines the conversion of a {@link String} representing credit to an external funding agency, to a
 * corresponding {@link uk.ac.ebi.quickgo.ontology.model.OBOTerm.Credit} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>code|url</li>
 * </ul>
 * <p>
 * @author Ricardo Antunes
 */
class CreditsFieldConverter implements FieldConverter<OBOTerm.Credit> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditsFieldConverter.class);
    private static final int FIELD_COUNT = 2;

    @Override public Optional<OBOTerm.Credit> apply(String fieldStr) {
        List<FlatField> fields = newFlatField().parse(fieldStr).getFields();

        Optional<OBOTerm.Credit> creditOpt;

        if (fields.size() == FIELD_COUNT) {
            OBOTerm.Credit credit = new OBOTerm.Credit();
            credit.code = cleanFieldValue(fields.get(0).buildString());
            credit.url = cleanFieldValue(fields.get(1).buildString());

            creditOpt = Optional.of(credit);
        } else {
            LOGGER.warn("Could not parse flattened credit: {}", fieldStr);
            creditOpt = Optional.empty();
        }

        return creditOpt;
    }
}
