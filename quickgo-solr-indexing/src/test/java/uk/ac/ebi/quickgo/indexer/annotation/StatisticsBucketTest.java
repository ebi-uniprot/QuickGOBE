package uk.ac.ebi.quickgo.indexer.annotation;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

/**
 * @Author Tony Wardell
 * Date: 19/10/2015
 * Time: 15:42
 * Created with IntelliJ IDEA.
 */
public class StatisticsBucketTest {

	@Test
	public void testGoTermGeneProducts(){

		StatisticsBucket statisticsBucket = new StatisticsBucket();

		//Load contents
		final List<GOAnnotation> goAnnotations = this.testAnnotations();
		for(GOAnnotation annotation : goAnnotations) {
			statisticsBucket.addAnnotationToStatistics(annotation);
		}

		//Annotations by Go Id
		StatisticTuple[] topAnnotationsByGoTermHits = statisticsBucket.topAnnotationsPerGOID();

		assertEquals("GO:0005524", topAnnotationsByGoTermHits[0].getstatisticTupleKey()	);
		assertEquals(5, topAnnotationsByGoTermHits[0].getStatisticTupleHits());

		assertEquals("GO:0008152", topAnnotationsByGoTermHits[1].getstatisticTupleKey());
		assertEquals(4, topAnnotationsByGoTermHits[1].getStatisticTupleHits());

		assertEquals("GO:0003824", topAnnotationsByGoTermHits[2].getstatisticTupleKey());
		assertEquals(3, topAnnotationsByGoTermHits[2].getStatisticTupleHits());


		//GeneProducts by Go Id
		StatisticTuple[] topGeneProductsByGoTermHits = statisticsBucket.topGeneProductsPerGOID();

		assertEquals("GO:0008152", topGeneProductsByGoTermHits[0].getstatisticTupleKey());
		assertEquals(4, topGeneProductsByGoTermHits[0].getStatisticTupleHits());

		assertEquals("GO:0005524", topGeneProductsByGoTermHits[1].getstatisticTupleKey());
		assertEquals(3, topGeneProductsByGoTermHits[1].getStatisticTupleHits());

		assertEquals("GO:0003824", topGeneProductsByGoTermHits[2].getstatisticTupleKey());
		assertEquals(2, topGeneProductsByGoTermHits[2].getStatisticTupleHits());


		//Annotations by aspect
		StatisticTuple[] topAspectAnnotationHits = statisticsBucket.topAnnotationsPerAspect();

		assertEquals("C", topAspectAnnotationHits[0].getstatisticTupleKey());
		assertEquals(6, topAspectAnnotationHits[0].getStatisticTupleHits());

		assertEquals("F", topAspectAnnotationHits[1].getstatisticTupleKey());
		assertEquals(4, topAspectAnnotationHits[1].getStatisticTupleHits());

		assertEquals("P", topAspectAnnotationHits[2].getstatisticTupleKey());
		assertEquals(2, topAspectAnnotationHits[2].getStatisticTupleHits());


		//Gene Products by aspect
		StatisticTuple[] topAspectGeneProductHits = statisticsBucket.topGeneProductsPerAspect();

		assertEquals("C", topAspectGeneProductHits[0].getstatisticTupleKey());
		assertEquals(4, topAspectGeneProductHits[0].getStatisticTupleHits());

		assertEquals("F", topAspectGeneProductHits[1].getstatisticTupleKey());
		assertEquals(4, topAspectGeneProductHits[1].getStatisticTupleHits());

		assertEquals("P", topAspectGeneProductHits[2].getstatisticTupleKey());
		assertEquals(2, topAspectGeneProductHits[2].getStatisticTupleHits());


		//Annotations by Evidences
		StatisticTuple[] topEvidenceAnnotationHits = statisticsBucket.topAnnotationsPerEvidence();
		assertEquals("ECO:0000323", topEvidenceAnnotationHits[0].getstatisticTupleKey());
		assertEquals(6,topEvidenceAnnotationHits[0].getStatisticTupleHits());

		assertEquals("ECO:0000501", topEvidenceAnnotationHits[1].getstatisticTupleKey());
		assertEquals(3,topEvidenceAnnotationHits[1].getStatisticTupleHits());

		assertEquals("ECO:0000256", topEvidenceAnnotationHits[2].getstatisticTupleKey());
		assertEquals(2,topEvidenceAnnotationHits[2].getStatisticTupleHits());

		assertEquals("ECO:0000999", topEvidenceAnnotationHits[3].getstatisticTupleKey());
		assertEquals(1,topEvidenceAnnotationHits[3].getStatisticTupleHits());

		//Gene Products by Evidences
		StatisticTuple[] topEvidenceGeneProductHits = statisticsBucket.topGeneProductsPerEvidence();
		assertEquals(80, topEvidenceGeneProductHits.length);

		assertEquals("ECO:0000323", topEvidenceGeneProductHits[0].getstatisticTupleKey());
		assertEquals(4, topEvidenceGeneProductHits[0].getStatisticTupleHits());

		assertEquals("ECO:0000501", topEvidenceGeneProductHits[1].getstatisticTupleKey());
		assertEquals(2, topEvidenceGeneProductHits[1].getStatisticTupleHits());

		assertEquals("ECO:0000256", topEvidenceGeneProductHits[2].getstatisticTupleKey());
		assertEquals(1, topEvidenceGeneProductHits[2].getStatisticTupleHits());

		assertEquals("ECO:0000999", topEvidenceGeneProductHits[3].getstatisticTupleKey());
		assertEquals(1, topEvidenceGeneProductHits[3].getStatisticTupleHits());

		assertEquals(null, topEvidenceGeneProductHits[4]);



		//References ordered by Annotations
		StatisticTuple[] topReferenceAnnotationHits = statisticsBucket.topAnnotationsPerReference();

		assertEquals("GO_REF:0000038", topReferenceAnnotationHits[0].getstatisticTupleKey());
		assertEquals(5, topReferenceAnnotationHits[0].getStatisticTupleHits());

		assertEquals("GO_REF:0000040", topReferenceAnnotationHits[1].getstatisticTupleKey());
		assertEquals(4, topReferenceAnnotationHits[1].getStatisticTupleHits());

		assertEquals("GO_REF:0000039", topReferenceAnnotationHits[2].getstatisticTupleKey());
		assertEquals(3, topReferenceAnnotationHits[2].getStatisticTupleHits());

	}


