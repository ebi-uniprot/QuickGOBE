package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import uk.ac.ebi.quickgo.common.converter.FlatField;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the conversion of a {@link String} representing a data structure containing a term identifier and its
 * relation to a the term it is associated with, to a corresponding
 * {@link uk.ac.ebi.quickgo.ontology.model.OBOTerm.Relation} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>goId|relationType</li>
 * </ul>
 * Within the {@link OBOTerm} class there exist several attributes that contain ontology identifiers and relation
 * types tuples. Here are some of them:
 * <ul>
 *     <li>{@link OBOTerm#children}</li>
 *     <li>{@link OBOTerm#replaces}</li>
 *     <li>{@link OBOTerm#replacements}</li>
 * </ul>
 * <p>
 * @author Ricardo Antunes
 */
class RelationConverter implements FieldConverter<OBOTerm.Relation> {
    private static final Logger logger = LoggerFactory.getLogger(RelationConverter.class);

    private static final int FIELD_COUNT = 2;

    @Override public Optional<OBOTerm.Relation> apply(String replaceString) {
        List<FlatField> fields = FlatFieldBuilder.newFlatField().parse(replaceString).getFields();

        if (fields.size() == FIELD_COUNT) {
            OBOTerm.Relation relation = new OBOTerm.Relation();
            relation.id = cleanFieldValue(fields.get(0).buildString());
            relation.type = cleanFieldValue(fields.get(1).buildString());

            return Optional.of(relation);
        } else {
            logger.warn("Could not parse flattened relation: {}", replaceString);
        }

        return Optional.empty();
    }
}