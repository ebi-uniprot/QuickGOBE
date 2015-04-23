package uk.ac.ebi.quickgo.web.util.query;

import org.junit.Test;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;

/**
 * @Author Tony Wardell
 * Date: 25/03/2015
 * Time: 11:57
 * Created with IntelliJ IDEA.
 */
public class QueryToTest {

//	@Test
//	public void testGoId() {
//
//		QueryTo queryTo = new QueryTo();
//
//		String query = "goId:GO:0033014";								//Solr Query: goID:(*0033014) AND *:*
//		//String query = "ancestorsI:GO:0033014, GO:0090305";
//		//String query = "xx:GO:0033014";
//		AnnotationParameters annotationParameters = queryTo.queryToAnnotationParameters(query, false);
//		System.out.println(annotationParameters);
//
//		// Create query from filter values
//		String solrQuery = annotationParameters.toSolrQuery();
//		System.out.println("Solr Query testGoId: " + solrQuery);
//	}


	/**
	 * {"goID":"GO:0033014","dbObjectID":""}   -- goId in quick filters
	 *
	 * //is_a, part_of
	 * {"dbObjectID":"","taxonomyId":"","subSet":"","goID":"GO:0033014","slim":"false","ancestorsIPO":"ancestorsIPO","ecoName":"","ecoID":"","ecoAncestorsI":""}
	 *
	 * //is_a, part_of, regulates
	 * {"dbObjectID":"","taxonomyId":"","subSet":"","goID":"GO:0033014","slim":"false","ancestorsIPOR":"ancestorsIPOR","ecoName":"","ecoID":"","ecoAncestorsI":""}
	 *
	 * Advanced filter also equals true;
	 */

	@Test
	public void testAncestorsIPO() {

		QueryTo queryTo = new QueryTo();

		//Solr Query testAncestors: *:*
		//String query = "goId:GO:0033014, GO:0090305, ancestorsI";

		//Solr Query testAncestors: ancestorsIPO:(*0033014) AND *:*
		//String query = "{\"goID\":\"GO:0033014\",\"slim\":\"false\",\"ancestorsIPO\":\"ancestorsIPO\"}";
		//Carlo's version query = "{"goID":"GO:0033014","ancestorsIPO":"ancestorsIPO"}";
		//                         {"goId":"GO:0033014","ancestorsIPO":"ancestorsIPO",}

		//Solr Query testAncestors: goID:(ancestorsIPO\:ancestorsIPO OR *0033014) AND *:*
		//String query = "{goId:GO:0033014,ancestorsIPO:ancestorsIPO,}";

		String query = "\"goID\":\"GO:0033014\",\"ancestorsIPO\":\"ancestorsIPO\",";
		System.out.println("The query String" + query);

		AnnotationParameters annotationParameters = queryTo.queryToAnnotationParameters(query, false);
		System.out.println(annotationParameters);

		// Create query from filter values
		String solrQuery = annotationParameters.toSolrQuery();
		System.out.println("Solr Query testAncestors: " + solrQuery);

		AppliedFilterSet appliedFilterSet = queryTo.queryToAppliedFilterSet(query, false);
		System.out.println("Applied Filter Set testAncestorsI: " + appliedFilterSet);
	}


	/**
	 * Using a slim
	 * {"dbObjectID":"","taxonomyId":"","subSet":"","goID":"GO:0033014","slim":"true","ancestorsIPO":"ancestorsIPO","ecoName":"","ecoID":"","ecoAncestorsI":""}
	 */


	/**
	 * Quey To was creating the following output
	 * AnnotationParameters{parameters={GO=[0003870], ancestorsIPO=[GO:0003824]}}
	 * Solr Query: ancestorsIPO:(*0003824) AND *:*
	 * Applied Filter Set: AppliedFilterSet{parameters={GO=[0003870], ancestorsIPO=[GO:0003824]}}
	 * //todo the above is obviously borked
	 *
	 * After changing QueryTo the following is much better
	 *
	 * The query passed to the Webservice controller is "goID":"GO:0003824","goID":"GO:0003870",
	 * Annotation Parameters: AnnotationParameters{parameters={ancestorsIPO=[GO:0003824, GO:0003870]}}
	 * Applied Filter Set: AppliedFilterSet{parameters={ancestorsIPO=[GO:0003824, GO:0003870]}}
	 * Solr Query: ancestorsIPO:(*0003824 OR *0003870) AND *:*
	 *
	 */
	@Test
	public void testSimpleGoTermList(){

		QueryTo queryTo = new QueryTo();

		String query = "\"goID\":\"GO:0003824\",\"GO:0003870\",";

		AnnotationParameters annotationParameters = queryTo.queryToAnnotationParameters(query, false);
		System.out.println(annotationParameters);

		// Create query from filter values
		String solrQuery = annotationParameters.toSolrQuery();
		System.out.println("Solr Query: " + solrQuery);

		AppliedFilterSet appliedFilterSet = queryTo.queryToAppliedFilterSet(query, false);
		System.out.println("Applied Filter Set: " + appliedFilterSet);
	}
}
