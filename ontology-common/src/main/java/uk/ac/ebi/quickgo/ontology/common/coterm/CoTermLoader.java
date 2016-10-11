package uk.ac.ebi.quickgo.ontology.common.coterm;

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

    private Resource manualCoTermsFile;
    private Resource allCoTermsFile;

    public Map<String, List<CoTerm>> coTermsAll;
    public Map<String, List<CoTerm>> coTermsManual;

    public CoTermLoader(Resource manualCoTermsFile, Resource allCoTermsFile) {
        this.manualCoTermsFile = manualCoTermsFile;
        this.allCoTermsFile = allCoTermsFile;
    }

    public void load(){
        logger.info("Loading Co terms from file");

        try {
            coTermsAll = new HashMap<>();
            loadCoStatsFile(allCoTermsFile.getFile(), coTermsAll);

            coTermsManual = new HashMap<>();
            loadCoStatsFile(manualCoTermsFile.getFile(), coTermsManual);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Co-occurring terms from file", e);
        }
    }

    private Map<String, List<CoTerm>> loadCoStatsFile(File inputFile, Map<String, List<CoTerm>> stats){
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
                    stats.put(currentTerm, comparedTerms);

                    //Reset
                    currentTerm = CoTerm.getId();
                    comparedTerms = new ArrayList<>();
                }

                comparedTerms.add(CoTerm);

            }


            //save last term
            stats.put(currentTerm, comparedTerms);
            logger.info("Loaded " + lineCount + " lines from " + inputFile.getName());
            logger.info("Number of GO Terms loaded is " + stats.keySet().size());



        } catch (IOException e ) {
            throw new RuntimeException(e);
        }
        return stats;
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


    static CoTerm fromFile(String line) {
        String[] columns = line.split("\\t");
        CoTerm coTerm = new CoTerm(columns[COLUMN_ID], columns[COLUMN_COMPARE],
                Float.parseFloat(columns[COLUMN_PROB]), Float.parseFloat(columns[COLUMN_SIG]),
                Long.parseLong(columns[COLUMN_TOGETHER]), Long.parseLong(columns[COLUMN_COMPARED]));
        return coTerm;
    }


}


