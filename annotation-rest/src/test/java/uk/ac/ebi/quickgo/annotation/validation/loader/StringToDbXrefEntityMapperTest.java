package uk.ac.ebi.quickgo.annotation.validation.loader;

import uk.ac.ebi.quickgo.annotation.validation.model.DBXRefEntity;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Tony Wardell
 * Date: 09/11/2016
 * Time: 16:17
 * Created with IntelliJ IDEA.
 */
class StringToDbXrefEntityMapperTest {

    private static final String[] VALID_ROW = {"AGI_LocusCode","SO:0000704","gene",
            "A[Tt][MmCc0-5][Gg][0-9]{5}(\\).[0-9]{1})?",
            "http://arabidopsis.org/servlets/TairObject?type=locus&name=[example_id]"};

    private final Pattern patternInstance = Pattern.compile(VALID_ROW[3]);

    @Test
    void mappingIsSuccessful() throws BindException {
        FieldSet validFieldSet = new DefaultFieldSet(VALID_ROW);
        StringToDbXrefEntityMapper mapper = new StringToDbXrefEntityMapper();
        DBXRefEntity instance = mapper.mapFieldSet(validFieldSet);
        assertThat(instance.idValidationPattern.toString(), is(patternInstance.toString()));
        assertThat(instance.database, is(VALID_ROW[0]));
        assertThat(instance.databaseURL, is(VALID_ROW[4]));
        assertThat(instance.entityType, is(VALID_ROW[1]));
        assertThat(instance.entityTypeName, is(VALID_ROW[2]));
    }

    @Test
    void mappingIsSuccessfulWhenDatabaseIsEmpty() throws BindException {
        String[] empty1 = {"", VALID_ROW[1], VALID_ROW[2], VALID_ROW[3], VALID_ROW[4]};
        FieldSet validFieldSet = new DefaultFieldSet(empty1);
        StringToDbXrefEntityMapper mapper = new StringToDbXrefEntityMapper();
        DBXRefEntity instance = mapper.mapFieldSet(validFieldSet);
        assertThat(instance.idValidationPattern.toString(), is(patternInstance.toString()));
        assertThat(instance.database, is(""));
        assertThat(instance.databaseURL, is(VALID_ROW[4]));
        assertThat(instance.entityType, is(VALID_ROW[1]));
        assertThat(instance.entityTypeName, is(VALID_ROW[2]));
    }

    @Test
    void mappingIsSuccessfulWhenEntityTypeNameIsEmpty() throws BindException {
        String[] empty1 = {VALID_ROW[0], VALID_ROW[1], "", VALID_ROW[3], VALID_ROW[4]};
        FieldSet validFieldSet = new DefaultFieldSet(empty1);
        StringToDbXrefEntityMapper mapper = new StringToDbXrefEntityMapper();
        DBXRefEntity instance = mapper.mapFieldSet(validFieldSet);
        assertThat(instance.idValidationPattern.toString(), is(patternInstance.toString()));
        assertThat(instance.database, is(VALID_ROW[0]));
        assertThat(instance.databaseURL, is(VALID_ROW[4]));
        assertThat(instance.entityType, is(VALID_ROW[1]));
        assertThat(instance.entityTypeName,is(""));
    }

    @Test
    void mappingIsSuccessfulWhenEntityTypeIsEmpty() throws BindException {
        String[] empty1 = {VALID_ROW[0], "", VALID_ROW[2], VALID_ROW[3], VALID_ROW[4]};
        FieldSet validFieldSet = new DefaultFieldSet(empty1);
        StringToDbXrefEntityMapper mapper = new StringToDbXrefEntityMapper();
        DBXRefEntity instance = mapper.mapFieldSet(validFieldSet);
        assertThat(instance.idValidationPattern.toString(), is(patternInstance.toString()));
        assertThat(instance.database, is(VALID_ROW[0]));
        assertThat(instance.databaseURL, is(VALID_ROW[4]));
        assertThat(instance.entityType, is(""));
        assertThat(instance.entityTypeName,is(VALID_ROW[2]));
    }

    @Test
    void mappingIsSuccessfulWhenDatabaseUrlIsEmpty() throws BindException {
        String[] empty1 = {VALID_ROW[0], VALID_ROW[1], VALID_ROW[2], VALID_ROW[3], ""};
        FieldSet validFieldSet = new DefaultFieldSet(empty1);
        StringToDbXrefEntityMapper mapper = new StringToDbXrefEntityMapper();
        DBXRefEntity instance = mapper.mapFieldSet(validFieldSet);
        assertThat(instance.idValidationPattern.toString(), is(patternInstance.toString()));
        assertThat(instance.database, is(VALID_ROW[0]));
        assertThat(instance.databaseURL, is(""));
        assertThat(instance.entityType, is(VALID_ROW[1]));
        assertThat(instance.entityTypeName,is(VALID_ROW[2]));
    }
}
