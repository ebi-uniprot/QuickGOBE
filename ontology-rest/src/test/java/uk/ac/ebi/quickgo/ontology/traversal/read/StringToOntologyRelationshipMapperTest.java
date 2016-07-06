package uk.ac.ebi.quickgo.ontology.traversal.read;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.COLUMN_CHILD;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.COLUMN_PARENT;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.COLUMN_RELATIONSHIP;
import static uk.ac.ebi.quickgo.ontology.traversal.read.Columns.numColumns;

/**
 * Created 20/05/16
 * @author Edd
 */
public class StringToOntologyRelationshipMapperTest {
    private StringToOntologyRelationshipMapper mapper;

    @Before
    public void setUp() {
        this.mapper = new StringToOntologyRelationshipMapper();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldSetThrowsException() throws Exception {
        mapper.mapFieldSet(null);
    }

    @Test(expected = IncorrectTokenCountException.class)
    public void fieldSetWithInsufficientValuesThrowsException() throws Exception {
        String[] tokens = new String[numColumns() - 1];
        FieldSet fieldSet = new DefaultFieldSet(tokens);

        mapper.mapFieldSet(fieldSet);
    }

    @Test
    public void convertFieldSetWithNullValuesIntoOntologyRelationship() throws Exception {
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
    public void convertFieldSetWithValidValuesIntoOntologyRelationship() throws Exception {
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
    public void convertFieldSetWithTrimmableValuesIntoOntologyRelationship() throws Exception {
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