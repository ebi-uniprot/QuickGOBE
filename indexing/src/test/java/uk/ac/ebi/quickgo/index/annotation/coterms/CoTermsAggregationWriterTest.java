package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Before;
import org.junit.Test;

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
public class CoTermsAggregationWriterTest {

    private static final String[] TWO_SAME_GENEPRODUCTS = {"A0A000", "A0A000"};
    private static final String[] TWO_DIFFERENT_GENE_PRODUCTS = {"A0A000", "A0A001"};
    private static final String REPLACEMENT_GOID = "GO:0009999";
    private CoTermsAggregationWriter aggregator;

    @Before
    public void setup() {
        aggregator = new CoTermsAggregationWriter(t -> true);
    }

    @Test
    public void calculateStatisticsForTwoRecordsWithTheSameGoTerm() throws Exception {
        List<AnnotationDocument> docs = writeDocs(TWO_SAME_GENEPRODUCTS);
        completeAggregation(docs);

        Map<String, AtomicLong> coTerms = aggregator.getCoTerms(AnnotationDocMocker.GO_ID);
        assertThat(coTerms, is(notNullValue()));
        assertThat(coTerms.keySet(), hasSize(1));

        //Is the only one
        AtomicLong ac = coTerms.get(AnnotationDocMocker.GO_ID);
        assertThat(ac.get(), is(1L));

        assertThat(aggregator.getTotalOfAnnotatedGeneProducts(), is(1L));
        assertThat(aggregator.getGeneProductCountForGoTerm(docs.get(0).goId), is(1L));
        assertThat(aggregator.getGeneProductCountForGoTerm(docs.get(1).goId), is(1L));

    }

    @Test
    public void calculateStatisticsForTwoRecordsWithDifferentGoTermsAndDifferentGeneProductSoNoCoTerms() throws Exception {
        List<AnnotationDocument> docs = writeDocs(TWO_DIFFERENT_GENE_PRODUCTS);
        docs.get(1).goId = REPLACEMENT_GOID;
        completeAggregation(docs);

        Map<String, AtomicLong> coTerms1 = aggregator.getCoTerms(docs.get(0).goId);
        assertThat(coTerms1.keySet(), hasSize(1));
        AtomicLong ac1 = coTerms1.get(docs.get(0).goId);
        assertThat(ac1.get(), is(1L));

        Map<String, AtomicLong> coTerms2 = aggregator.getCoTerms(docs.get(1).goId);
        assertThat(coTerms2.keySet(), hasSize(1));
        AtomicLong ac2 = coTerms2.get(docs.get(1).goId);
        assertThat(ac2.get(), is(1L));

        assertThat(aggregator.getTotalOfAnnotatedGeneProducts(), is(2L));
        assertThat(aggregator.getGeneProductCountForGoTerm(docs.get(0).goId), is(1L));
        assertThat(aggregator.getGeneProductCountForGoTerm(docs.get(1).goId), is(1L));
    }

    @Test
    public void calculateStatisticsForTwoRecordsWithTheDifferentGoTermsSameGeneProduct() throws Exception {
        List<AnnotationDocument> docs = writeDocs(TWO_SAME_GENEPRODUCTS);
        docs.get(1).goId = REPLACEMENT_GOID;
        completeAggregation(docs);

        Map<String, AtomicLong> coTerms1 =aggregator.getCoTerms(docs.get(0).goId);
        assertThat(coTerms1.keySet(), hasSize(2));
        AtomicLong ac1x1 = coTerms1.get(docs.get(0).goId);
        assertThat(ac1x1.get(), is(1L));
        AtomicLong ac1x2 = coTerms1.get(docs.get(1).goId);
        assertThat(ac1x2.get(), is(1L));

        Map<String, AtomicLong> coTerms2 = aggregator.getCoTerms(docs.get(1).goId);
        assertThat(coTerms2.keySet(), hasSize(2));
        AtomicLong ac2x1 = coTerms2.get(docs.get(1).goId);
        assertThat(ac2x1.get(), is(1L));
        AtomicLong ac2x2 = coTerms2.get(docs.get(1).goId);
        assertThat(ac2x2.get(), is(1L));

        assertThat(aggregator.getTotalOfAnnotatedGeneProducts(), is(1L));
        assertThat(aggregator.getGeneProductCountForGoTerm(docs.get(0).goId), is(1L));
        assertThat(aggregator.getGeneProductCountForGoTerm(docs.get(1).goId), is(1L));
    }

    @Test
    public void zeroAnnotationsProcessedIfPredicateNotTrue() throws Exception {
        List<AnnotationDocument> docs = writeDocs(TWO_SAME_GENEPRODUCTS);
        completeAggregation(docs);

        CoTermsAggregationWriter aggregatorFalse = new CoTermsAggregationWriter(t -> false);
        assertThat(aggregatorFalse.getTotalOfAnnotatedGeneProducts(), is(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfNullAnnotationPassedToAddRowToMatrix() throws Exception {
        aggregator.write(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfNullPredicatePassedToConstructor() {
        new CoTermsAggregationWriter(null);
    }

    private List<AnnotationDocument> writeDocs(String... geneProductIds) throws Exception {
        List<AnnotationDocument> docs = new ArrayList<>();

        for (String geneProductId : geneProductIds) {
            docs.add(AnnotationDocMocker.createAnnotationDoc(geneProductId));
        }
        return docs;
    }

    private void completeAggregation(List<AnnotationDocument> docs) throws Exception {
        aggregator.write(docs);
        aggregator.finish();
    }

}
