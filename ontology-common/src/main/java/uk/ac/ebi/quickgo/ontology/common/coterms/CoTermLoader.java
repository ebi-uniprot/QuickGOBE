package uk.ac.ebi.quickgo.ontology.common.coterms;

import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Read the files that hold the co-occurring term data, and load to memory.
 *
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 13:55
 * Created with IntelliJ IDEA.
 */
public class CoTermLoader {

    private static final Logger logger = LoggerFactory.getLogger(CoTermLoader.class);

    private final Resource manualCoTermsFile;
    private final Resource allCoTermsFile;

    public Map<String, List<CoTerm>> coTermsAll;
    public Map<String, List<CoTerm>> coTermsManual;

    /**
     *
     * @param manualCoTermsFile location and file name of co-occurring terms for Terms used in manually derived
     * annotations.
     * @param allCoTermsFile location and file name of co-occurring terms for Terms used in annotations derived
     * from all sources.
     */
    public CoTermLoader(Resource manualCoTermsFile, Resource allCoTermsFile) {
        Preconditions.checkArgument(manualCoTermsFile != null, "Resource manualCoTermsFile should not be null, but " +
                "was");
        Preconditions.checkArgument(allCoTermsFile != null, "Resource allCoTermsFile should not be null, but " +
                "was");
        this.manualCoTermsFile = manualCoTermsFile;
        this.allCoTermsFile = allCoTermsFile;
    }

    /**
     * Read the files, load data into memory.
     */
    public void load(){
        logger.info("Loading Co terms from file");

        try {
            coTermsAll = new HashMap<>();
            loadCoTermsFile(allCoTermsFile.getFile(), coTermsAll);

            coTermsManual = new HashMap<>();
            loadCoTermsFile(manualCoTermsFile.getFile(), coTermsManual);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Co-occurring terms from file", e);
        }
    }

    /**
     * Load file contents into memory
     * @param inputFile source file
     * @param coTerms target map
     * @return map with file contents
     */
    private void loadCoTermsFile(File inputFile, Map<String, List<CoTerm>> coTerms){
        List<CoTerm> comparedTerms = new ArrayList<>();
        String line;
        String currentTerm = null;
        long lineCount=0;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

            //Read file which is sorted by source id, then size of significance ratio.
            while ((line = br.readLine()) != null) {

                lineCount++;

                //Ignore any line that doesn't start with a GO id.
                if(!line.startsWith("GO")){
                    continue;
                }

                CoTerm CoTerm = fromFile(line);

                //one time initialisation
                if(currentTerm==null){
                    currentTerm = CoTerm.getId();
                }

                //Have we arrived at a new source term?
                if(!CoTerm.getId().equals(currentTerm)) {
                    coTerms.put(currentTerm, comparedTerms);

                    //Reset
                    currentTerm = CoTerm.getId();
                    comparedTerms = new ArrayList<>();
                }

                comparedTerms.add(CoTerm);

            }


            //save last term
            coTerms.put(currentTerm, comparedTerms);
            logger.info("Loaded " + lineCount + " lines from " + inputFile.getName());
            logger.info("Number of GO Terms loaded is " + coTerms.keySet().size());

        } catch (IOException e ) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Specification how to map file columns to CoTerm entity
     */

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_COMPARE = 1;
    private static final int COLUMN_PROB = 2;
    private static final int COLUMN_SIG = 3;
    private static final int COLUMN_TOGETHER = 4;
    private static final int COLUMN_COMPARED = 5;


    private static CoTerm fromFile(String line) {
        String[] columns = line.split("\\t");
        return new CoTerm(columns[COLUMN_ID], columns[COLUMN_COMPARE],
                Float.parseFloat(columns[COLUMN_PROB]), Float.parseFloat(columns[COLUMN_SIG]),
                Long.parseLong(columns[COLUMN_TOGETHER]), Long.parseLong(columns[COLUMN_COMPARED]));
    }


}


