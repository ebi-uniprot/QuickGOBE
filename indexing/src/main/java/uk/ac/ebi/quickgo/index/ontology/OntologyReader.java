package uk.ac.ebi.quickgo.index.ontology;

import uk.ac.ebi.quickgo.ff.files.ontology.ECOSourceFiles;
import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.loader.ontology.ECOLoader;
import uk.ac.ebi.quickgo.ff.loader.ontology.GOLoader;
import uk.ac.ebi.quickgo.index.ontology.converter.GOTermToODocConverter;
import uk.ac.ebi.quickgo.index.ontology.converter.GenericTermToODocConverter;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * This provides standard reading of an ontology core source file, which can be
 * hooked into a Spring Batch step, see {@link OntologyConfig}.
 *
 * Created 03/12/15
 * @author Edd
 */
public class OntologyReader extends AbstractItemStreamItemReader<OntologyDocument> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyReader.class);

    private static final GOTermToODocConverter GO_TERM_TO_DOC_CONVERTER = new GOTermToODocConverter();
    private static final GenericTermToODocConverter GENERIC_TERM_TO_DOC_CONVERTER = new GenericTermToODocConverter();

    private final GeneOntology go;
    private final EvidenceCodeOntology eco;

    private Iterator<GenericTerm> goTermIterator;
    private Iterator<GenericTerm> ecoTermIterator;

    public static OntologyReader buildReader(File sourceFileDir) {
        GeneOntology geneOntology;

        try {
            geneOntology = new GOLoader(new GOSourceFiles(requireNonNull(sourceFileDir))).load();
        } catch (Exception e) {
            LOGGER.error("Failed to load GO ontology files", e);
            geneOntology = new EmptyGeneOntology();
        }

        EvidenceCodeOntology evidenceCodeOntology;
        try {
            evidenceCodeOntology = new ECOLoader(new ECOSourceFiles(requireNonNull(sourceFileDir))).load();
        } catch (Exception e) {
            LOGGER.error("Failed to load ECO ontology files", e);
            evidenceCodeOntology = new EmptyEvidenceCodeOntology();
        }

        return new OntologyReader(geneOntology, evidenceCodeOntology);
    }

    OntologyReader(GeneOntology go, EvidenceCodeOntology  eco) {
        checkArgument(Objects.nonNull(go), "The GeneOntology passed to the OntologyReader is " +
                "null");
        checkArgument(Objects.nonNull(eco), "The EvidenceCodeOntology passed to the OntologyReader is " +
                "null");
        this.go = go;
        this.eco = eco;
    }

    /**
     * Creates and returns {@link OntologyDocument} instances corresponding to
     * each known GO / ECO term.
     *
     * @return an {@link OntologyDocument} corresponding to each known GO / ECO term
     * @throws Exception exception indicating an error during reading
     */
    @Override public OntologyDocument read() throws Exception {
        if (goTermIterator.hasNext()) {
            return GO_TERM_TO_DOC_CONVERTER.apply((GOTerm) goTermIterator.next());
        } else if (ecoTermIterator.hasNext()) {
            return GENERIC_TERM_TO_DOC_CONVERTER.apply(ecoTermIterator.next());
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
     * @param executionContext the {@link ExecutionContext} used whilst performing an opening action
     */
    @Override public void open(ExecutionContext executionContext) {
        super.open(executionContext);
        final List<GenericTerm> terms = go.getTerms();
        if (terms.size() > 0) {
            LOGGER.info("Loaded Gene Ontology successfully");
        } else {
            LOGGER.info("Loading Gene Ontology unsuccessful");
        }
        this.goTermIterator = terms.iterator();

        final List<GenericTerm> ecoTerms = eco.getTerms();
        if (ecoTerms.size() > 0) {
            LOGGER.info("Loaded Evidence Code Ontology successfully");
        } else {
            LOGGER.info("Loading Evidence Code Ontology unsuccessful");
        }
        this.ecoTermIterator = ecoTerms.iterator();
    }

    static class EmptyGeneOntology extends GeneOntology {

        @Override
        public List<GenericTerm> getTerms() {
            return Collections.emptyList();
        }
    }

    static class EmptyEvidenceCodeOntology extends EvidenceCodeOntology {

        @Override
        public List<GenericTerm> getTerms() {
            return Collections.emptyList();
        }
    }
}


