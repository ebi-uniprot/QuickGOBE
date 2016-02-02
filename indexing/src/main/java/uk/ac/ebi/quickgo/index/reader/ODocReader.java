package uk.ac.ebi.quickgo.index.reader;

import uk.ac.ebi.quickgo.ff.files.ontology.ECOSourceFiles;
import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.loader.ontology.ECOLoader;
import uk.ac.ebi.quickgo.ff.loader.ontology.GOLoader;
import uk.ac.ebi.quickgo.index.reader.converter.GOTermToODocConverter;
import uk.ac.ebi.quickgo.index.reader.converter.GenericTermToODocConverter;
import uk.ac.ebi.quickgo.index.write.job.IndexingJobConfig;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import java.io.File;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * This provides standard reading of an ontology core source file, which can be
 * hooked into a Spring Batch step, see {@link IndexingJobConfig}.
 *
 * Created 03/12/15
 * @author Edd
 */
public class ODocReader extends AbstractItemStreamItemReader<OntologyDocument> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODocReader.class);

    private static final GOTermToODocConverter GO_TERM_TO_DOC_CONVERTER = new GOTermToODocConverter();
    private static final GenericTermToODocConverter GENERIC_TERM_TO_DOC_CONVERTER = new GenericTermToODocConverter();

    private final Optional<GeneOntology> goOptional;
    private final Optional<EvidenceCodeOntology> ecoOptional;

    private Iterator<GenericTerm> goTermIterator;
    private Iterator<GenericTerm> ecoTermIterator;

    public ODocReader(File sourceFileDir) {
        this(
                new GOLoader(new GOSourceFiles(requireNonNull(sourceFileDir))).load(),
                new ECOLoader(new ECOSourceFiles(requireNonNull(sourceFileDir))).load());
    }

    ODocReader(Optional<GeneOntology> goOptional, Optional<EvidenceCodeOntology>
            ecoOptional) {
        this.goOptional = goOptional;
        this.ecoOptional = ecoOptional;
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
            Optional<OntologyDocument> optionalDoc =
                    GO_TERM_TO_DOC_CONVERTER.apply(Optional.of((GOTerm) goTermIterator.next()));
            return extractOntologyDocument(optionalDoc);
        } else if (ecoTermIterator.hasNext()) {
            Optional<OntologyDocument> optionalDoc =
                    GENERIC_TERM_TO_DOC_CONVERTER.apply(Optional.of(ecoTermIterator.next()));
            return extractOntologyDocument(optionalDoc);
        } else {
            return null;
        }
    }

    protected OntologyDocument extractOntologyDocument(Optional<OntologyDocument> optionalDoc)
            throws DocumentReaderException {
        if (optionalDoc.isPresent()) {
            return optionalDoc.get();
        } else {
            throw new DocumentReaderException("The converted Optional<OntologyDocument> should never be empty");
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

        if (goOptional.isPresent()) {
            this.goTermIterator = goOptional.get().getTerms().iterator();
            LOGGER.info("Loaded Gene Ontology successfully");
        } else {
            LOGGER.error("Could not load Gene Ontology from source files.");
            throw new DocumentReaderException("Could not load Gene Ontology from source files.");
        }

        if (ecoOptional.isPresent()) {
            this.ecoTermIterator = ecoOptional.get().getTerms().iterator();
            LOGGER.info("Loaded Evidence Code Ontology successfully");
        } else {
            LOGGER.error("Could not load Evidence Code Ontology from source files.");
            throw new DocumentReaderException("Could not load Gene Ontology from source files.");
        }
    }
}
