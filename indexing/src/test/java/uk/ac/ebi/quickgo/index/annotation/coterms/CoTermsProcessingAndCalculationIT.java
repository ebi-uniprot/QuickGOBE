package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createAnnotationDoc;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocs;

/**
 * @author Tony Wardell
 * Date: 09/01/2017
 * Time: 14:16
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoTermsConfig.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class CoTermsProcessingAndCalculationIT {

    @Autowired
    private ItemProcessor<String, List<CoTerm>> coTermsAllCalculator;

    @Autowired
    private CoTermsAggregationWriter coTermsAllAggregationWriter;

    //Configuration
    private static final int NUMBER_OF_GENERIC_DOCS = 10;

    private List<AnnotationDocument> genericDocs;

    @Test
    public void coTermCalculationForSingleTermDifferentGeneProducts() throws Exception{
        final String TARGET_TERM = "GO:0003824";
        genericDocs = createGenericDocs(NUMBER_OF_GENERIC_DOCS);
        assertThat(genericDocs, hasSize(10));

        //Write to aggregation writer raw data.
        coTermsAllAggregationWriter.write(genericDocs);
        coTermsAllAggregationWriter.close();

        //For the passed in GO Term id, find the list of co-occurring terms and calculate CoTerm instances.
        List<CoTerm> results = coTermsAllCalculator.process(TARGET_TERM);

        assertThat(results, hasSize(1));
        CoTerm result = results.get(0);

        assertThat(result.getSimilarityRatio(), is(100.0f));
        assertThat(result.getProbabilityRatio(), is(1.0f));
        assertThat(result.getCompared(), is(10L));
        assertThat(result.getTogether(), is(10L));
    }

    @Test
    public void coTermCalculationForTenTermsSameGeneProduct() throws Exception{

        Supplier<String> gpSupplier = () -> "A0A000";
        genericDocs = createGenericDocs(10, gpSupplier);

        //Write to aggregation writer raw data.
        coTermsAllAggregationWriter.write(genericDocs);
        coTermsAllAggregationWriter.close();


        //For the passed in GO Term id, find the list of co-occurring terms and calculate CoTerm instances.
        final String TARGET_TERM = "GO:0003824";
        List<CoTerm> results = coTermsAllCalculator.process(TARGET_TERM);

        assertThat(results, hasSize(1));
        CoTerm result = results.get(0);

        assertThat(result.getSimilarityRatio(), is(100.0f));
        assertThat(result.getProbabilityRatio(), is(1.0f));
        assertThat(genericDocs, hasSize(10));
        assertThat(result.getCompared(), is(1L));
        assertThat(result.getTogether(), is(1L));
    }

    @Test
    public void coTermCalculationInDepth() throws Exception{

        List<AnnotationDocument> docs = new ArrayList<>();

        final String gp1 = "A0A000";
        final String term1 = "GO:0003824";
        docs.add(createAnnotationDoc(gp1, term1));
        docs.add(createAnnotationDoc(gp1, term1));

        final String gp2 = "A0A001";
        final String term2 = "GO:0016887";
        docs.add(createAnnotationDoc(gp2, term2));      //no impact on term1
        docs.add(createAnnotationDoc(gp2, term1));

        final String gp3 = "A0A002";
        final String term3 = "GO:0003870";
        docs.add(createAnnotationDoc(gp3, term3));      //only impacts probability ratio

        //Write to aggregation writer raw data.
        coTermsAllAggregationWriter.write(docs);
        coTermsAllAggregationWriter.close();


        //For the passed in GO Term id, find the list of co-occurring terms and calculate CoTerm instances.
        List<CoTerm> results = coTermsAllCalculator.process(term1);
        assertThat(results, hasSize(2));

        //term compared to its self will always be first as it has the highest similarity ratio
        //term1 vs term1
        CoTerm result = results.get(0);
        assertThat(result.getComparedTerm(), is(term1));
        assertThat(result.getSimilarityRatio(), is(100.0f));
        assertThat(result.getProbabilityRatio(), is(1.5f));
        assertThat(result.getCompared(), is(2L));
        assertThat(result.getTogether(), is(2L));

        //term1 vs term2
        result = results.get(1);
        assertThat(result.getComparedTerm(), is(term2));
        assertThat(result.getSimilarityRatio(), is(50.0f));
        assertThat(result.getProbabilityRatio(), is(1.5f));
        assertThat(result.getCompared(), is(1L));
        assertThat(result.getTogether(), is(1L));

        //Next term
        results = coTermsAllCalculator.process(term2);

        //term2 vs term2
        assertThat(results, hasSize(2));
        result = results.get(0);
        assertThat(result.getComparedTerm(), is(term2));
        assertThat(result.getSimilarityRatio(), is(100.0f));
        assertThat(result.getProbabilityRatio(), is(3.0f));
        assertThat(result.getCompared(), is(1L));
        assertThat(result.getTogether(), is(1L));

        //term2 vs term1
        result = results.get(1);
        assertThat(result.getComparedTerm(), is(term1));
        assertThat(result.getSimilarityRatio(), is(50.0f));
        assertThat(result.getProbabilityRatio(), is(1.5f));
        assertThat(result.getCompared(), is(2L));
        assertThat(result.getTogether(), is(1L));
    }
}
