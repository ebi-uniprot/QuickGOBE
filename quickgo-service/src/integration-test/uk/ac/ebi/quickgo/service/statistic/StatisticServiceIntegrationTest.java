package uk.ac.ebi.quickgo.service.statistic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;
import uk.ac.ebi.quickgo.service.statistic.type.StatsTerm;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;

/**
 * Statistics integration tests
 * 
 * @author cbonill
 * 
 */
public class StatisticServiceIntegrationTest {

	static ApplicationContext appContext;
	static StatisticService statisticService;
	static AnnotationService annotationService;
	static MiscellaneousService miscellaneousService;
	static FileWriter fw;
	public static void main(String[] args) {

		File statsFile = new File("statistics.txt");		
		try {
			fw = new FileWriter(statsFile.getAbsoluteFile());

			appContext = new ClassPathXmlApplicationContext("service-beans.xml", "common-beans.xml", "query-beans.xml");
			statisticService = (StatisticService) appContext.getBean("statisticService");
			annotationService = (AnnotationService) appContext.getBean("annotationService");		
			miscellaneousService = (MiscellaneousService) appContext.getBean("miscellaneousService");
			
			// BY ANNOTATION
			
			fw.write("*****************\n");
			fw.write("GO ID STATISTICS BY ANNOTATION (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByAnnotationStats("*:*", AnnotationField.GOID.getValue());
			System.out.println("GO ID done " + System.currentTimeMillis());
			
			fw.write("*****************\n");
			fw.write("ASPECT STATISTICS BY ANNOTATION (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByAnnotationStats("*:*", AnnotationField.GOASPECT.getValue());
			System.out.println("ASPECT done "+ System.currentTimeMillis());
			
			fw.write("*****************\n");
			fw.write("EVIDENCE STATISTICS BY ANNOTATION (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByAnnotationStats("*:*", AnnotationField.GOEVIDENCE.getValue());
			System.out.println("EVIDENCE done "+ System.currentTimeMillis());
			
			fw.write("*****************\n");
			fw.write("REFERENCES STATISTICS BY ANNOTATION (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByAnnotationStats("*:*", AnnotationField.REFERENCE.getValue());
			System.out.println("REFE// TODO Auto-generated catch blockRENCES done " + System.currentTimeMillis());
			
			fw.write("*****************\n");
			fw.write("TAXONOMY STATISTICS BY ANNOTATION (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByAnnotationStats("*:*", AnnotationField.TAXONOMYID.getValue());
			System.out.println("TAXONOMY done " + System.currentTimeMillis());
			
			fw.write("*****************\n");
			fw.write("ASSIGNED BY STATISTICS BY ANNOTATION (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByAnnotationStats("*:*",  AnnotationField.ASSIGNEDBY.getValue());
			System.out.println("ASSIGEND BY done " + System.currentTimeMillis());

			fw.write("*****************\n");
			fw.write("GO ID BY STATISTICS  BY ANNOTATION (FLYBASE ANG GOC ANNOTATIONS)\n");
			fw.write("*****************\n");
			generateByAnnotationStats( AnnotationField.ASSIGNEDBY.getValue() + ":(FlyBase OR GOC)", "goID");
			
			fw.write("*****************\n");
			fw.write("TAXONOMY ID  BY ANNOTATION (9606)\n");
			fw.write("*****************\n");
			generateByAnnotationStats(AnnotationField.TAXONOMYID.getValue() + ":9606", "taxonomyId");			
			
			
			// BY PROTEIN
			
			fw.write("*****************\n");
			fw.write("GO ID STATISTICS  BY PROTEIN (FLYBASE ANG GOC ANNOTATIONS)\n");
			fw.write("*****************\n");
			generateByProteinStats(AnnotationField.ASSIGNEDBY.getValue() + ":(FlyBase OR GOC)", AnnotationField.GOID.getValue());
			
			
			long start = System.currentTimeMillis();			
		
			fw.write("*****************\n");
			fw.write("TAXONOMY STATISTICS BY PROTEIN (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByProteinStats("*:*", AnnotationField.TAXONOMYID.getValue());
			System.out.println("TAXONOMY done " + System.currentTimeMillis());			
			
			fw.write("*****************\n");
			fw.write("ASPECT STATISTICS BY PROTEIN (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByProteinStats("*:*", AnnotationField.GOASPECT.getValue());
			System.out.println("ASPECT done "+ System.currentTimeMillis());
			
			fw.write("*****************\n");
			fw.write("EVIDENCE STATISTICS BY PROTEIN (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByProteinStats("*:*", AnnotationField.GOEVIDENCE.getValue());
			System.out.println("EVIDENCE done "+ System.currentTimeMillis());
			
			fw.write("*****************\n");
			fw.write("REFERENCES STATISTICS BY PROTEIN (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByProteinStats("*:*", AnnotationField.REFERENCE.getValue());
			System.out.println("REFERENCES done " + System.currentTimeMillis());		
			
			fw.write("*****************\n");
			fw.write("ASSIGNED BY STATISTICS BY PROTEIN (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByProteinStats("*:*",  AnnotationField.ASSIGNEDBY.getValue());
			System.out.println("ASSIGNED BY done " + System.currentTimeMillis());
			
			fw.write("*****************\n");
			fw.write("GO ID STATISTICS BY PROTEIN (ALL ANNOTATIONS)\n");
			fw.write("*****************\n");			
			generateByProteinStats("*:*", AnnotationField.GOID.getValue());
			System.out.println("GO ID done " + System.currentTimeMillis());
			
			long end = System.currentTimeMillis();
			System.out.println("Total time proteins statistics: " + (end - start));
			
			//********************
			// CO-OCCURRENCE STATS
			//********************
			
			// ALL			
			getAllCoOccurrenceStats("GO:0005829");
			getAllCoOccurrenceStats("GO:0016020");
			// NON-IEA
			getNonIEACoOccurrenceStats("GO:0005829");
			
			
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	private static void getAllCoOccurrenceStats(String term) throws IOException {
		fw.write("**************\n");
		fw.write("TERM: " + term + "\n");
		fw.write("**************\n");
		TreeSet<COOccurrenceStatsTerm> coOccurrenceStatsTerms = (TreeSet)miscellaneousService.allCOOccurrenceStatistics(term);
		for(COOccurrenceStatsTerm coOccurrenceStatsTerm : coOccurrenceStatsTerms){			
			fw.write("COMPARED TERM: "
					+ coOccurrenceStatsTerm.getComparedTerm() + " TOGETHER: "
					+ coOccurrenceStatsTerm.getTogether() + " COMPARED: "
					+ coOccurrenceStatsTerm.getCompared() + " PR: "
					+ coOccurrenceStatsTerm.getProbabilityRatio() + " S%:" + coOccurrenceStatsTerm.getProbabilitySimilarityRatio() + "\n");
		}
		
	}

	private static void getNonIEACoOccurrenceStats(String term) throws IOException {
		fw.write("**************\n");
		fw.write("TERM: " + term + "\n");
		fw.write("**************\n");
		TreeSet<COOccurrenceStatsTerm> coOccurrenceStatsTerms = (TreeSet)miscellaneousService.nonIEACOOccurrenceStatistics(term);
		for(COOccurrenceStatsTerm coOccurrenceStatsTerm : coOccurrenceStatsTerms){			
			fw.write("COMPARED TERM: "
					+ coOccurrenceStatsTerm.getComparedTerm() + " TOGETHER: "
					+ coOccurrenceStatsTerm.getTogether() + " COMPARED: "
					+ coOccurrenceStatsTerm.getCompared() + " PR: "
					+ coOccurrenceStatsTerm.getProbabilityRatio() + " S%:" + coOccurrenceStatsTerm.getProbabilitySimilarityRatio() + "\n");
		}
		
	}

	private static void generateByAnnotationStats(String query, String field) {
		Set<StatsTerm> statsTerms = statisticService.statisticsByAnnotation(query, field);
		for (StatsTerm statsTerm : statsTerms) {
			try {
				fw.write(statsTerm.getCode() + " " + statsTerm.getName() + " "	+ statsTerm.getPercentage() + " " + statsTerm.getCount() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void generateByProteinStats(String query, String field) {
		Set<StatsTerm> statsTerms = statisticService.statisticsByProtein(query, field);
		for (StatsTerm statsTerm : statsTerms) {
			try {
				fw.write(statsTerm.getCode() + " " + statsTerm.getName() + " "	+ statsTerm.getPercentage() + " " + statsTerm.getCount() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}