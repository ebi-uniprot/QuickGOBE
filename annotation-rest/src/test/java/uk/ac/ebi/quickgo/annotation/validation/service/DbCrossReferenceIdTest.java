package uk.ac.ebi.quickgo.annotation.validation.service;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.annotation.validation.service.DbCrossReferenceId.*;

/**
 * @author Tony Wardell
 * Date: 21/11/2016
 * Time: 14:36
 * Created with IntelliJ IDEA.
 */
class DbCrossReferenceIdTest {

    private static final String DB = "UniProt";
    private static final String ID = "12345";
    private static final String ID_WITH_DB = DB + DELIMITER + ID;

    @Test
    void retrieveDbSectionSuccessfully(){
        assertThat(db(ID_WITH_DB), is(DB));
    }

    @Test
    void retrieveIdSectionSuccessfully(){
        assertThat(id(ID_WITH_DB), is(ID));
    }

    @Test
    void retrieveIdSectionSuccessfullyWithUpperCaseLettersInTheId(){
        assertThat(id("UniProt:AA"), is("AA"));
    }

    @Test
    void returnNullIfValueDoesNotContainDelimiterDb(){
        assertThat(db("ABC"), is(equalTo(null)));
    }

    @Test
    void throwExceptionIfValueDoesNotContainDelimiterForId(){
        assertThat(id("ABC"), is(equalTo(null)));
    }

    @Test
    void throwExceptionIfValueIsNullForDb(){
        assertThrows(IllegalArgumentException.class, () -> db(null));
    }

    @Test
    void throwExceptionIfValueIsNullForId(){
        assertThrows(IllegalArgumentException.class, () -> id(null));
    }

    @Test
    void retrieveDbSectionFromJustTheDelimiterGivesEmptyString(){
        assertThat(db(DELIMITER), is(""));
    }

    @Test
    void retrieveIdSectionFromJustTheDelimiterGivesEmptyString(){
        assertThat(id(DELIMITER), is(""));
    }

    @Test
    void retrieveDbSectionWhereDoesNotExistGivesEmptyString(){
        assertThat(db(DELIMITER+ID), is(""));
    }

    @Test
    void retrieveIdSectionWhereDoesNotExistGivesEmptyString(){
        assertThat(id(DB+DELIMITER), is(""));
    }

    @Test
    void retrieveDbSectionWhereDbSectionContainsSpaces(){
        assertThat(db("   " + DB + "   " + DELIMITER), is(DB));
    }

    @Test
    void retrieveIdSectionWhereIdSectionContainsSpaces(){
        assertThat(id(DELIMITER + "    " + ID + "    "), is(ID));
    }

    @Test
    void detectWhenValueContainsDelimiter(){
        assertThat(isFullId("DB" + DELIMITER + "ID"), is(true));
    }

    @Test
    void detectWhenValueDoesNotContainDelimiter(){
        assertThat(isFullId("DBID"), is(false));
    }
}
