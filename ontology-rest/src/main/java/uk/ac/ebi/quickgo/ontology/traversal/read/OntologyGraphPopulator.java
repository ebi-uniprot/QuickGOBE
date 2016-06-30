package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyRelationship;

import java.util.List;
import org.slf4j.Logger;
import org.springframework.batch.item.ItemWriter;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * The purpose of this class is to populate an ontology graph with the base information:
 * vertices and edges.
 *
 * Created 18/05/16
 * @author Edd
 */
public class OntologyGraphPopulator implements ItemWriter<OntologyRelationship> {
    private static final Logger LOGGER = getLogger(OntologyGraphPopulator.class);
    private final OntologyGraph ontologyGraph;

    public OntologyGraphPopulator(OntologyGraph ontologyGraph) {
        this.ontologyGraph = ontologyGraph;
    }

    @Override public void write(List<? extends OntologyRelationship> list) throws Exception {
        LOGGER.debug("Adding {} ontology graph tuples.", list.size());
        ontologyGraph.addRelationships(list);
    }
}
