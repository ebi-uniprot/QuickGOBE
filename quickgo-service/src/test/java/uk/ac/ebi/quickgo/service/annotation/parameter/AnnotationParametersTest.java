package uk.ac.ebi.quickgo.service.annotation.parameter;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.junit.Test;

import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

/**
 * Tests for {@link AnnotationParameters} class
 * @author cbonill
 *
 */
public class AnnotationParametersTest {

	@Test
	public void emptyParameters(){
		AnnotationParameters annotationParameters = new AnnotationParameters();
		assertTrue(annotationParameters.toSolrQuery().equals("*:*"));
	}
	
	@Test
	public void dbObjectIdParameters(){
		AnnotationParameters annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.DBOBJECTID.name(), Arrays.asList("A00001","A00002"));
		assertTrue(annotationParameters.toSolrQuery().length() == ("dbObjectID:(A00001 OR A00002) AND *:*").length());
	}
	
	@Test
	public void dbObjectIdAndGOIdParameters(){
		AnnotationParameters annotationParameters = new AnnotationParameters();
		annotationParameters.addParameter(AnnotationField.DBOBJECTID.name(), Arrays.asList("A00001","A00002"));
		annotationParameters.addParameter(AnnotationField.GOID.name(), Arrays.asList("GO:000001","GO:000002"));		
		assertTrue(annotationParameters.toSolrQuery().length() == ("goID:(*000001 OR *000002) " + "AND dbObjectID:(A00001 OR A00002) AND *:*").length());
	}
}