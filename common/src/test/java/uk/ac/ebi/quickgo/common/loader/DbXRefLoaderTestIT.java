package uk.ac.ebi.quickgo.common.loader;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.common.validator.DbXRefEntity;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 09:27
 *         Created with IntelliJ IDEA.
 */
class DbXRefLoaderTestIT {

	private static final String NOWHERE_CANTFIND = "OVER/RAINBOW";
	private static final String FIND_IT_HERE = "src/test/resources/DB_XREFS_ENTITIES.dat.gz";
	private static final boolean CASE_SENSITIVE_MATCHING  = false;

	@Test
	void loadFileUnsuccessfully(){

		DbXRefLoader dbXRefLoader = new DbXRefLoader(NOWHERE_CANTFIND, CASE_SENSITIVE_MATCHING );
		List<DbXRefEntity> list = dbXRefLoader.load();
		assertThat(list, hasSize(0));
	}

	@Test
	void loadFileSuccessfully(){
		DbXRefLoader dbXRefLoader = new DbXRefLoader(FIND_IT_HERE, CASE_SENSITIVE_MATCHING );
		List<DbXRefEntity> list = dbXRefLoader.load();
		assertThat(list, hasSize(142));
		assertThat(list.get(0).toString(), is("GeneProductXrefEntity{database='AGI_LocusCode', " +
				"entityType='SO:0000704', entityTypeName='gene', idValidationPattern=A[Tt][MmCc0-5][Gg][0-9]{5}(\\\\" +
				".[0-9]{1})?, databaseURL='http://arabidopsis.org/servlets/TairObject?type=locus&name=[example_id]'}"));
	}
}
