package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import java.util.function.Function;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationPartitionKeyGenerator.PARTITION_DELIMITER;

/**
 * Tests the behaviour of the {@link AnnotationPartitionKeyGenerator} class.
 */
public class AnnotationPartitionKeyGeneratorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String DOC_ID = "P12345";
    private static final String GENE_PRODUCT_ID = "cac1";

    private Function<AnnotationDocument, String> geneProductShardingKey;

    private AnnotationPartitionKeyGenerator generator;

    private final AnnotationDocument doc = new AnnotationDocument();

    @Before
    public void setUp() throws Exception {
        geneProductShardingKey = (AnnotationDocument doc) -> doc.geneProductId;

        generator = new AnnotationPartitionKeyGenerator(geneProductShardingKey);

        doc.id = DOC_ID;
        doc.geneProductId = GENE_PRODUCT_ID;
    }

    @Test
    public void nullShardingFunctionThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Sharding key generator cannot be null");

        generator = new AnnotationPartitionKeyGenerator(null);
    }

    @Test
    public void nullAnnotationDocumentIdThrowsException() throws Exception {
        AnnotationDocument doc = new AnnotationDocument();
        doc.id = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation identifier cannot be null or empty.");

        generator.process(doc);
    }

    @Test
    public void emptyAnnotationDocumentIdThrowsException() throws Exception {
        AnnotationDocument doc = new AnnotationDocument();
        doc.id = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation identifier cannot be null or empty.");

        generator.process(doc);
    }

    @Test
    public void nullShardingKeyThrowsException() throws Exception {
        generator = new AnnotationPartitionKeyGenerator((AnnotationDocument aDoc) -> null);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(startsWith("Unable to generate a sharding key for:"));

        generator.process(doc);
    }

    @Test
    public void emptyShardingKeyReturnsJust() throws Exception {
        generator = new AnnotationPartitionKeyGenerator((AnnotationDocument aDoc) -> "");

        AnnotationDocument modifiedDoc = generator.process(doc);

        assertThat(modifiedDoc.id, is(DOC_ID));
    }

    @Test
    public void processorModifiesDocumentIdSuccessfully() throws Exception {
        AnnotationDocument modifiedDoc = generator.process(doc);

        assertThat(modifiedDoc.id, is(generateIdentifierWithShardingKey(DOC_ID)));
    }

    private String generateIdentifierWithShardingKey(String id) {
        return GENE_PRODUCT_ID + PARTITION_DELIMITER + id;
    }
}