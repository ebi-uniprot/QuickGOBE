package uk.ac.ebi.quickgo.ff.loader;

import uk.ac.ebi.quickgo.ff.files.ontology.OntologySourceFiles;

import java.util.Optional;

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

    public SourceInfoLoader(S sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    protected abstract T newInstance();
    public abstract Optional<T> load();
}
