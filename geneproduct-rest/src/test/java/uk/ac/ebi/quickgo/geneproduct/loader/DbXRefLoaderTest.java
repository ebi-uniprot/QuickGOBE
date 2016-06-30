package uk.ac.ebi.quickgo.geneproduct.loader;

import uk.ac.ebi.quickgo.geneproduct.model.GeneProductDbXRefIDFormat;

import java.util.List;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 09:27
 *         Created with IntelliJ IDEA.
 */
public class DbXRefLoaderTest {

	private static final String NOWHERE_CANTFIND = "OVER/RAINBOW";
	private static final String FIND_IT_HERE = "src/test/resources/DB_XREFS_ENTITIES.dat.gz";

	@Test
	public void loadFileUnsuccessfully(){
		DbXRefLoader dbXRefLoader = new DbXRefLoader(NOWHERE_CANTFIND);
		List<GeneProductDbXRefIDFormat> list = dbXRefLoader.load();
		assertThat(list, hasSize(0));
	}

	@Test
	public void loadFileSuccessfully(){
		DbXRefLoader dbXRefLoader = new DbXRefLoader(FIND_IT_HERE);
		List<GeneProductDbXRefIDFormat> list = dbXRefLoader.load();
		assertThat(list, hasSize(119));
		assertThat(list.get(0).toString(), is("GeneProductXrefEntity{database='AGI_LocusCode', " +
				"entityType='SO:0000704', entityTypeName='gene', idValidationPattern=A[Tt][MmCc0-5][Gg][0-9]{5}(\\\\" +
				".[0-9]{1})?, databaseURL='http://arabidopsis.org/servlets/TairObject?type=locus&name=[example_id]'}"));
	}
}
