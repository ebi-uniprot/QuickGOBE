package uk.ac.ebi.quickgo.solr.mapper.term.eco;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.solr.mapper.term.EntityTermMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;

/**
 * For creating ECO Terms from Solr ones
 */
@Service("entityECOTermMapper")
public class EntityECOTermMapper extends EntityTermMapper<ECOTerm> {
    @Override public void mapSpecificFields(ECOTerm term, Collection<SolrTerm> solrObjects,
            List<SolrDocumentType> solrDocumentTypes) {
        //No specific fields to map
    }

    @Override protected ECOTerm createEmptyTerm() {
        return new ECOTerm();
    }
}
