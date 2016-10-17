package uk.ac.ebi.quickgo.ontology.common.coterms;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents the store of co-occurring GO Term information.
 *
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:39
 * Created with IntelliJ IDEA.
 */
public interface CoTermRepository {

    /**
     * For a single GO Term, retrieve a list of co-occurring terms and related statistics, each one of which represent
     * a GO Term that is used to annotate the same gene.
     * @param id is the target GO term, for which the method will retrieve co-occurring terms.
     * @param source is the method from which the annotation that uses the GO term was generated.
     * @param limit Limit the number of co-occurring terms returned to this number, as a maximum.
     * @param filter apply the predicate to filter the results.
     * @return a list of CoTerms.
     */
    List<CoTerm> findCoTerms(String id, CoTermSource source, int limit, Predicate<CoTerm> filter);
}
