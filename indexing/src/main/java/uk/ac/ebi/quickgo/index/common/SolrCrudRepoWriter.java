package uk.ac.ebi.quickgo.index.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.solr.repository.SolrCrudRepository;

import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.index.ontology.OntologyConfig;

import java.util.List;

/**
 * Generic {@link ItemWriter} for a {@link SolrCrudRepository}. Its is used
 * in a Spring batch job setup for writing chunks of data, i.e., the
 * parameter of the {@link SolrCrudRepoWriter#write(List)} method, to
 * the configured repository. The size of this list is specified
 * during job configuration.
 *
 * See also, {@link OntologyConfig}
 *
 * Created 02/12/15
 * @author Edd
 */
public class SolrCrudRepoWriter<D extends QuickGODocument, R extends SolrCrudRepository<D, String>> implements
        ItemWriter<D> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrCrudRepoWriter.class);
    private final SolrCrudRepository repository;

    public SolrCrudRepoWriter(R repository) {
        this.repository = repository;
    }

    @Override public void write(List<? extends D> list) throws Exception {
        LOGGER.info("Writing batch to the repository.");
        repository.saveAll(list);
    }
}
