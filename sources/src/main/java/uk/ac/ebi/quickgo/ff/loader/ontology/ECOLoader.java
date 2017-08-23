package uk.ac.ebi.quickgo.ff.loader.ontology;

import uk.ac.ebi.quickgo.ff.files.ontology.ECOSourceFiles;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads a complete ECO ontology from a suite of source files.
 *
 * Created by Edd on 11/12/2015.
 */
public class ECOLoader extends AbstractGenericOLoader<ECOSourceFiles, EvidenceCodeOntology> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ECOLoader.class);

    public ECOLoader(ECOSourceFiles sourceFiles) {
        super(sourceFiles);
    }

    @Override
    protected EvidenceCodeOntology newInstance() {
        return new EvidenceCodeOntology();
    }

    @Override
    public EvidenceCodeOntology load() throws Exception{
            EvidenceCodeOntology eco = getInstance();

            // add generic ontology info
            createWithGenericOInfo(EvidenceCodeOntology.NAME_SPACE, Optional.empty());
            return eco;
    }
}
