package uk.ac.ebi.quickgo.common.validator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 13:50
 *         Created with IntelliJ IDEA.
 */
public class GeneProductXrefEntityTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	private String database = "UniProtKB";
	private String entityType = "PR:000000001";
	private String entityTypeName = "protein";
	private String idValidationPattern = "([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])((-[0-9]+)|:PRO_[0-9]{10}|:VAR_[0-9]{6}){0,1}";
	private String dbURL = " http://www.uniprot.org/uniprot/[example_id]";

	GeneProductDbXRefIDFormat geneProductXrefEntity;

	@Before
	public void setup(){
		geneProductXrefEntity = new GeneProductDbXRefIDFormat(database, entityType, entityTypeName,
				idValidationPattern, dbURL);
	}

	@Test
	public void validId(){
		assertThat(geneProductXrefEntity.matches("A0A000"), is(true));
	}

	@Test
	public void inValidId(){
		assertThat(geneProductXrefEntity.matches("99999"), is(false));
	}

	@Test
	public void exceptionThrownIfDatabaseIsNull(){
		thrown.expect(IllegalArgumentException.class);
		new GeneProductDbXRefIDFormat(null, entityType, entityTypeName, idValidationPattern, dbURL);
	}

	@Test
	public void exceptionThrownIfEntityTypeIsNull(){
		thrown.expect(IllegalArgumentException.class);
		new GeneProductDbXRefIDFormat(database, null, entityTypeName, idValidationPattern, dbURL);
	}

	@Test
	public void exceptionThrownIfIdValidationPatternIsNull(){
		thrown.expect(IllegalArgumentException.class);
		new GeneProductDbXRefIDFormat(database, entityType, entityTypeName, null, dbURL);
	}

	@Test
	public void exceptionNotThrownIfEntityTypeNameIsNull(){
		new GeneProductDbXRefIDFormat(database, entityType, null, idValidationPattern, dbURL);
	}

	@Test
	public void exceptionNotThrownIfDbURLIsNull(){
		new GeneProductDbXRefIDFormat(database, entityType, entityTypeName, idValidationPattern, null);
	}
}
