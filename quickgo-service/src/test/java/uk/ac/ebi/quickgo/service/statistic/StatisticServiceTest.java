package uk.ac.ebi.quickgo.service.statistic;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.statistic.StatsTerm;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.query.model.ontology.enums.TermField;

/**
 * Tests for {@link StatisticService}
 * @author cbonill
 *
 */
public class StatisticServiceTest {

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		    setThreadingPolicy(new Synchroniser());
		}
	};

	@Test
	public void testAnnotationStatsAllAnnotationsAssignedBy(){

		final String query = "*:*";
		final String facetField = AnnotationField.ASSIGNEDBY.getValue();

		// Services
		final StatisticServiceImpl statisticService = new StatisticServiceImpl();
		final AnnotationService annotationService = context.mock(AnnotationService.class);
		StatisticsUtil statisticsUtil = new StatisticsUtil();
		statisticService.annotationService = annotationService;
		statisticService.statisticsUtil = statisticsUtil;

		//Count terms
		Count interProCount = new Count(new FacetField(facetField), "InterPro", 5);
		Count agBaseCount = new Count(new FacetField(facetField), "AgBase", 5);
		final List<Count> counts = new ArrayList<>();
		counts.add(agBaseCount);
		counts.add(interProCount);

		context.checking(new Expectations() {
			{
				allowing(statisticService.annotationService).getFacetFields(query, null, facetField, 80);
				will(returnValue(counts));

				allowing(annotationService).getTotalNumberAnnotations(query);
				will(returnValue(10L));

			}
		});

		Set<StatsTerm> statsTermsSet = statisticService.statisticsByAnnotation(query, facetField);
		assertTrue(statsTermsSet.size() == 2);
		List<StatsTerm> statsTerms = new ArrayList<>(statsTermsSet);
		assertTrue(statsTerms.get(0).getPercentage() == 50);
		assertTrue(statsTerms.get(1).getPercentage() == 50);
		context.assertIsSatisfied();
	}

	@Test
	public void testAnnotationStatsAllAnnotationsEvidence(){

		final String query = "*:*";
		final String facetField = AnnotationField.GOEVIDENCE.getValue();

		// Services
		final StatisticServiceImpl statisticService = new StatisticServiceImpl();
		final AnnotationService annotationService = context.mock(AnnotationService.class);
		StatisticsUtil statisticsUtil = new StatisticsUtil();
		statisticService.annotationService = annotationService;
		statisticService.statisticsUtil = statisticsUtil;

		//Count terms
		Count issCount = new Count(new FacetField(facetField), "ISS", 2);
		Count ieaCount = new Count(new FacetField(facetField), "IEA", 8);
		final List<Count> counts = new ArrayList<>();
		counts.add(issCount);
		counts.add(ieaCount);

		context.checking(new Expectations() {
			{
				allowing(statisticService.annotationService).getFacetFields(query, null, facetField, 80);
				will(returnValue(counts));

				allowing(annotationService).getTotalNumberAnnotations(query);
				will(returnValue(10L));
			}
		});

		Set<StatsTerm> statsTermsSet = statisticService.statisticsByAnnotation(query, facetField);
		assertTrue(statsTermsSet.size() == 2);
		List<StatsTerm> statsTerms = new ArrayList<>(statsTermsSet);
		assertTrue(statsTerms.get(0).getPercentage() == 80);
		assertTrue(statsTerms.get(1).getPercentage() == 20);
		context.assertIsSatisfied();
	}

	@Test
	public void testAnnotationStatsHumanAnnotationsGoID(){

		final String query = AnnotationField.TAXONOMYID + ":" + 9606;
		final String facetField = AnnotationField.GOID.getValue();

		// Services
		final StatisticServiceImpl statisticService = new StatisticServiceImpl();
		TermService goTermService = context.mock(TermService.class);
		final AnnotationService annotationService = context.mock(AnnotationService.class);
		statisticService.annotationService = annotationService;
		StatisticsUtil statisticsUtil = new StatisticsUtil();
		statisticsUtil.goTermService = goTermService;
		statisticService.statisticsUtil = statisticsUtil;


		// Ontology names
		final Map<String, Map<String, String>> ontologyTermsNames = new HashMap<String, Map<String,String>>();
		HashMap<String, String> name1 = new HashMap<>();
		name1.put(TermField.NAME.getValue(), "cytoplasm");
		HashMap<String, String> name2 = new HashMap<>();
		name2.put(TermField.NAME.getValue(), "membrane");

		ontologyTermsNames.put("GO:0000001", name1);
		ontologyTermsNames.put("GO:0000002", name2);

		//Count terms
		Count issCount = new Count(new FacetField(facetField), "GO:0000001", 4);
		Count ieaCount = new Count(new FacetField(facetField), "GO:0000002", 6);
		final List<Count> counts = new ArrayList<>();
		counts.add(issCount);
		counts.add(ieaCount);

		context.checking(new Expectations() {
			{
				allowing(statisticService.annotationService).getFacetFields(query, null, facetField, 80);
				will(returnValue(counts));

				allowing(statisticService.statisticsUtil.goTermService).retrieveNames();
				will(returnValue(ontologyTermsNames));

				allowing(annotationService).getTotalNumberAnnotations(query);
				will(returnValue(10L));
			}
		});

		Set<StatsTerm> statsTermsSet = statisticService.statisticsByAnnotation(query, facetField);
		assertTrue(statsTermsSet.size() == 2);
		List<StatsTerm> statsTerms = new ArrayList<>(statsTermsSet);
		assertTrue(statsTerms.get(0).getPercentage() == 60);
		assertTrue(statsTerms.get(1).getPercentage() == 40);
		assertTrue(statsTerms.get(0).getName().equals("membrane"));
		assertTrue(statsTerms.get(1).getName().equals("cytoplasm"));
		context.assertIsSatisfied();
	}
}
