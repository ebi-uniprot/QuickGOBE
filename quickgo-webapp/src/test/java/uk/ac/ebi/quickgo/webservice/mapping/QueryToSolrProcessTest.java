package uk.ac.ebi.quickgo.webservice.mapping;

import org.junit.Test;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationWSUtil;
import uk.ac.ebi.quickgo.webservice.model.Filter;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @Author Tony Wardell
 * Date: 13/10/2015
 * Time: 14:00
 * Created with IntelliJ IDEA.
 */
public class QueryToSolrProcessTest {

	@Test
	public void testGoId(){

		//Setup
		List<Filter> filters = new ArrayList<>();
		filters.add(new Filter("goID","GO:0003824"));
		filters.add(new Filter("goTermUse","ancestor"));
		filters.add(new Filter("goRelations","IPO"));


		FilterRequestJson filterRequest = new FilterRequestJson();
		filterRequest.setList(filters);

		//We only need annotationsWSUtil to do something if goTermUse = "slim"
		AnnotationWSUtil annotationWSUtil = null;
		QueryToSolrProcess queryToSolrProcess = new QueryToSolrProcess( annotationWSUtil);
		String solrQuery = queryToSolrProcess.toSolrQuery(filterRequest);
		assertEquals("(ancestorsIPO:(GO\\:0003824))", solrQuery);
	}
}
