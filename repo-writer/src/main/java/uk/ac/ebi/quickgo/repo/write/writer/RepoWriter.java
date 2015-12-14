package uk.ac.ebi.quickgo.repo.write.writer;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.ontology.OntologyRepository;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

/**
 * Created 02/12/15
 * @author Edd
 */
public class RepoWriter implements ItemWriter<OntologyDocument> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(RepoWriter.class);
    private final OntologyRepository repository;

    public RepoWriter(OntologyRepository repository) {
        this.repository = repository;
    }

    @Override public void write(List<? extends OntologyDocument> list) throws Exception {
        LOGGER.info("Writing to the repository.");
        repository.save(list);
    }
}
