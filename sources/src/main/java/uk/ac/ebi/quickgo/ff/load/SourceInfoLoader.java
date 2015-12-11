package uk.ac.ebi.quickgo.ff.load;

import uk.ac.ebi.quickgo.ff.ontology.OntologySourceFiles;
import uk.ac.ebi.quickgo.ff.ontology.SourceFiles;
import uk.ac.ebi.quickgo.model.ontology.generic.ITermContainer;

/**
 * Created 10/12/15
 * @author Edd
 */
public abstract class SourceInfoLoader<S extends OntologySourceFiles, T> {
    protected final S sourceFiles;

    public SourceInfoLoader(S sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    protected abstract T newInstance();
    protected abstract T load() throws Exception;
}
