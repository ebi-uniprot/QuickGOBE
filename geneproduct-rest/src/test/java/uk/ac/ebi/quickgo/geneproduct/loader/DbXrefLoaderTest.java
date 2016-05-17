package uk.ac.ebi.quickgo.geneproduct.loader;

import org.junit.Test;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProductDbXrefIDFormat;
import java.util.List;
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

	@Test
	public void loadFileUnsuccessfully(){
		DbXRefLoader dbXRefLoader = new DbXRefLoader(NOWHERE_CANTFIND);
		List<GeneProductDbXrefIDFormat> list = dbXRefLoader.load();
		assertThat(list, hasSize(0));
	}
}
