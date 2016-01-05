package uk.ac.ebi.quickgo.repo.reader;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.ff.files.ontology.ECOSourceFiles;
import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.loader.ontology.ECOLoader;
import uk.ac.ebi.quickgo.ff.loader.ontology.GOLoader;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.repo.reader.converter.GOTermToODocConverter;
import uk.ac.ebi.quickgo.repo.reader.converter.GenericTermToODocConverter;

import java.io.File;
import java.util.Iterator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

/**
 * This provides standard reading of an ontology core source file, which can be
 * hooked into a Spring Batch step, see {@link IndexingJobConfig}.
 *
 * Created 03/12/15
 * @author Edd
 */
public class ODocReader extends AbstractItemStreamItemReader<OntologyDocument> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ODocReader.class);

    private File sourceFileDir;
    private static final GOTermToODocConverter GO_TERM_TO_DOC_CONVERTER = new GOTermToODocConverter();
    private static final GenericTermToODocConverter GENERIC_TERM_TO_DOC_CONVERTER = new GenericTermToODocConverter();
    private Iterator<GenericTerm> goTermIterator;
    private Iterator<GenericTerm> ecoTermIterator;

    public ODocReader(File sourceFileDir) {
        this.sourceFileDir = sourceFileDir;
    }

    /**
     * Creates and returns {@link OntologyDocument} instances corresponding to
     * each known GO / ECO term.
     *
     * @return
     * @throws Exception
     */
    @Override public OntologyDocument read() throws Exception {
        if (goTermIterator.hasNext()) {
            return GO_TERM_TO_DOC_CONVERTER.apply(Optional.of((GOTerm) goTermIterator.next())).get();
        } else if (ecoTermIterator.hasNext()) {
            return GENERIC_TERM_TO_DOC_CONVERTER.apply(Optional.of(ecoTermIterator.next())).get();
        } else {
            return null;
        }
    }

    @Override public void close() {
        // no op
    }

    /**
     * Open both Gene and Evidence Code ontologies. These will be used as data
     * sources for indexing to Solr.
     * @param executionContext
     */
    @Override public void open(ExecutionContext executionContext) {
        super.open(executionContext);

        try {
            GOLoader goLoader = new GOLoader(new GOSourceFiles(this.sourceFileDir));
            ECOLoader ecoLoader = new ECOLoader(new ECOSourceFiles(this.sourceFileDir));

            Optional<GeneOntology> geneOntologyOptional = goLoader.load();
            Optional<EvidenceCodeOntology> ecoOptional = ecoLoader.load();

            if (geneOntologyOptional.isPresent()) {
                this.goTermIterator = geneOntologyOptional.get().getTerms().iterator();
                LOGGER.info("Loaded Gene Ontology successfully");
            } else {
                LOGGER.error("Problem during indexing: could not load Gene Ontology from source files.");
            }

            if (ecoOptional.isPresent()) {
                this.ecoTermIterator = ecoOptional.get().getTerms().iterator();
                LOGGER.info("Loaded Evidence Code Ontology successfully");
            } else {
                LOGGER.error("Problem during indexing: could not load Evidence Code Ontology from source files.");
            }

        } catch (Exception e) {
            LOGGER.error("Problem during indexing: could not open ontology input files", e);
        }
    }
}
