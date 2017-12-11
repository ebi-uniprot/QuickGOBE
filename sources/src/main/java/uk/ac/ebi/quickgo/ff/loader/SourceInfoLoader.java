package uk.ac.ebi.quickgo.ff.loader;

import uk.ac.ebi.quickgo.ff.files.ontology.OntologySourceFiles;

/**
 * Class whose responsibility is to retrieve information from
 * ontology source files and construct a new instance of type
 * {@code T}.
 *
 * Created 10/12/15
 * @author Edd
 */
public abstract class SourceInfoLoader<S extends OntologySourceFiles, T> {
    protected final S sourceFiles;
    private T instance;

    public SourceInfoLoader(S sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public abstract T load() throws Exception;

    protected abstract T newInstance();

    protected T getInstance() {
        if (this.instance == null) {
            return this.instance = newInstance();
        } else {
            return this.instance;
        }
    }
}
