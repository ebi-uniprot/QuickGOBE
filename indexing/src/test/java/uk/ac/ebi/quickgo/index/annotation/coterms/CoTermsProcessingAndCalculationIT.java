package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
@SpringBootTest(classes = {CoTermsConfig.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class CoTermsProcessingAndCalculationIT {

    private static final String NON_IEA_EVIDENCE = "ABC";
    @Autowired
    private CoTermsAggregationWriter coTermsManualAggregationWriter;
    @Autowired
    private ItemProcessor<String, List<CoTerm>> coTermsManualCalculator;
    @Autowired
    private ItemProcessor<String, List<CoTerm>> coTermsAllCalculator;
    @Autowired
    private CoTermsAggregationWriter coTermsAllAggregationWriter;

    @Test
    void similarityRatioIsAlwaysOneHundredForATermThatCoOccursWithItsSelf() throws Exception {
        final int noOfDocs = 1;
        List<AnnotationDocument> docsToWrite = createGenericDocs(noOfDocs);
        assertThat(docsToWrite, hasSize(noOfDocs));
        writeDocsToAllAggregationInstance(docsToWrite);
        final String targetTerm = docsToWrite.get(0).goId;
        List<CoTerm> coOccurringTerms = coTermsAllCalculator.process(targetTerm);

        assertThat(coOccurringTerms, hasSize(1));
        CoTerm result = coOccurringTerms.get(0);

        assertThat(result.getSimilarityRatio(), is(100.0f));
    }

    @Test
    void coTermCalculationForSingleTermDifferentGeneProducts() throws Exception {
        List<AnnotationDocument> docsToWrite = createGenericDocs(10);
        assertThat(docsToWrite, hasSize(10));
        writeDocsToAllAggregationInstance(docsToWrite);

        //For the passed in GO Term id, find the list of co-occurring terms and calculate CoTerm instances.
        final String targetTerm = docsToWrite.get(0).goId;
        List<CoTerm> coOccurringTerms = coTermsAllCalculator.process(targetTerm);

        assertThat(coOccurringTerms, hasSize(1));
        CoTerm result = coOccurringTerms.get(0);
        assertThat(result.getComparedTerm(), is(targetTerm));
        assertThat(result.getSimilarityRatio(), is(100.0f));
        assertThat(result.getProbabilityRatio(), is(1.0f));
        assertThat(result.getCompared(), is(10L));
        assertThat(result.getTogether(), is(10L));
    }

    @Test
    void coTermCalculationForTenTermsSameGeneProduct() throws Exception {
        Supplier<String> gpSupplier = () -> "A0A000";
        List<AnnotationDocument> docsToWrite = createGenericDocs(10, gpSupplier);
        assertThat(docsToWrite, hasSize(10));

        writeDocsToAllAggregationInstance(docsToWrite);

        final String targetTerm = docsToWrite.get(0).goId;
        List<CoTerm> coOccurringTerms = coTermsAllCalculator.process(targetTerm);

        assertThat(coOccurringTerms, hasSize(1));
        CoTerm result = coOccurringTerms.get(0);
        assertThat(result.getComparedTerm(), is(targetTerm));
        assertThat(result.getSimilarityRatio(), is(100.0f));
        assertThat(result.getProbabilityRatio(), is(1.0f));
        assertThat(result.getCompared(), is(1L));
        assertThat(result.getTogether(), is(1L));
    }

    @Test
    void checkCoTermCalculationForMixOfTermAndGeneProductAndEvidenceCode() throws Exception {
        List<AnnotationDocument> docsToWrite = new ArrayList<>();

        final String gp1 = "A0A000";
        final String term1 = "GO:0003824";
        docsToWrite.add(createAnnotationDoc(gp1, term1));
        docsToWrite.add(createAnnotationDoc(gp1, term1));

        final String gp2 = "A0A001";
        final String term2 = "GO:0016887";
        final AnnotationDocument docManual = createAnnotationDoc(gp2, term2);
        docManual.goEvidence = NON_IEA_EVIDENCE;
        docsToWrite.add(docManual);      //no impact on term1
        docsToWrite.add(createAnnotationDoc(gp2, term1));

        final String gp3 = "A0A002";
        final String term3 = "GO:0003870";
        docsToWrite.add(createAnnotationDoc(gp3, term3));      //only impacts probability ratio

        writeDocsToAllAggregationInstance(docsToWrite);

        //For the passed in GO Term id, find the list of co-occurring terms and calculate CoTerm instances.
        List<CoTerm> coOccurringTerms = coTermsAllCalculator.process(term1);
        assertThat(coOccurringTerms, hasSize(2));

        //term compared to its self will always be first as it has the highest similarity ratio
        //term1 vs term1
        CoTerm result = coOccurringTerms.get(0);
        assertThat(result.getComparedTerm(), is(term1));
        assertThat(result.getSimilarityRatio(), is(100.0f));
        assertThat(result.getProbabilityRatio(), is(1.5f));
        assertThat(result.getCompared(), is(2L));
        assertThat(result.getTogether(), is(2L));

        //term1 vs term2
        result = coOccurringTerms.get(1);
        assertThat(result.getComparedTerm(), is(term2));
        assertThat(result.getSimilarityRatio(), is(50.0f));
        assertThat(result.getProbabilityRatio(), is(1.5f));
        assertThat(result.getCompared(), is(1L));
        assertThat(result.getTogether(), is(1L));

        //Next term
        coOccurringTerms = coTermsAllCalculator.process(term2);

        //term2 vs term2
        assertThat(coOccurringTerms, hasSize(2));
        result = coOccurringTerms.get(0);
        assertThat(result.getComparedTerm(), is(term2));
        assertThat(result.getSimilarityRatio(), is(100.0f));
        assertThat(result.getProbabilityRatio(), is(3.0f));
        assertThat(result.getCompared(), is(1L));
        assertThat(result.getTogether(), is(1L));

        //term2 vs term1
        result = coOccurringTerms.get(1);
        assertThat(result.getComparedTerm(), is(term1));
        assertThat(result.getSimilarityRatio(), is(50.0f));
        assertThat(result.getProbabilityRatio(), is(1.5f));
        assertThat(result.getCompared(), is(2L));
        assertThat(result.getTogether(), is(1L));
    }

    @Test
    void thereShouldBeZeroCoTermRecordsIfTheAnnotationHasANonManualSource() throws Exception {
        final int noOfDocs = 1;
        List<AnnotationDocument> docsToWrite = createGenericDocs(noOfDocs);
        assertThat(docsToWrite, hasSize(noOfDocs));
        writeDocsToManualAggregationInstance(docsToWrite);
        final String targetTerm = docsToWrite.get(0).goId;
        assertThat(coTermsManualCalculator.process(targetTerm), hasSize(0));
    }

    @Test
    void simpleCalculationForManualOnlyAnnotationsProvesManualOpWorks() throws Exception {
        List<AnnotationDocument> docsToWrite = createGenericDocs(10).stream()
                                                                    .map(ad -> {
                                                                        ad.goEvidence = NON_IEA_EVIDENCE;
                                                                        return ad;
                                                                    })
                                                                    .collect(Collectors.toList());
        assertThat(docsToWrite, hasSize(10));

        writeDocsToAllAggregationInstance(docsToWrite);

        //For the passed in GO Term id, find the list of co-occurring terms and calculate CoTerm instances.
        final String targetTerm = docsToWrite.get(0).goId;
        List<CoTerm> coOccurringTerms = coTermsAllCalculator.process(targetTerm);

        assertThat(coOccurringTerms, hasSize(1));
        CoTerm result = coOccurringTerms.get(0);
        assertThat(result.getComparedTerm(), is(targetTerm));
        assertThat(result.getSimilarityRatio(), is(100.0f));
        assertThat(result.getProbabilityRatio(), is(1.0f));
        assertThat(result.getCompared(), is(10L));
        assertThat(result.getTogether(), is(10L));
    }

    private void writeDocsToAllAggregationInstance(List<AnnotationDocument> docsToWrite) throws Exception {
        coTermsAllAggregationWriter.write(docsToWrite);
        coTermsAllAggregationWriter.close();
    }

    private void writeDocsToManualAggregationInstance(List<AnnotationDocument> docsToWrite) throws Exception {
        coTermsManualAggregationWriter.write(docsToWrite);
        coTermsManualAggregationWriter.close();
    }

}
