package uk.ac.ebi.quickgo.index.write;

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
    private final int ontologySkipLimit;

    @Autowired
    public IndexerProperties(
            @Value("${indexing.ontology.source}") String ontologySourceFile,
            @Value("${indexing.ontology.chunk.size:500}") int ontologyChunkSize,
            @Value("${indexing.ontology.skip.limit:100}") int ontologySkipLimit) {
        this.ontologySourceFile = ontologySourceFile;
        this.ontologyChunkSize = ontologyChunkSize;
        this.ontologySkipLimit = ontologySkipLimit;
    }

    public int getOntologySkipLimit() {
        return ontologySkipLimit;
    }

    public String getOntologySourceFile() {
        return ontologySourceFile;
    }

    public int getOntologyChunkSize() {
        return ontologyChunkSize;
    }
}
