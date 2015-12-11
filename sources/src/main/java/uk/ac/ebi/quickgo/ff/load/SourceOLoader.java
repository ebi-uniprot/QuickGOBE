package uk.ac.ebi.quickgo.ff.load;

import uk.ac.ebi.quickgo.model.ontology.generic.ITermContainer;

/**
 * Created 10/12/15
 * @author Edd
 */
public interface SourceOLoader<T extends ITermContainer> {

    T load() throws Exception;
}
