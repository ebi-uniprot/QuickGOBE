package uk.ac.ebi.quickgo.geneproduct.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 13:50
 *         Created with IntelliJ IDEA.
 */
public class GeneProductXrefEntityTest {

	private String database = "UniProtKB";
	private String entityType = "PR:000000001";
	private String entityTypeName = "protein";
	private String localIdSyntax = "([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])((-[0-9]+)|:PRO_[0-9]{10}|:VAR_[0-9]{6}){0,1}";
	private String urlSyntax = " http://www.uniprot.org/uniprot/[example_id]";

	GeneProductDbXRefIDFormat geneProductXrefEntity;

	@Before
	public void setup(){
		geneProductXrefEntity = new GeneProductDbXRefIDFormat(database, entityType, entityTypeName,
				localIdSyntax, urlSyntax);
	}

	@Test
	public void validId(){
		assertThat(geneProductXrefEntity.matches("A0A000"), is(true));
	}

	@Test
	public void inValidId(){
		assertThat(geneProductXrefEntity.matches("99999"), is(false));
	}


}
