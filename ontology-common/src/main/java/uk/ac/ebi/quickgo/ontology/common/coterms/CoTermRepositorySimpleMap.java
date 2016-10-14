package uk.ac.ebi.quickgo.ontology.common.coterms;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Retrieve the co-occurring terms for the selected term from the in-memory map.
 *
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 13:48
 * Created with IntelliJ IDEA.
 */
@Component
public class CoTermRepositorySimpleMap implements CoTermRepository {

    private final Map<String, List<CoTerm>> coTermsAll;
    private final Map<String, List<CoTerm>> coTermsManual;

    public CoTermRepositorySimpleMap(Map<String, List<CoTerm>> coTermsAll, Map<String, List<CoTerm>> coTermsManual) {
        this.coTermsAll = coTermsAll;
        this.coTermsManual = coTermsManual;
    }

    /**
     * Get all co-occurring terms for the requested term up to the supplied limit
     * @param id the GO Term for which we will lookup co-occurring terms.
     * @param limit Limit the number of co-occurring terms return to the limit specified.
     * @param filter apply the predicate to filter the results.
     * @return a list of objects, each one of which represent a GO Term that is used to annotate the same gene
     * product as the id. Each object holds statistics related to that co-occurrence.
     */
    public List<CoTerm> findCoTerms(String id, CoTermSource source, int limit, Predicate<CoTerm> filter) {

        Preconditions.checkArgument(id != null, "The findCoTerms id should not be null, but is");
        Preconditions.checkArgument(source != null, "The findCoTerms source should not be null, but is");
        Preconditions.checkArgument(filter != null, "The findCoTerms filter should not be null, but is");
        return source == CoTermSource.MANUAL ? findCoTermsFromMap(coTermsManual, id, limit, filter)
                : findCoTermsFromMap(coTermsAll, id, limit, filter);
    }

    /**
     * Get all co-occurring terms for the requested term up to the supplied limit. The data within the file is
     * ordered by GOTerm and then probability score. Apply the predicate passed to this class for filtering the results.
     * @param id the GO Term for which we will lookup co-occurring terms.
     * @param limit Limit the number of co-occurring terms return to the limit specified.
     * @param filter apply the predicate to filter the results.
     * @return a list of objects, each one of which represent a GO Term that is used to annotate the same gene
     * product as the id. Each object holds statistics related to that co-occurrence.
     */
    private List<CoTerm> findCoTermsFromMap(Map<String, List<CoTerm>> map, String id, int limit, Predicate<CoTerm>
            filter) {
        List<CoTerm> results = map.get(id);
        if (results == null) {
            return Collections.emptyList();
        }

        //If we have been passed a filtering predicate, use it. Could be extended to be a list of filters.
        return results.stream()
                .filter(filter)
                .limit(limit)
                .collect(Collectors.toList());
    }

}
