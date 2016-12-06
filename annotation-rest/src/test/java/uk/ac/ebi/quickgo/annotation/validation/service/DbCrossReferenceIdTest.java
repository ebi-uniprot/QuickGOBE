package uk.ac.ebi.quickgo.annotation.validation.service;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.annotation.validation.service.DbCrossReferenceId.*;

/**
 * @author Tony Wardell
 * Date: 21/11/2016
 * Time: 14:36
 * Created with IntelliJ IDEA.
 */
public class DbCrossReferenceIdTest {

    private static final String DB = "UniProt";
    private static final String ID = "12345";
    private static final String DELIMITER = ":";
    private static final String ID_WITH_DB = DB + DELIMITER + ID;

    @Test
    public void retrieveDbSectionSuccessfully(){
        assertThat(db(ID_WITH_DB), is(DB));
    }

    @Test
    public void retrieveIdSectionSuccessfully(){
        assertThat(id(ID_WITH_DB), is(ID));
    }

    @Test
    public void retrieveIdSectionSuccessfullyWithUpperCaseLettersInTheId(){
        assertThat(id("UniProt:AA"), is("AA"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfValueDoesNotContainDelimiterDb(){
        db("ABC");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfValueDoesNotContainDelimiterForId(){
        id("ABC");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfValueIsNullForDb(){
        db(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfValueIsNullForId(){
        id(null);
    }

    @Test
    public void retrieveDbSectionFromJustTheDelimiterGivesEmptyString(){
        assertThat(db(DELIMITER), is(""));
    }

    @Test
    public void retrieveIdSectionFromJustTheDelimiterGivesEmptyString(){
        assertThat(id(DELIMITER), is(""));
    }

    @Test
    public void retrieveDbSectionWhereDoesNotExistGivesEmptyString(){
        assertThat(db(DELIMITER+ID), is(""));
    }

    @Test
    public void retrieveIdSectionWhereDoesNotExistGivesEmptyString(){
        assertThat(id(DB+DELIMITER), is(""));
    }

    @Test
    public void retrieveDbSectionWhereDbSectionContainsSpaces(){
        assertThat(db("   " + DB + "   " + DELIMITER), is(DB));
    }

    @Test
    public void retrieveIdSectionWhereIdSectionContainsSpaces(){
        assertThat(id(DELIMITER + "    " + ID + "    "), is(ID));
    }
}
