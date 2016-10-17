package uk.ac.ebi.quickgo.ontology.common.coterms;

import com.google.common.base.Preconditions;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
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

    private Map<String, List<CoTerm>> coTermsAll;
    private Map<String, List<CoTerm>> coTermsManual;

    /**
     * Create a instance of CoTermRepositorySimpleMap without using the CoTermLoader inner class.
     * @param coTermsAll CoTerms derived from all sources.
     * @param coTermsManual CoTerms derived from non-electronic source.
     */
    public CoTermRepositorySimpleMap(Map<String, List<CoTerm>> coTermsAll, Map<String, List<CoTerm>> coTermsManual) {
        this.coTermsAll = coTermsAll;
        this.coTermsManual = coTermsManual;
    }

    /**
     * Create a instance of CoTermRepositorySimpleMap with the expectation of using the CoTermLoader inner class.
     */
    public CoTermRepositorySimpleMap() {

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
     * Get all co-occurring terms for the requested term up to the supplied limit. The data within the source is
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

    /**
     * Read the sources that hold the co-occurring term data, and load to memory.
     */
    class CoTermLoader {
        private final Logger logger = LoggerFactory.getLogger(CoTermLoader.class);
        private final Resource manualSource;
        private final Resource allSource;

        /**
         *
         * @param manualCoTermsSource source of co-occurring terms for Terms used in manually derived annotations.
         * @param allCoTermSource source of co-occurring terms for Terms used in annotations derived from all sources.
         */
        CoTermLoader(Resource manualCoTermsSource, Resource allCoTermSource) {
            Preconditions.checkArgument(manualCoTermsSource != null, "Resource manualCoTermsSource should not be null" +
                    ".");
            Preconditions.checkArgument(allCoTermSource != null, "Resource allCoTermSource should not be null.");
            this.manualSource = manualCoTermsSource;
            this.allSource = allCoTermSource;
        }

        /**
         * Read the sources, load data into memory.
         */
        public void load() {
            logger.info("Loading Co terms from sources");
            loadCoTermsSource(allSource, coTermsAll = new HashMap<>());
            loadCoTermsSource(manualSource, coTermsManual = new HashMap<>());
        }

        /**
         * Load source contents into memory
         * @param source of CoTerms.
         * @param coTerms target map.
         */
        private void loadCoTermsSource(Resource source, Map<String, List<CoTerm>> coTerms) {
            Preconditions.checkState(source.exists(), "The input source " + source.getDescription() + " for CoTerms " +
                    "does not exist.");
            List<CoTerm> comparedTerms = new ArrayList<>();
            String line;
            String currentTerm = null;
            long lineCount = 0;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(source.getInputStream()))) {

                while ((line = br.readLine()) != null) {
                    lineCount++;

                    //Ignore any line that doesn't start with a GO id.
                    if (!line.startsWith("GO")) {
                        continue;
                    }

                    CoTerm CoTerm = parseInputString(line);

                    //one time initialisation
                    if (currentTerm == null) {
                        currentTerm = CoTerm.getId();
                    }

                    //Have we arrived at a new source term?
                    if (!CoTerm.getId().equals(currentTerm)) {
                        coTerms.put(currentTerm, comparedTerms);

                        //Reset
                        currentTerm = CoTerm.getId();
                        comparedTerms = new ArrayList<>();
                    }

                    comparedTerms.add(CoTerm);
                }

                //save last term
                coTerms.put(currentTerm, comparedTerms);
                logger.info("Loaded " + lineCount + " lines from " + source.getDescription());
                logger.info("Number of GO Terms loaded is " + coTerms.keySet().size());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        /**
         * Specification how to map input string columns to CoTerm entity
         */

        private static final int COLUMN_ID = 0;
        private static final int COLUMN_COMPARE = 1;
        private static final int COLUMN_PROB = 2;
        private static final int COLUMN_SIG = 3;
        private static final int COLUMN_TOGETHER = 4;
        private static final int COLUMN_COMPARED = 5;

        private CoTerm parseInputString(String line) {
            String[] columns = line.split("\\t");
            return new CoTerm(columns[COLUMN_ID], columns[COLUMN_COMPARE],
                    Float.parseFloat(columns[COLUMN_PROB]), Float.parseFloat(columns[COLUMN_SIG]),
                    Long.parseLong(columns[COLUMN_TOGETHER]), Long.parseLong(columns[COLUMN_COMPARED]));
        }

    }

}
