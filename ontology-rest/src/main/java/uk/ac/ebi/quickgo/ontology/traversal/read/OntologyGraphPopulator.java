package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;

import java.util.List;
import org.slf4j.Logger;
import org.springframework.batch.item.ItemWriter;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created 18/05/16
 * @author Edd
 */
public class OntologyGraphPopulator implements ItemWriter<OntologyRelationshipTuple> {
    private static final Logger LOGGER = getLogger(OntologyGraphPopulator.class);
    private final OntologyGraph ontologyGraph;

    public OntologyGraphPopulator(OntologyGraph ontologyGraph) {
        this.ontologyGraph = ontologyGraph;
    }

    @Override public void write(List<? extends OntologyRelationshipTuple> list) throws Exception {
        LOGGER.info("Adding {} ontology graph tuples.", list.size());
        ontologyGraph.getTuples().addAll(list);
    }
}
