package uk.ac.ebi.quickgo.index.annotation.costats;
import uk.ac.ebi.quickgo.common.costats.HitCount;
import uk.ac.ebi.quickgo.index.annotation.Annotation;
import uk.ac.ebi.quickgo.index.annotation.AnnotationMocker;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * @Author Tony Wardell
 * Date: 26/11/2015
 * Time: 16:26
 * Created with IntelliJ IDEA.
 */
public class CoStatsPermutationsTest {

    CoStatsPermutations coStatsPermutations;

    @Before
    public void setup(){
        coStatsPermutations = new CoStatsPermutations();
    }

	@Test
	public void calculateStatisticsForTwoRecordsWithTheSameGoTerm(){

		Annotation annotation1 = AnnotationMocker.createValidAnnotation();
        Annotation annotation2 = AnnotationMocker.createValidAnnotation();

        List<Annotation> annotations = Arrays.asList(annotation1, annotation2);
        annotations.forEach(coStatsPermutations::addRowToMatrix);
        coStatsPermutations.finish();

        //Now test
        Map<String, Map<String, HitCount>> matrix = coStatsPermutations.getTermToTermOverlapMatrix();

        assertThat(matrix.keySet(), hasSize(1));

        Map<String, HitCount> costats = matrix.get("GO:0000977");
        assertThat(costats, is(notNullValue()));
        assertThat(costats.keySet(), hasSize(1));

        //Is the only one
        HitCount hc = costats.get("GO:0000977");
        assertThat(hc.hits, is(1l));

	}

    @Test
    public void calculateStatisticsForTwoRecordsWithTheDifferentGoTermsDifferentGeneProductSoNoCoStats(){

        Annotation annotation1 = AnnotationMocker.createValidAnnotation();
        Annotation annotation2 = AnnotationMocker.createValidAnnotation();
        annotation2.goId = "GO:0009999";
        annotation2.dbObjectId = "A0A000";

        List<Annotation> annotations = Arrays.asList(annotation1, annotation2);
        annotations.forEach(coStatsPermutations::addRowToMatrix);

        coStatsPermutations.finish();

        //Now test
        Map<String, Map<String, HitCount>> matrix = coStatsPermutations.getTermToTermOverlapMatrix();

        assertThat(matrix.keySet(), hasSize(2));

        Map<String, HitCount> costats1 = matrix.get(annotation1.goId);
        assertThat(costats1.keySet(), hasSize(1));//2
        HitCount hc1 = costats1.get(annotation1.goId);
        assertThat(hc1.hits, is(1l));

        Map<String, HitCount> costats2 = matrix.get(annotation2.goId);
        assertThat(costats2.keySet(), hasSize(1));
        HitCount hc2 = costats2.get(annotation2.goId);
        assertThat(hc2.hits, is(1l));

    }


    @Test
    public void calculateStatisticsForTwoRecordsWithTheDifferentGoTermsSameGeneProduct(){

        Annotation annotation1 = AnnotationMocker.createValidAnnotation();
        Annotation annotation2 = AnnotationMocker.createValidAnnotation();
        annotation2.goId = "GO:0009999";
        List<Annotation> annotations = Arrays.asList(annotation1, annotation2);
        annotations.forEach(coStatsPermutations::addRowToMatrix);
        coStatsPermutations.finish();

        //Now test
        Map<String, Map<String, HitCount>> matrix = coStatsPermutations.getTermToTermOverlapMatrix();

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
    }



}
