package uk.ac.ebi.quickgo.service.annotation;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.EGOAspect;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationWebServiceField;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

/**
 * Integration tests for {@link AnnotationService} 
 * @author cbonill
 *
 */
public class AnnotationServiceIntegrationTest {

	static ApplicationContext appContext;
	static AnnotationService annotationService;
	
	public static void main(String[] args) {

		appContext = new ClassPathXmlApplicationContext("service-beans.xml", "common-beans.xml","query-beans.xml");
		annotationService = (AnnotationService) appContext.getBean("annotationService");
	
		// Retrieve all annotations
		annotationService.retrieveAll();
		
		//***************
		// Filter queries
		//***************
		
		// Gene product A0SDZ9
		AnnotationParameters annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.DBOBJECTID.name(), Arrays.asList("A0SDZ9"));
		List<Annotation> fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with gene product A0SDZ9: " + fileteredAnnotations.size());		
		
		// Taxon Id 9913
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.TAXONOMYID.name(), Arrays.asList("9913"));
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with taxon id 9913: " + fileteredAnnotations.size());		
		
		// Taxon Id 9606 OR 10090
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.TAXONOMYID.name(), Arrays.asList("9606"));
		annotationParameters.addParameter(AnnotationField.TAXONOMYID.name(), Arrays.asList("10090"));
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with taxon id 9606 OR 10090: " + fileteredAnnotations.size());
		
		// Gene product A0SDZ9 and GO_REF
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.DBOBJECTID.name(), Arrays.asList("A0SDZ9"));
		annotationParameters.addParameter(AnnotationField.DBXREF.name(), Arrays.asList("GO_REF*"));
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with gene product A0SDZ9 and GO_REFs: " + fileteredAnnotations.size());
		 		
		// Gene product A0SE07 and IEA evidence
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.DBOBJECTID.name(), Arrays.asList("A0SE07"));
		annotationParameters.addParameter(AnnotationField.GOEVIDENCE.name(), Arrays.asList("IEA"));
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with gene product A0SE07 and IEA: " + fileteredAnnotations.size());
				
		// Target Set KRUK
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.TARGETSET.name(), Arrays.asList("KRUK"));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with target set KRUK: " + fileteredAnnotations.size());
				
		// Qualifier part_of
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.QUALIFIER.name(), Arrays.asList("part_of"));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with qualifier part_of: " + fileteredAnnotations.size());
				
		// GO identifier GO:0005524 
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.GOID.name(), Arrays.asList("GO:0005524"));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with go id GO:0005524: " + fileteredAnnotations.size());
		
		// With InterPro:* 
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.WITH.name(), Arrays.asList("InterPro*"));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with InterPro:* : " + fileteredAnnotations.size());
		
		// Molecular Function aspect 
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.GOASPECT.name(), Arrays.asList(EGOAspect.F.text));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with Molecular Function aspect: : " + fileteredAnnotations.size());
		
		// Molecular Function aspect using Web Service field
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationWebServiceField.ASPECT.name(), Arrays.asList(EGOAspect.F.text));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations with Molecular Function aspect: : " + fileteredAnnotations.size());
		
		// Assigned by AgBase
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.ASSIGNEDBY.name(), Arrays.asList("AgBase"));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations assigned by AgBase: " + fileteredAnnotations.size());
	
		// Assigned by AgBase or InterPro
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.ASSIGNEDBY.name(), Arrays.asList("AgBase","InterPro"));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations assigned by AgBase or InterPro: " + fileteredAnnotations.size());
		
		//******************
		// Advanced queries
		//******************
		
		// Annotations assigned by AgBase and qualifier involved_in and not IMP go evidence
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationWebServiceField.ADVANCEDQUERY.name(), Arrays.asList("assignedBy:AgBase AND (qualifier:involved_in AND NOT goEvidence:IMP)"));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations assigned by AgBase and qualifier involved_in and not IMP go evidence: " + fileteredAnnotations.size());
		
		// Advanced query "goID:GO:0000166 AND goEvidence:IEA AND NOT with:InterPro*"
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationWebServiceField.ADVANCEDQUERY.name(), Arrays.asList("goID:GO:0000166 AND goEvidence:IEA AND NOT with:InterPro*"));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations for goID GO:0000166, GO evidence IEA and not InterPro: " + fileteredAnnotations.size());
		
		// Advanced query "assignedBy:InterPro AND goAspect:Function AND NOT dbObjectID:A0A002"
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationWebServiceField.ADVANCEDQUERY.name(), Arrays.asList("assignedBy:InterPro AND goAspect:Function AND NOT dbObjectID:A0A002"));		
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations assigned by InterPro and Function aspect and not for the protein A0A002: " + fileteredAnnotations.size());
		
		
		//************************************
		// Filter queries AND Advanced queries
		//************************************
		
		// Annotations assigned by AgBase and qualifier involved_in and not IMP go evidence
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationWebServiceField.ADVANCEDQUERY.name(), Arrays.asList("qualifier:involved_in AND NOT goEvidence:IMP"));
		annotationParameters.addParameter(AnnotationField.ASSIGNEDBY.name(), Arrays.asList("AgBase"));
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations assigned by AgBase and qualifier involved_in and not IMP go evidence: " + fileteredAnnotations.size());
		
		// Query "assignedBy:InterPro AND goAspect:Function AND NOT dbObjectID:A0A002"
		annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationWebServiceField.ADVANCEDQUERY.name(), Arrays.asList("NOT dbObjectID:A0A002"));
		annotationParameters.addParameter(AnnotationField.ASSIGNEDBY.name(), Arrays.asList("InterPro"));
		annotationParameters.addParameter(AnnotationField.GOASPECT.name(), Arrays.asList("Function"));				
		fileteredAnnotations = annotationService.retrieveAnnotations(annotationParameters.toSolrQuery(),0,-1);
		System.out.println("Annotations assigned by InterPro and Function aspect and not for the protein A0A002: " + fileteredAnnotations.size());

	}	
}