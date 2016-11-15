package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;

import com.google.common.base.Preconditions;
import java.util.function.Function;
import org.springframework.batch.item.ItemProcessor;

/**
 * Uses the gene product identifier as the sharding key.The sharding key is used by Solr cloud to direct the record to
 * the right shard.
 *
 * See <a href="https://cwiki.apache.org/confluence/display/solr/Shards+and+Indexing+Data+in+SolrCloud">
 *     https://cwiki.apache.org/confluence/display/solr/Shards+and+Indexing+Data+in+SolrCloud</a>, for more
 *     information on Solr cloud and sharding.
 *
 * @author Ricardo Antunes
 */
class AnnotationPartitionKeyGenerator implements ItemProcessor<AnnotationDocument, AnnotationDocument> {
    static final String PARTITION_DELIMITER = "!";

    private final Function<AnnotationDocument, String> shardingKeyGenerator;

    AnnotationPartitionKeyGenerator(Function<AnnotationDocument, String> shardingKeyGenerator) {
        Preconditions.checkArgument(shardingKeyGenerator != null, "Sharding key generator cannot be null");

        this.shardingKeyGenerator = shardingKeyGenerator;
    }

    @Override public AnnotationDocument process(AnnotationDocument item) throws Exception {
        Preconditions.checkArgument(item.id != null && !item.id.isEmpty(),
                "Annotation identifier cannot be null or empty.");

        item.id = createIdWithPartition(item);

        return item;
    }

    private String createIdWithPartition(AnnotationDocument doc) {
        String shardingKey = shardingKeyGenerator.apply(doc);

        String idWithPartition;

        if (shardingKey == null) {
            throw new RuntimeException("Unable to generate a sharding key for: " + doc);
        } else if (shardingKey.isEmpty()) {
            idWithPartition = doc.id;
        } else {
            idWithPartition = shardingKey + PARTITION_DELIMITER + doc.id;
        }

        return idWithPartition;
    }
}