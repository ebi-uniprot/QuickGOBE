package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains methods that are shared between the AnnotationToX converters.
 * @author Tony Wardell
 * Date: 20/01/2017
 * Time: 17:01
 * Created with IntelliJ IDEA.
 */
public class ConversionUtil {
    private static final String ID_DELIMITER = ":";
    private static final String COMMA = ",";
    private static final String PIPE = "|";
    private static final DateFormat YYYYMMDD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public String withFromAsString(List<Annotation.ConnectedXRefs> connectedXRefs) {
        return connectedXRefs.stream()
                             .map(itemList -> simpleRefAndToString(itemList))
                             .collect(Collectors.joining(PIPE));
    }

    private String simpleRefAndToString(Annotation.ConnectedXRefs itemList) {
        return itemList.getConnectedXrefs()
                       .stream()
                       .map(cr -> {
                           Annotation.SimpleXRef sr = ((Annotation.SimpleXRef) cr);
                           return sr.asXref();
                       })
                       .collect(Collectors.joining(COMMA)).toString();

    }

    public String extensionsAsString(List<Annotation.ConnectedXRefs> connectedXRefs) {
        return connectedXRefs.stream()
                             .map(itemList -> qualifiedRefAndToString(itemList))
                             .collect(Collectors.joining(PIPE));
    }

    private String qualifiedRefAndToString(Annotation.ConnectedXRefs itemList) {
        return itemList.getConnectedXrefs()
                       .stream()
                       .map(cr -> {
                           Annotation.QualifiedXref sr = ((Annotation.QualifiedXref) cr);
                           return sr.asXref();
                       })
                       .collect(Collectors.joining(COMMA)).toString();
    }

    public String[] idToComponents(Annotation annotation) {

        return annotation.id.split(ID_DELIMITER);
    }

    public String toYMD(Date date) {
        return YYYYMMDD_DATE_FORMAT.format(date);
    }
}
