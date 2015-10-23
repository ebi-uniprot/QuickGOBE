package uk.ac.ebi.quickgo.service.miscellaneous;

import java.util.TreeSet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;

/**
 * {@link MiscellaneousService} integration tests
 * @author cbonill
 *
 */
public class MiscellaneousServiceIntegrationTest {

	static ApplicationContext appContext;
	static MiscellaneousService miscellaneousService;
	
	public static void main(String[] args) {

		appContext = new ClassPathXmlApplicationContext("service-beans.xml", "common-beans.xml",
				"query-beans.xml");
		miscellaneousService = (MiscellaneousService) appContext.getBean("miscellaneousService");
		
		
		calculateNonIEAStats("GO:0016020");
		calculateNonIEAStats("GO:0030529");
		calculateNonIEAStats("GO:0005829");
		
		calculateAllStats("GO:0016020");
		calculateAllStats("GO:0097381");
		calculateAllStats("GO:0016020");				
	}

	private static void calculateAllStats(String term) {
		TreeSet<COOccurrenceStatsTerm> coOccurrenceStatsTerms = (TreeSet)miscellaneousService.allCOOccurrenceStatistics(term);
		System.out.println("TERM: " + term);
		System.out.println("SIZE: " + coOccurrenceStatsTerms.size());
		for(COOccurrenceStatsTerm coOccurrenceStatsTerm : coOccurrenceStatsTerms){
			System.out.println("COMPARED TERM: "
					+ coOccurrenceStatsTerm.getComparedTerm() + " TOGETHER: "
					+ coOccurrenceStatsTerm.getTogether() + " COMPARED: "
					+ coOccurrenceStatsTerm.getCompared() + " PR: "
					+ coOccurrenceStatsTerm.getProbabilityRatio() + " S%:" + coOccurrenceStatsTerm.getProbabilitySimilarityRatio());

		}		
	}
	
	private static void calculateNonIEAStats(String term) {
		TreeSet<COOccurrenceStatsTerm> coOccurrenceStatsTerms = (TreeSet)miscellaneousService.nonIEACOOccurrenceStatistics(term);
		System.out.println("TERM: " + term);
		System.out.println("SIZE: " + coOccurrenceStatsTerms.size());
		for(COOccurrenceStatsTerm coOccurrenceStatsTerm : coOccurrenceStatsTerms.descendingSet()){
			System.out.println("COMPARED TERM: "
					+ coOccurrenceStatsTerm.getComparedTerm() + " TOGETHER: "
					+ coOccurrenceStatsTerm.getTogether() + " COMPARED: "
					+ coOccurrenceStatsTerm.getCompared() + " PR: "
					+ coOccurrenceStatsTerm.getProbabilityRatio() + " S%:" + coOccurrenceStatsTerm.getProbabilitySimilarityRatio());

		}		
	}
}