package uk.ac.ebi.quickgo.ontology.traversal.read;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.validation.BindException;

import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.COLUMN_CHILD;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.COLUMN_PARENT;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.COLUMN_RELATIONSHIP;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.numColumns;

/**
 * Created 18/05/16
 * @author Edd
 */
public class StringToOntologyTupleMapper implements FieldSetMapper<OntologyRelationshipTuple> {
    @Override public OntologyRelationshipTuple mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new IllegalArgumentException("Provided field set is null");
        }

        if (fieldSet.getFieldCount() < numColumns()) {
            throw new IncorrectTokenCountException("Incorrect number of columns, expected: " + numColumns() + "; " +
                    "found: " + fieldSet.getFieldCount(), numColumns(), fieldSet.getFieldCount());
        }

        OntologyRelationshipTuple relationshipTuple = new OntologyRelationshipTuple();

        relationshipTuple.childVertex = trimIfNotNull(fieldSet.readString(COLUMN_CHILD.getPosition()));
        relationshipTuple.parentVertex = trimIfNotNull(fieldSet.readString(COLUMN_PARENT.getPosition()));
        relationshipTuple.edgeLabel = trimIfNotNull(fieldSet.readString(COLUMN_RELATIONSHIP.getPosition()));

        return relationshipTuple;
    }

    private String trimIfNotNull(String value) {
        return value == null ? null : value.trim();
    }
}
