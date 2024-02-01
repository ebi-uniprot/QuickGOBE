package uk.ac.ebi.quickgo.ontology.traversal.read;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.COLUMN_CHILD;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.COLUMN_PARENT;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.COLUMN_RELATIONSHIP;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.numColumns;

/**
 * Created 20/05/16
 * @author Edd
 */
class StringToOntologyRelationshipMapperTest {
    private StringToOntologyRelationshipMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = new StringToOntologyRelationshipMapper();
    }

    @Test
    void nullFieldSetThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> mapper.mapFieldSet(null));
    }

    @Test
    void fieldSetWithInsufficientValuesThrowsException() {
        String[] tokens = new String[numColumns() - 1];
        FieldSet fieldSet = new DefaultFieldSet(tokens);
        assertThrows(IncorrectTokenCountException.class, () -> mapper.mapFieldSet(fieldSet));
    }

    @Test
    void convertFieldSetWithNullValuesIntoOntologyRelationship() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[COLUMN_CHILD.getPosition()] = null;
        tokens[COLUMN_PARENT.getPosition()] = null;
        tokens[COLUMN_RELATIONSHIP.getPosition()] = null;

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawOntologyRelationship ontologyRelationship = mapper.mapFieldSet(fieldSet);

        assertThat(ontologyRelationship.child, is(tokens[COLUMN_CHILD.getPosition()]));
        assertThat(ontologyRelationship.parent, is(tokens[COLUMN_PARENT.getPosition()]));
        assertThat(ontologyRelationship.relationship, is(tokens[COLUMN_RELATIONSHIP.getPosition()]));
    }

    @Test
    void convertFieldSetWithValidValuesIntoOntologyRelationship() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[COLUMN_CHILD.getPosition()] = "GO:1";
        tokens[COLUMN_PARENT.getPosition()] = "GO:2";
        tokens[COLUMN_RELATIONSHIP.getPosition()] = "I";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawOntologyRelationship ontologyRelationship = mapper.mapFieldSet(fieldSet);

        assertThat(ontologyRelationship.child, is(tokens[COLUMN_CHILD.getPosition()]));
        assertThat(ontologyRelationship.parent, is(tokens[COLUMN_PARENT.getPosition()]));
        assertThat(ontologyRelationship.relationship, is(tokens[COLUMN_RELATIONSHIP.getPosition()]));
    }

    @Test
    void convertFieldSetWithTrimmableValuesIntoOntologyRelationship() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[COLUMN_CHILD.getPosition()] = " GO:1";
        tokens[COLUMN_PARENT.getPosition()] = "\tGO:2 ";
        tokens[COLUMN_RELATIONSHIP.getPosition()] = "I  ";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawOntologyRelationship ontologyRelationship = mapper.mapFieldSet(fieldSet);

        assertThat(ontologyRelationship.child, is(tokens[COLUMN_CHILD.getPosition()].trim()));
        assertThat(ontologyRelationship.parent, is(tokens[COLUMN_PARENT.getPosition()].trim()));
        assertThat(ontologyRelationship.relationship, is(tokens[COLUMN_RELATIONSHIP.getPosition()].trim()));
    }
}