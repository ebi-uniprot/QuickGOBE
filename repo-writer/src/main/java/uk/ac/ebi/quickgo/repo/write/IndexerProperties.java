package uk.ac.ebi.quickgo.repo.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created 02/12/15
 * @author Edd
 */
@Component
public class IndexerProperties {
    private final String ontologySourceFile;

    private final int ontologyChunkSize;

    @Autowired
    public IndexerProperties(
            @Value("${indexing.ontology.source}") String ontologySourceFile,
            @Value("${indexing.ontology.chunk.size}") int ontologyChunkSize) {
        this.ontologySourceFile = ontologySourceFile;
        this.ontologyChunkSize = ontologyChunkSize;
    }

    public String getOntologySourceFile() {
        return ontologySourceFile;
    }

    public int getOntologyChunkSize() {
        return ontologyChunkSize;
    }
}
