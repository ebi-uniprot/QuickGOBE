package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 16:26
 * Created with IntelliJ IDEA.
 */
public class AnnotationCoOccurringTermsAggregatorTest {

    AnnotationCo_occurringTermsAggregator aggregator;

    @Before
    public void setup(){
        aggregator = new AnnotationCo_occurringTermsAggregator(t -> true);
    }

	@Test
    public void calculateStatisticsForTwoRecordsWithTheSameGoTerm() throws Exception {

        AnnotationDocument annotation1 = AnnotationDocMocker.createAnnotationDoc("A0A000");
        AnnotationDocument annotation2 = AnnotationDocMocker.createAnnotationDoc("A0A000");
        List<AnnotationDocument> docs = Arrays.asList(annotation1, annotation2);
        aggregator.write(docs);
        aggregator.finish();

        //Now test
        Map<String, Map<String, HitCount>> matrix = aggregator.getTermToTermOverlapMatrix();

        assertThat(matrix.keySet(), hasSize(1));

        Map<String, HitCount> costats = matrix.get("GO:0003824");
        assertThat(costats, is(notNullValue()));
        assertThat(costats.keySet(), hasSize(1));

        //Is the only one
        HitCount hc = costats.get("GO:0003824");
        assertThat(hc.hits, is(1l));

        assertThat(aggregator.getTotalOfAnnotatedGeneProducts(), is(1l));
        assertThat(aggregator.getGeneProductCounts().keySet(), hasSize(1));
        assertThat(aggregator.getGeneProductCounts().get(annotation1.goId).hits, is(1L));
        assertThat(aggregator.getGeneProductCounts().get(annotation2.goId).hits, is(1L));

	}

    @Test
    public void calculateStatisticsForTwoRecordsWithDifferentGoTermsAndDifferentGeneProductSoNoCoStats() throws
                                                                                                       Exception {

        AnnotationDocument annotation1 = AnnotationDocMocker.createAnnotationDoc("A0A000");
        AnnotationDocument annotation2 = AnnotationDocMocker.createAnnotationDoc("A0A001");
        annotation2.goId = "GO:0009999";
        List<AnnotationDocument> docs = Arrays.asList(annotation1, annotation2);
        aggregator.write(docs);
        aggregator.finish();

        //Now test
        Map<String, Map<String, HitCount>> matrix = aggregator.getTermToTermOverlapMatrix();

        assertThat(matrix.keySet(), hasSize(2));

        Map<String, HitCount> costats1 = matrix.get(annotation1.goId);
        assertThat(costats1.keySet(), hasSize(1));//2
        HitCount hc1 = costats1.get(annotation1.goId);
        assertThat(hc1.hits, is(1l));

        Map<String, HitCount> costats2 = matrix.get(annotation2.goId);
        assertThat(costats2.keySet(), hasSize(1));
        HitCount hc2 = costats2.get(annotation2.goId);
        assertThat(hc2.hits, is(1l));

        assertThat(aggregator.getTotalOfAnnotatedGeneProducts(), is(2l));
        assertThat(aggregator.getGeneProductCounts().keySet(), hasSize(2));
        assertThat(aggregator.getGeneProductCounts().get(annotation1.goId).hits, is(1L));
        assertThat(aggregator.getGeneProductCounts().get(annotation2.goId).hits, is(1L));
    }


    @Test
    public void calculateStatisticsForTwoRecordsWithTheDifferentGoTermsSameGeneProduct() throws Exception {

        AnnotationDocument annotation1 = AnnotationDocMocker.createAnnotationDoc("A0A000");
        AnnotationDocument annotation2 = AnnotationDocMocker.createAnnotationDoc("A0A000");
        annotation2.goId = "GO:0009999";
        List<AnnotationDocument> docs = Arrays.asList(annotation1, annotation2);
        aggregator.write(docs);
        aggregator.finish();

        //Now test
        Map<String, Map<String, HitCount>> matrix = aggregator.getTermToTermOverlapMatrix();

        assertThat(matrix.keySet(), hasSize(2));

        Map<String, HitCount> costats1 = matrix.get(annotation1.goId);
        assertThat(costats1.keySet(), hasSize(2));
        HitCount hc1x1 = costats1.get(annotation1.goId);
        assertThat(hc1x1.hits, is(1l));
        HitCount hc1x2 = costats1.get(annotation2.goId);
        assertThat(hc1x1.hits, is(1l));

        Map<String, HitCount> costats2 = matrix.get(annotation2.goId);
        assertThat(costats2.keySet(), hasSize(2));
        HitCount hc2x1 = costats2.get(annotation2.goId);
        assertThat(hc2x1.hits, is(1l));
        HitCount hc2x2 = costats2.get(annotation1.goId);
        assertThat(hc2x2.hits, is(1l));

        assertThat(aggregator.getTotalOfAnnotatedGeneProducts(), is(1l));
        assertThat(aggregator.getGeneProductCounts().keySet(), hasSize(2));
        assertThat(aggregator.getGeneProductCounts().get(annotation1.goId).hits, is(1L));
        assertThat(aggregator.getGeneProductCounts().get(annotation2.goId).hits, is(1L));
    }

    @Test
    public void zeroAnnotationsProcessedIfPredicateNotTrue() throws Exception {

        AnnotationDocument annotation1 = AnnotationDocMocker.createAnnotationDoc("A0A000");
        AnnotationDocument annotation2 = AnnotationDocMocker.createAnnotationDoc("A0A000");
        AnnotationCo_occurringTermsAggregator aggregatorFalse = new AnnotationCo_occurringTermsAggregator(t -> false);
        List<AnnotationDocument> docs = Arrays.asList(annotation1, annotation2);
        aggregator.write(docs);
        aggregator.finish();

        //Now test
        Map<String, Map<String, HitCount>> matrix = aggregatorFalse.getTermToTermOverlapMatrix();

        assertThat(matrix.keySet(), hasSize(0));

        assertThat(aggregatorFalse.getTotalOfAnnotatedGeneProducts(), is(0l));
        assertThat(aggregatorFalse.getGeneProductCounts().keySet(), hasSize(0));

    }

    @Test(expected=IllegalArgumentException.class)
    public void exceptionThrownIfNullAnnotationPassedToAddRowToMatrix() throws Exception {
        aggregator.write(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfNullPredicatePassedToConstructor() {
        new AnnotationCo_occurringTermsAggregator(null);
    }

}
