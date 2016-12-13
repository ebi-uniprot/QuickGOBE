package uk.ac.ebi.quickgo.ontology.common.coterms;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.groupingBy;

/**
 * Retrieve the co-occurring terms for the selected term from the in-memory map.
 *
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 13:48
 * Created with IntelliJ IDEA.
 */

public class CoTermRepositorySimpleMap implements CoTermRepository {

    private Map<String, List<CoTerm>> coTermsAll;
    private Map<String, List<CoTerm>> coTermsManual;

    private CoTermRepositorySimpleMap() {}

    /**
     * Create a instance of CoTermRepositorySimpleMap using maps of CoTerms from all sources and manual sources
     * respectively.
     * @param coTermsAll CoTerms derived from all sources.
     * @param coTermsManual CoTerms derived from non-electronic source.
     */
    public static CoTermRepositorySimpleMap createCoTermRepositorySimpleMap(Map<String, List<CoTerm>> coTermsAll,
            Map<String, List<CoTerm>> coTermsManual) {

        Preconditions.checkArgument(coTermsAll != null, "Map coTermsAll is null.");
        Preconditions.checkArgument(coTermsManual != null, "Map coTermsManual is null.");

        CoTermRepositorySimpleMap coTermRepository = new CoTermRepositorySimpleMap();
        coTermRepository.coTermsAll = coTermsAll;
        coTermRepository.coTermsManual = coTermsManual;
        return coTermRepository;
    }

    /**
     * Create a instance of CoTermRepositorySimpleMap loading the co-occurring data from the resources.
     * @param manualCoTermsSource source of co-occurring terms for Terms used in manually derived annotations.
     * @param allCoTermSource source of co-occurring terms for Terms used in annotations derived from all sources.
     * @throws IOException if the source of the co-occurring terms exists, but fails to be read.
     */
    public static CoTermRepositorySimpleMap createCoTermRepositorySimpleMap(Resource manualCoTermsSource, Resource
            allCoTermSource) throws IOException {

        Preconditions.checkArgument(manualCoTermsSource != null, "Resource manualCoTermsSource is null.");
        Preconditions.checkArgument(allCoTermSource != null, "Resource allCoTermSource is null.");
        Preconditions.checkState(manualCoTermsSource.exists(), "Resource manualCoTermsSource does not exist.");
        Preconditions.checkState(allCoTermSource.exists(), "Resource allCoTermSource does not exist.");

        CoTermRepositorySimpleMap coTermRepository = new CoTermRepositorySimpleMap();
        CoTermRepositorySimpleMap.CoTermLoader coTermLoader =
                coTermRepository.new CoTermLoader(manualCoTermsSource, allCoTermSource);
        coTermLoader.load();
        return coTermRepository;
    }

    /**
     * Get all co-occurring terms for the requested term up to the supplied limit
     * @param id the GO Term for which we will lookup co-occurring terms.
     * @return a list of CoTerms, each one of which represent a GO Term that is used to annotate the same gene
     * product as the id. Each CoTerm holds statistics related to that co-occurrence.
     * @throws IllegalArgumentException if the id is null
     * @throws IllegalStateException if the requested CoTermSource is null
     */
    public List<CoTerm> findCoTerms(String id, CoTermSource source) {
        Preconditions.checkArgument(id != null, "The findCoTerms id is null.");
        Preconditions.checkArgument(source != null, "The findCoTerms source is null.");
        return source == CoTermSource.MANUAL ? findCoTermsFromMap(coTermsManual, id)
                : findCoTermsFromMap(coTermsAll, id);
    }

    /**
     * Get all co-occurring terms for the requested term up to the supplied limit. The data within the source is
     * ordered by GOTerm and then probability score. Apply the predicate passed to this class for filtering the results.
     * @param id the GO Term for which we will lookup co-occurring terms.
     * @return a list of CoTerms, each one of which represent a GO Term that is used to annotate the same gene
     * product as the id. Each CoTerm holds statistics related to that co-occurrence.
     * @throws IllegalStateException if the target map is empty.
     */
    private List<CoTerm> findCoTermsFromMap(Map<String, List<CoTerm>> map, String id) {
        Preconditions.checkState(map.size()>0, "The coterms map is empty!");
        List<CoTerm> results = map.get(id);
        if (results == null) {
            return Collections.emptyList();
        }
        return results;
    }

    static class CoTermRecordParser {
        private static final int COLUMN_ID = 0;
        private static final int COLUMN_COMPARE = 1;
        private static final int COLUMN_PROB = 2;
        private static final int COLUMN_SIG = 3;
        private static final int COLUMN_TOGETHER = 4;
        private static final int COLUMN_COMPARED = 5;

        static CoTerm createFromText(String line) {
            String[] columns = line.split("\\t");
            return new CoTerm(columns[COLUMN_ID], columns[COLUMN_COMPARE],
                    Float.parseFloat(columns[COLUMN_PROB]), Float.parseFloat(columns[COLUMN_SIG]),
                    Long.parseLong(columns[COLUMN_TOGETHER]), Long.parseLong(columns[COLUMN_COMPARED]));
        }
    }

    /**
     * Read the sources that hold the co-occurring term data, and load to memory.
     */
    private class CoTermLoader {
        /**
         * Specification how to map input string columns to CoTerm entity
         */

        private final Logger logger = LoggerFactory.getLogger(CoTermLoader.class);
        private final Resource manualSource;
        private final Resource allSource;

        /**
         *
         * @param manualCoTermsSource source of co-occurring terms for Terms used in manually derived annotations.
         * @param allCoTermSource source of co-occurring terms for Terms used in annotations derived from all sources.
         */
        private CoTermLoader(Resource manualCoTermsSource, Resource allCoTermSource) {
            this.manualSource = manualCoTermsSource;
            this.allSource = allCoTermSource;
        }

        /**
         * Read the sources, load data into memory.
         * @throws IOException if the source of the co-occurring terms exists, but fails to be read.
         */
        private void load() throws IOException {
            logger.info("Loading Co terms from sources");
            coTermsAll = loadCoTermsSource(allSource);
            coTermsManual = loadCoTermsSource(manualSource);
        }

        /**
         * Load source contents into memory
         * @param source of CoTerms.
         * @throws IOException if the source of the co-occurring terms exists, but fails to be read.
         */
        private Map<String, List<CoTerm>> loadCoTermsSource(Resource source) throws IOException {

            try (Stream<String> lines = Files.lines(Paths.get(source.getURI()))) {
                return lines
                        .skip(1)    //header
                        .map(CoTermRecordParser::createFromText)
                        .collect(groupingBy(CoTerm::getTarget));

            } catch (IOException e) {
                throw e;
            }

        }
    }
}


