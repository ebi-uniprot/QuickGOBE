package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods that are shared between the AnnotationToX converters.
 * @author Tony Wardell
 * Date: 20/01/2017
 * Time: 17:01
 * Created with IntelliJ IDEA.
 */
abstract class AnnotationTo {
    public static final int DB = 0;
    static final String OUTPUT_DELIMITER = "\t";
    static final Function<String, String> nullToEmptyString = s -> s == null ? "" : s;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationTo.class);
    private static final String ID_DELIMITER = ":";
    private static final int LOWEST_VALID_TAXON_ID = 1;
    private static final String COMMA = ",";
    private static final String PIPE = "|";
    private static final DateTimeFormatter YYYYMMDD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    String toGeneProductType(String db) {
        switch (db) {
            case "UniProtKB":
                return "protein";
            case "IntAct":
                return "complex";
            case "RNAcentral":
                return "miRNA";
            default:
                LOGGER.error("Cannot determine gene product type for based on DB of " + db);
        }
        return "";
    }

    String withFromAsString(List<Annotation.ConnectedXRefs<Annotation.SimpleXRef>> connectedXRefs) {
        if (connectedXRefs == null || connectedXRefs.isEmpty()) {
            return "";
        }
        return connectedXRefs.stream()
                .map(this::simpleRefAndToString)
                .collect(Collectors.joining(PIPE));
    }

    String extensionsAsString(List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> connectedXRefs) {
        if (connectedXRefs == null || connectedXRefs.isEmpty()) {
            return "";
        }
        return connectedXRefs.stream()
                .map(this::qualifiedRefAndToString)
                .collect(Collectors.joining(PIPE));
    }

    String[] idToComponents(String id) {
        return id == null ? new String[]{"", ""} : id.split(ID_DELIMITER);
    }

    String toYMD(Date date) {
        return date == null ?
                "" : YYYYMMDD_DATE_FORMAT.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    String taxonIdAsString(int taxonId) {
        return taxonId < LOWEST_VALID_TAXON_ID ? "" : Integer.toString(taxonId);
    }

    private String simpleRefAndToString(Annotation.ConnectedXRefs<Annotation.SimpleXRef> itemList) {
        return itemList.getConnectedXrefs()
                .stream()
                .map(Annotation.SimpleXRef::asXref)
                .collect(Collectors.joining(COMMA));
    }

    private String qualifiedRefAndToString(Annotation.ConnectedXRefs<Annotation.QualifiedXref> itemList) {
        return itemList.getConnectedXrefs()
                .stream()
                .map(Annotation.QualifiedXref::asXref)
                .collect(Collectors.joining(COMMA));
    }
}
