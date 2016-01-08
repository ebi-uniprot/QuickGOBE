package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.ff.flatfield.FlatField;
import uk.ac.ebi.quickgo.service.converter.FieldConverter;
import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatField;

/**
 * Defines the conversion of a {@link String} representing information about a taxonomy constraint, to a
 * corresponding {@link uk.ac.ebi.quickgo.service.model.ontology.OBOTerm.TaxonConstraint} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>ancestorId|ancestorName|relationship|taxId|taxIdType|taxName|pubMedId1&pubMedId2</li>
 * </ul>
 * <p>
 *
 * Created 01/12/15
 * @author Edd
 */
class TaxonConstraintsFieldConverter implements FieldConverter<OBOTerm.TaxonConstraint> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxonConstraintsFieldConverter.class);
    private static final int FIELD_COUNT = 7;

    @Override public Optional<OBOTerm.TaxonConstraint> apply(String fieldsStr) {

        List<FlatField> fields = newFlatField().parse(fieldsStr).getFields();
        if (fields.size() == FIELD_COUNT) {

            OBOTerm.TaxonConstraint taxonConstraint = new OBOTerm.TaxonConstraint();
            taxonConstraint.ancestorId = cleanFieldValue(fields.get(0).buildString());
            taxonConstraint.ancestorName = cleanFieldValue(fields.get(1).buildString());
            taxonConstraint.relationship = cleanFieldValue(fields.get(2).buildString());
            taxonConstraint.taxId = cleanFieldValue(fields.get(3).buildString());
            taxonConstraint.taxIdType = cleanFieldValue(fields.get(4).buildString());
            taxonConstraint.taxName = cleanFieldValue(fields.get(5).buildString());

            taxonConstraint.citations = new ArrayList<>();
            fields.get(6).getFields().stream().forEach(
                    rawLit -> {
                        OBOTerm.Literature literature = new OBOTerm.Literature();
                        literature.id = rawLit.buildString();
                        taxonConstraint.citations.add(literature);
                    }
            );

            return Optional.of(taxonConstraint);
        } else {
            LOGGER.warn("Could not parse flattened taxonConstraint: {}", fieldsStr);
        }
        return Optional.empty();
    }
}
