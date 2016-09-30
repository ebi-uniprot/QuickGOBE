package uk.ac.ebi.quickgo.ontology.common.coterm;

import java.util.List;
import org.springframework.data.repository.Repository;

/**
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:39
 * Created with IntelliJ IDEA.
 */
public interface CoTermRepository extends Repository<List<CoTerm>, String> {

    List<CoTerm> findCoTerms(String id, CoTermType type, int limit, int thresholdLimit);
}
