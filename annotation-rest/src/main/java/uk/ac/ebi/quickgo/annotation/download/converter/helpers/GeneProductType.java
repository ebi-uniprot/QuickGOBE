package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For a String containing a DB identifier, determine and return the gene product type.
 *
 * @author Tony Wardell
 * Date: 09/04/2018
 * Time: 14:52
 * Created with IntelliJ IDEA.
 */
public class GeneProductType {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneProductType.class);

    private GeneProductType() {}

    public static final Function<String, String> toGpType = db -> {
        String type;
        switch (db) {
            case "UniProtKB":
                type = "protein";
                break;
            case "IntAct":
                type = "complex";
                break;
            case "RNAcentral":
                type = "miRNA";
                break;
            default:
                LOGGER.error("Cannot determine gene product type for based on DB of {}", db);
                type = "";
                break;
        }
        return type;
    };
}
