package uk.ac.ebi.quickgo.ff.loader.ontology;

import uk.ac.ebi.quickgo.ff.files.ontology.ECOSourceFiles;
import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.go.AnnotationExtensionRelations;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import java.io.File;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class is responsible for loading the legacy ontologies, required currently
 * only for showing graph images via {@link GraphImageService}. Encapsulation
 * of the loading functionality makes it simpler to test at the level of any callers
 * of {@link GraphImageService}.
 *
 * Created 27/09/16
 * @author Edd
 */
public class OntologyGraphicsSourceLoader {
    private static final Logger LOGGER = getLogger(OntologyGraphicsSourceLoader.class);
    private final File sourceDir;
    private GeneOntology geneOntology;
    private EvidenceCodeOntology evidenceCodeOntology;
    private AnnotationExtensionRelations goAnnotationExtensionRelations;

    public OntologyGraphicsSourceLoader(File sourceDir) {
        checkArgument(sourceDir != null && sourceDir.exists(), "Source directory cannot be null and must exist");

        this.sourceDir = sourceDir;

        loadOntologies();
    }

    public GeneOntology getGeneOntology() {
        return geneOntology;
    }

    public EvidenceCodeOntology getEvidenceCodeOntology() {
        return evidenceCodeOntology;
    }

    public AnnotationExtensionRelations getGoAnnotationExtensionRelations(){
        return goAnnotationExtensionRelations;
    }

    public boolean isLoaded() {
        return geneOntology != null && evidenceCodeOntology != null;
    }

    private void loadOntologies() {
        try {
            GOSourceFiles goSourceFiles = new GOSourceFiles(requireNonNull(sourceDir));
            geneOntology = new GOLoader(goSourceFiles).load();
            goAnnotationExtensionRelations = new AnnotationExtensionRelsLoader(goSourceFiles, geneOntology).load();
        } catch (Exception e) {
            LOGGER.warn("Could not load Gene Ontology or its Annotation Extension Relations correctly. No graphics can be provided.");
        }

        try {
            evidenceCodeOntology = new ECOLoader(new ECOSourceFiles(requireNonNull(sourceDir))).load();
        } catch (Exception e) {
            LOGGER.warn("Could not load ECO Ontology correctly. No graphics can be provided.");
        }
    }
}