	private List<GOAnnotation> testAnnotations(){
		List<GOAnnotation> list = new ArrayList<>();


		//AOA000 1
		//AOA003 1
		//AOA002 2
		//AOA001 3
		//AOA007 2
		//A0A009DX00 1
		//A0A009DWC0 2

		//GO:0003824 - 3 annotations, 2 unique annotations
		GOAnnotation goAnnotation1 = new GOAnnotation( );
		goAnnotation1.setGoID("GO:0003824");
		goAnnotation1.setDbObjectID("AOA000");
		goAnnotation1.setGoAspect("C");
		goAnnotation1.setGoEvidence("ECO:0000256");
		goAnnotation1.setReference("GO_REF:0000038");
		goAnnotation1.setAssignedBy("GOC");
		list.add(goAnnotation1);

		GOAnnotation goAnnotation2 = new GOAnnotation( );
		goAnnotation2.setGoID("GO:0003824");
		goAnnotation2.setDbObjectID("AOA000");
		goAnnotation2.setGoAspect("C");
		goAnnotation2.setGoEvidence("ECO:0000256");
		goAnnotation2.setReference("GO_REF:0000039");
		goAnnotation2.setAssignedBy("GOC");
		list.add(goAnnotation2);

		GOAnnotation goAnnotation3 = new GOAnnotation( );
		goAnnotation3.setGoID("GO:0003824");
		goAnnotation3.setDbObjectID("AOA007");
		goAnnotation3.setGoAspect("F");
		goAnnotation3.setGoEvidence("ECO:0000323");
		goAnnotation3.setReference("GO_REF:0000040");
		goAnnotation3.setAssignedBy("GOC");
		list.add(goAnnotation3);


		//GO:0008152 - 4 annotatons, 4 proteins
		GOAnnotation goAnnotation4 = new GOAnnotation( );
		goAnnotation4.setGoID("GO:0008152");
		goAnnotation4.setDbObjectID("AOA001");
		goAnnotation4.setGoAspect("C");
		goAnnotation4.setGoEvidence("ECO:0000323");
		goAnnotation4.setReference("GO_REF:0000038");
		goAnnotation4.setAssignedBy("GOC");
		list.add(goAnnotation4);

		GOAnnotation goAnnotation5 = new GOAnnotation( );
		goAnnotation5.setGoID("GO:0008152");
		goAnnotation5.setDbObjectID("AOA002");
		goAnnotation5.setGoAspect("F");
		goAnnotation5.setGoEvidence("ECO:0000501");
		goAnnotation5.setReference("GO_REF:0000038");
		goAnnotation5.setAssignedBy("GOC");
		list.add(goAnnotation5);

		GOAnnotation goAnnotation6 = new GOAnnotation( );
		goAnnotation6.setGoID("GO:0008152");
		goAnnotation6.setDbObjectID("AOA007");
		goAnnotation6.setGoAspect("P");
		goAnnotation6.setGoEvidence("ECO:0000323");
		goAnnotation6.setReference("GO_REF:0000039");
		goAnnotation6.setAssignedBy("GOC");
		list.add(goAnnotation6);

		GOAnnotation goAnnotation7 = new GOAnnotation( );
		goAnnotation7.setGoID("GO:0008152");
		goAnnotation7.setDbObjectID("A0A009DX00");
		goAnnotation7.setGoAspect("C");
		goAnnotation7.setGoEvidence("ECO:0000323");
		goAnnotation7.setReference("GO_REF:0000039");
		goAnnotation7.setAssignedBy("GOC");
		list.add(goAnnotation7);


		//GO:0005524 - 5 annotations, 3 proteins
		GOAnnotation goAnnotation8 = new GOAnnotation( );
		goAnnotation8.setGoID("GO:0005524");
		goAnnotation8.setDbObjectID("AOA001");
		goAnnotation8.setGoAspect("F");
		goAnnotation8.setGoEvidence("ECO:0000323");
		goAnnotation8.setReference("GO_REF:0000038");
		goAnnotation8.setAssignedBy("GOC");
		list.add(goAnnotation8);

		GOAnnotation goAnnotation9 = new GOAnnotation( );
		goAnnotation9.setGoID("GO:0005524");
		goAnnotation9.setDbObjectID("AOA001");
		goAnnotation9.setGoAspect("C");
		goAnnotation9.setGoEvidence("ECO:0000501");
		goAnnotation9.setReference("GO_REF:0000038");
		goAnnotation9.setAssignedBy("GOC");
		list.add(goAnnotation9);

		GOAnnotation goAnnotation10 = new GOAnnotation( );
		goAnnotation10.setGoID("GO:0005524");
		goAnnotation10.setDbObjectID("AOA002");
		goAnnotation10.setGoAspect("C");
		goAnnotation10.setGoEvidence("ECO:0000501");
		goAnnotation10.setReference("GO_REF:0000040");
		goAnnotation10.setAssignedBy("GOC");
		list.add(goAnnotation10);

		GOAnnotation goAnnotation11 = new GOAnnotation( );
		goAnnotation11.setGoID("GO:0005524");
		goAnnotation11.setDbObjectID("A0A009DWC0");
		goAnnotation11.setGoAspect("P");
		goAnnotation11.setGoEvidence("ECO:0000323");
		goAnnotation11.setReference("GO_REF:0000040");
		goAnnotation11.setAssignedBy("GOC");
		list.add(goAnnotation11);

		GOAnnotation goAnnotation12 = new GOAnnotation( );
		goAnnotation12.setGoID("GO:0005524");
		goAnnotation12.setDbObjectID("A0A009DWC0");
		goAnnotation12.setGoAspect("F");
		goAnnotation12.setGoEvidence("ECO:0000999");
		goAnnotation12.setReference("GO_REF:0000040");
		goAnnotation12.setAssignedBy("GOC");
		list.add(goAnnotation12);
		return list;
	}
}
