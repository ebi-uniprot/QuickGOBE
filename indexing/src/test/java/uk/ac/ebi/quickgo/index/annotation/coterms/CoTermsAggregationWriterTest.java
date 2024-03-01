package uk.ac.ebi.quickgo.index.annotation.coterms;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 16:26
 * Created with IntelliJ IDEA.
 */
class CoTermsAggregationWriterTest {

    private static final String[] TWO_SAME_GENE_PRODUCTS = {"A0A000", "A0A000"};
    private static final String[] TWO_DIFFERENT_GENE_PRODUCTS = {"A0A000", "A0A001"};
    private static final String REPLACEMENT_GO_ID = "GO:0009999";
    private CoTermsAggregationWriter aggregator;

    @BeforeEach
    void setup() {
        aggregator = new CoTermsAggregationWriter(t -> true);
    }

    @Test
    void buildCoTermsForSelectedTermFor2DocsSameGoTermSameGeneProduct() throws Exception {
        List<AnnotationDocument> docs = createDocs(TWO_SAME_GENE_PRODUCTS);
        completeAggregation(docs);
        CoTermsForSelectedTerm coTermsForSelectedTerm =
                aggregator.createCoTermsForSelectedTerm(AnnotationDocMocker.GO_ID);
        assertThat(coTermsForSelectedTerm.highestSimilarity().size(), is(1));
    }

    @Test
    void buildCoTermsForSelectedTermFor2Docs2GoTerms2GeneProductsSoNoCoTerms() throws Exception {
        List<AnnotationDocument> docs = createDocs(TWO_DIFFERENT_GENE_PRODUCTS);
        docs.get(1).goId = REPLACEMENT_GO_ID;
        completeAggregation(docs);

        CoTermsForSelectedTerm coTermsForSelectedTerm =
                aggregator.createCoTermsForSelectedTerm(AnnotationDocMocker.GO_ID);
        assertThat(coTermsForSelectedTerm.highestSimilarity().size(), is(1));

        coTermsForSelectedTerm = aggregator.createCoTermsForSelectedTerm(REPLACEMENT_GO_ID);
        assertThat(coTermsForSelectedTerm.highestSimilarity().size(), is(1));
    }

    @Test
    void buildCoTermsForSelectedTermFor2Docs2GoTermsSameGeneProducts() throws Exception {
        List<AnnotationDocument> docs = createDocs(TWO_SAME_GENE_PRODUCTS);
        docs.get(1).goId = REPLACEMENT_GO_ID;
        completeAggregation(docs);

        CoTermsForSelectedTerm coTermsForSelectedTerm =
                aggregator.createCoTermsForSelectedTerm(AnnotationDocMocker.GO_ID);
        assertThat(coTermsForSelectedTerm.highestSimilarity().size(), is(2));

        coTermsForSelectedTerm = aggregator.createCoTermsForSelectedTerm(REPLACEMENT_GO_ID);
        assertThat(coTermsForSelectedTerm.highestSimilarity().size(), is(2));
    }

    @Test
    void exceptionThrownIfNullAnnotationPassedToAddRowToMatrix() {
        assertThrows(IllegalArgumentException.class, () -> aggregator.write(null));
    }

    @Test
    void exceptionThrownIfNullPredicatePassedToConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new CoTermsAggregationWriter(null));
    }

    private List<AnnotationDocument> createDocs(String... geneProductIds) {
        List<AnnotationDocument> docs = new ArrayList<>();

        for (String geneProductId : geneProductIds) {
            docs.add(AnnotationDocMocker.createAnnotationDoc(geneProductId));
        }
        return docs;
    }

    private void completeAggregation(List<AnnotationDocument> docs) throws Exception {
        aggregator.write(new Chunk<>(docs));
        aggregator.close();
    }

}
