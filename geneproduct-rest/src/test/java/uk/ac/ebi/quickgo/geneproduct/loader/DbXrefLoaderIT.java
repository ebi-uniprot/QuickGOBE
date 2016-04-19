package uk.ac.ebi.quickgo.geneproduct.loader;

import org.junit.Test;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProductXrefEntity;

import java.nio.file.Files;
import java.nio.file.Path;
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
	public void loadFileSuccessfully(){
		//System.out.println(System.getProperty("user.dir"));

		DbXrefLoader dbXrefLoader = new DbXrefLoader("../test/resources/");
		Map<GeneProductXrefEntity.Key, List<GeneProductXrefEntity>> xrefMap = dbXrefLoader.load();
		assertThat(xrefMap.values(), hasSize(120));


	}
}
