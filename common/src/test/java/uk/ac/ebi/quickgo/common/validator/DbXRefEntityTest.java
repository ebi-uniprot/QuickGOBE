package uk.ac.ebi.quickgo.common.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for DbXRefEntity
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 13:50
 *         Created with IntelliJ IDEA.
 */
class DbXRefEntityTest {
	private static final boolean CASE_SENSITIVE_MATCHING  = false;
	private static final String database = "UniProtKB";
	private static final String entityType = "PR:000000001";
	private static final String entityTypeName = "protein";
	private static final String idValidationPattern = "([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])((-[0-9]+)|-PRO_[0-9]{10}|-VAR_[0-9]{6}){0,1}";
	private static final String dbURL = " http://www.uniprot.org/uniprot/[example_id]";
	private DbXRefEntity dbXrefEntity;

	@BeforeEach
	void setup(){
		dbXrefEntity = new DbXRefEntity(database, entityType, entityTypeName, idValidationPattern, dbURL, CASE_SENSITIVE_MATCHING);
	}

	@Test
	void validIdWhenExactMatch() {
		assertThat(dbXrefEntity.matches("A0A000"), is(true));
	}

	@Test
	void validIdWhenCaseInsensitiveMatch() {
		assertThat(dbXrefEntity.matches("a0A000"), is(true));
	}

	@Test
	void validIdWhenExactMatchIncludingDatabase() {
		assertThat(dbXrefEntity.matches("UniProtKB:A0A000"), is(true));
	}

    @Test
    void invalidIdWhenExactMatchButNonMatchingDatabase() {
        assertThat(dbXrefEntity.matches("UnirotKB:A0A000"), is(false));
    }

	@Test
	void validIdWhenCaseInsensitiveMatchIncludingDatabase() {
		assertThat(dbXrefEntity.matches("uniProtkb:a0a000"), is(true));
	}

	@Test
	void validIdWhenExactMatchIncludingDatabaseWithEntityColons() {
		assertThat(dbXrefEntity.matches("uniProtkb:a0a000-PRO_0123456789"), is(true));
	}

	@Test
	void validIdWhenIDincludesFeature() {
		assertThat(dbXrefEntity.matches("a0a000-PRO_0123456789"), is(true));
	}

	@Test
    void isInvalidId() {
        assertThat(dbXrefEntity.matches("99999"), is(false));
    }

	@Test
	void exceptionThrownIfDatabaseIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new DbXRefEntity(null, entityType, entityTypeName, idValidationPattern, dbURL, CASE_SENSITIVE_MATCHING));
    }

	@Test
	void exceptionThrownIfEntityTypeIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new DbXRefEntity(database, null, entityTypeName, idValidationPattern, dbURL, CASE_SENSITIVE_MATCHING));
    }

	@Test
	void exceptionThrownIfIdValidationPatternIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new DbXRefEntity(database, entityType, entityTypeName, null, dbURL, CASE_SENSITIVE_MATCHING));
    }

	@Test
	void exceptionNotThrownIfEntityTypeNameIsNull(){
		new DbXRefEntity(database, entityType, null, idValidationPattern, dbURL, CASE_SENSITIVE_MATCHING);
	}

	@Test
	void exceptionNotThrownIfDbURLIsNull(){
		new DbXRefEntity(database, entityType, entityTypeName, idValidationPattern, null, CASE_SENSITIVE_MATCHING);
	}

	@Test
	void validationCanBeMadeCaseSensitive(){
		boolean matchingIsCaseSensitive = true;
		dbXrefEntity = new DbXRefEntity(database, entityType, entityTypeName, idValidationPattern, dbURL, matchingIsCaseSensitive);
		assertThat(dbXrefEntity.matches("A0A000"), is(true));
		assertThat(dbXrefEntity.matches("a0A000"), is(false));
        assertThat(dbXrefEntity.matches("UniProtKB:A0A000"), is(true));
        assertThat(dbXrefEntity.matches("uniProtkb:a0a000"), is(false));
    }
}
