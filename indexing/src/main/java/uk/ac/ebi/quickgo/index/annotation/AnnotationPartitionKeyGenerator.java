package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import com.google.common.base.Preconditions;
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
public class AnnotationPartitionKeyGenerator implements ItemProcessor<AnnotationDocument, AnnotationDocument> {
    private static final String PARTITION_DELIMITER = "!";

    @Override public AnnotationDocument process(AnnotationDocument item) throws Exception {
        Preconditions.checkArgument(item.id != null && !item.id.isEmpty(),
                "Annotation identifier cannot be null or empty .");
        Preconditions.checkArgument(item.geneProductId != null && !item.geneProductId.isEmpty(),
                "Gene product identifier cannot be null or empty .");

        item.id = createIdWithPartition(item.id, item.geneProductId);

        return item;
    }

    private String createIdWithPartition(String identifier, String partitionKey) {
        return partitionKey + PARTITION_DELIMITER + identifier;
    }
}