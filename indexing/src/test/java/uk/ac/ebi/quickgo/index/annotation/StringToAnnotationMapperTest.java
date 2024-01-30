package uk.ac.ebi.quickgo.index.annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.index.annotation.Columns.*;

/**
 * Test the behaviour of the {@link StringToAnnotationMapper} class.
 * Created 21/04/16
 * @author Edd
 */

class StringToAnnotationMapperTest {

    private static final Logger LOGGER = getLogger(StringToAnnotationMapper.class);
    private StringToAnnotationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new StringToAnnotationMapper();
    }

    @Test
    void nullFieldSetThrowsException() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.mapFieldSet(null);
        });
    }

    @Test
    void fieldSetWithInsufficientValuesThrowsException() throws Exception {
        assertThrows(IncorrectTokenCountException.class, () -> {
            String[] tokens = new String[numColumns() - 1];
            FieldSet fieldSet = new DefaultFieldSet(tokens);

            mapper.mapFieldSet(fieldSet);
        });
    }

    @Test
    void convertFieldSetWithNullValuesIntoAnnotation() throws Exception {
        String[] tokens = new String[Columns.numColumns()];

        tokens[COLUMN_DB.getPosition()] = null;
        tokens[COLUMN_DB_OBJECT_ID.getPosition()] = null;
        tokens[COLUMN_QUALIFIER.getPosition()] = null;
        tokens[COLUMN_GO_ID.getPosition()] = null;
        tokens[COLUMN_DB_REFERENCES.getPosition()] = null;
        tokens[COLUMN_EVIDENCE_CODE.getPosition()] = null;
        tokens[COLUMN_WITH.getPosition()] = null;
        tokens[COLUMN_INTERACTING_TAXON_ID.getPosition()] = null;
        tokens[COLUMN_DATE.getPosition()] = null;
        tokens[COLUMN_ASSIGNED_BY.getPosition()] = null;
        tokens[COLUMN_ANNOTATION_EXTENSION.getPosition()] = null;
        tokens[COLUMN_ANNOTATION_PROPERTIES.getPosition()] = null;

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        Annotation annotation = mapper.mapFieldSet(fieldSet);

        checkAnnotationObjectFieldsMatchTokenFields(tokens, annotation);
    }

    @Test
    void convertValidFieldSetIntoAnnotation() throws Exception {
        String[] tokens = new String[Columns.numColumns()];

        tokens[COLUMN_DB.getPosition()] = "IntAct";
        tokens[COLUMN_DB_OBJECT_ID.getPosition()] = "EBI-10043081";
        tokens[COLUMN_QUALIFIER.getPosition()] = "enables";
        tokens[COLUMN_GO_ID.getPosition()] = "GO:0000977";
        tokens[COLUMN_DB_REFERENCES.getPosition()] = "PMID:12871976";
        tokens[COLUMN_EVIDENCE_CODE.getPosition()] = "ECO:0000353";
        tokens[COLUMN_WITH.getPosition()] = "GO:0036376,GO:1990573";
        tokens[COLUMN_INTERACTING_TAXON_ID.getPosition()] = "1234";
        tokens[COLUMN_DATE.getPosition()] = "20150122";
        tokens[COLUMN_ASSIGNED_BY.getPosition()] = "IntAct";
        tokens[COLUMN_ANNOTATION_EXTENSION.getPosition()] = "occurs_in(CL:1000428)";
        tokens[COLUMN_ANNOTATION_PROPERTIES.getPosition()] = "go_evidence=IPI";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        Annotation annotation = mapper.mapFieldSet(fieldSet);

        checkAnnotationObjectFieldsMatchTokenFields(tokens, annotation);
    }

    @Test
    void convertValidButUntrimmedFieldSetIntoAnnotation() throws Exception {
        String[] tokens = new String[Columns.numColumns()];

        tokens[COLUMN_DB.getPosition()] = "   IntAct   ";
        tokens[COLUMN_DB_OBJECT_ID.getPosition()] = "  EBI-10043081";
        tokens[COLUMN_QUALIFIER.getPosition()] = "enables     ";
        tokens[COLUMN_GO_ID.getPosition()] = "GO:0000977    ";
        tokens[COLUMN_DB_REFERENCES.getPosition()] = "PMID:12871976";
        tokens[COLUMN_EVIDENCE_CODE.getPosition()] = "ECO:0000353  ";
        tokens[COLUMN_WITH.getPosition()] = "  GO:0036376,GO:1990573";
        tokens[COLUMN_INTERACTING_TAXON_ID.getPosition()] = "1234";
        tokens[COLUMN_DATE.getPosition()] = "  20150122";
        tokens[COLUMN_ASSIGNED_BY.getPosition()] = "IntAct";
        tokens[COLUMN_ANNOTATION_EXTENSION.getPosition()] = "occurs_in(CL:1000428)   ";
        tokens[COLUMN_ANNOTATION_PROPERTIES.getPosition()] = "go_evidence=IPI    ";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        Annotation annotation = mapper.mapFieldSet(fieldSet);

        checkAnnotationObjectFieldsMatchTokenFields(tokens, annotation);
    }

    private void checkAnnotationObjectFieldsMatchTokenFields(String[] tokens, Annotation annotation) {
        assertThat(annotation.db, is(trim(tokens[COLUMN_DB.getPosition()])));
        assertThat(annotation.dbObjectId, is(trim(tokens[COLUMN_DB_OBJECT_ID.getPosition()])));
        assertThat(annotation.qualifier, is(trim(tokens[COLUMN_QUALIFIER.getPosition()])));
        assertThat(annotation.goId, is(trim(tokens[COLUMN_GO_ID.getPosition()])));
        assertThat(annotation.dbReferences, is(trim(tokens[COLUMN_DB_REFERENCES.getPosition()])));
        assertThat(annotation.evidenceCode, is(trim(tokens[COLUMN_EVIDENCE_CODE.getPosition()])));
        assertThat(annotation.with, is(trim(tokens[COLUMN_WITH.getPosition()])));
        assertThat(annotation.interactingTaxonId, is(trim(tokens[COLUMN_INTERACTING_TAXON_ID.getPosition()])));
        assertThat(annotation.assignedBy, is(trim(tokens[COLUMN_ASSIGNED_BY.getPosition()])));
        assertThat(annotation.annotationExtension, is(trim(tokens[COLUMN_ANNOTATION_EXTENSION.getPosition()])));
        assertThat(annotation.annotationProperties, is(trim(tokens[COLUMN_ANNOTATION_PROPERTIES.getPosition()])));
        assertThat(annotation.date, is(trim(tokens[COLUMN_DATE.getPosition()])));
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}