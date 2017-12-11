package uk.ac.ebi.quickgo.ff.loader.ontology;

import uk.ac.ebi.quickgo.ff.files.ontology.ECOSourceFiles;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;

/**
 * Loads a complete ECO ontology from a suite of source files.
 *
 * Created by Edd on 11/12/2015.
 */
public class ECOLoader extends AbstractGenericOLoader<ECOSourceFiles, EvidenceCodeOntology> {

    public ECOLoader(ECOSourceFiles sourceFiles) {
        super(sourceFiles);
    }

    @Override
    public EvidenceCodeOntology load() throws Exception {
        EvidenceCodeOntology eco = getInstance();

        // add generic ontology info
        createWithGenericOInfo(EvidenceCodeOntology.NAME_SPACE, null);
        return eco;
    }

    @Override
    protected EvidenceCodeOntology newInstance() {
        return new EvidenceCodeOntology();
    }
}
