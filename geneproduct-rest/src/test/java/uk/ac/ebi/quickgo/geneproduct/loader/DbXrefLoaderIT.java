package uk.ac.ebi.quickgo.geneproduct.loader;

import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProductDbXrefIDFormat;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 09:27
 *         Created with IntelliJ IDEA.
 */
public class DbXrefLoaderIT {

	@Test
	//@Ignore		//This test works successfully IF this test is run directly, not via maven/verify
	public void loadFileSuccessfully(){
		DbXrefLoader dbXrefLoader = new DbXrefLoader("../test/resources/");
		List<GeneProductDbXrefIDFormat> list = dbXrefLoader.load();
		assertThat(list, hasSize(119));


	}
}
