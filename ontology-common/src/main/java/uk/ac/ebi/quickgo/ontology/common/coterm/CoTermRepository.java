package uk.ac.ebi.quickgo.ontology.common.coterm;

import java.util.List;
import org.springframework.data.repository.Repository;

/**
 * Represents the store of co-occurring GO Term information.
 *
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:39
 * Created with IntelliJ IDEA.
 */
public interface CoTermRepository extends Repository<List<CoTerm>, String> {

    /**
     * For a single GO Term, retrieve a list of co-occurring terms and related statistics, in order of the
     * co-occurring terms similarity probablity (descending).
     * @param id is the target GO term, for which the method will retrieve co-occurring terms (GO terms that are used
     * to annotation the same gene products as this GO Term is used to annotate).
     * @param source is the method from which the annotation that uses the GO term was generated.
     * @param limit Limit the number of co-occurring terms return to the limit specified.
     * @param similarityThreshold if specified (greater than zero), only return co-occurring GO terms with a
     * similarity ratio above this figure.
     * @return a list of objects, each one of which represent a GO Term that is used to annotate the same gene
     * product as the id. Each object holds statistics related to that co-occurrence.
     */
    List<CoTerm> findCoTerms(String id, CoTermSource source, int limit, int similarityThreshold);
}
