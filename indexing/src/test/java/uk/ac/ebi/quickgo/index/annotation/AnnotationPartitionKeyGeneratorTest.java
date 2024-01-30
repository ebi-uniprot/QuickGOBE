package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;

import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationPartitionKeyGenerator.PARTITION_DELIMITER;

/**
 * Tests the behaviour of the {@link AnnotationPartitionKeyGenerator} class.
 */
class AnnotationPartitionKeyGeneratorTest {

    private static final String DOC_ID = "P12345";
    private static final String GENE_PRODUCT_ID = "cac1";

    private Function<AnnotationDocument, String> geneProductShardingKey;

    private AnnotationPartitionKeyGenerator generator;

    private final AnnotationDocument doc = new AnnotationDocument();

    @BeforeEach
    void setUp() {
        geneProductShardingKey = (AnnotationDocument doc) -> doc.geneProductId;

        generator = new AnnotationPartitionKeyGenerator(geneProductShardingKey);

        doc.id = DOC_ID;
        doc.geneProductId = GENE_PRODUCT_ID;
    }

    @Test
    void nullShardingFunctionThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> generator = new AnnotationPartitionKeyGenerator(null));
        assertTrue(exception.getMessage().contains("Sharding key generator cannot be null"));
    }

    @Test
    void nullAnnotationDocumentIdThrowsException() {
        AnnotationDocument doc = new AnnotationDocument();
        doc.id = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> generator.process(doc));
        assertTrue(exception.getMessage().contains("Annotation identifier cannot be null or empty."));
    }

    @Test
    void emptyAnnotationDocumentIdThrowsException() {
        AnnotationDocument doc = new AnnotationDocument();
        doc.id = "";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> generator.process(doc));
        assertTrue(exception.getMessage().contains("Annotation identifier cannot be null or empty."));
    }

    @Test
    void nullShardingKeyThrowsException() {
        generator = new AnnotationPartitionKeyGenerator((AnnotationDocument aDoc) -> null);

        Throwable exception = assertThrows(RuntimeException.class, () -> generator.process(doc));
        assertTrue(exception.getMessage().startsWith("Unable to generate a sharding key for:"));
    }

    @Test
    void emptyShardingKeyReturnsJust() throws Exception {
        generator = new AnnotationPartitionKeyGenerator((AnnotationDocument aDoc) -> "");

        AnnotationDocument modifiedDoc = generator.process(doc);

        assertThat(modifiedDoc.id, is(DOC_ID));
    }

    @Test
    void processorModifiesDocumentIdSuccessfully() throws Exception {
        AnnotationDocument modifiedDoc = generator.process(doc);

        assertThat(modifiedDoc.id, is(generateIdentifierWithShardingKey(DOC_ID)));
    }

    private String generateIdentifierWithShardingKey(String id) {
        return GENE_PRODUCT_ID + PARTITION_DELIMITER + id;
    }
}