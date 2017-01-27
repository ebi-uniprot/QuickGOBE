package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class contains methods that are shared between the AnnotationToX converters.
 * @author Tony Wardell
 * Date: 20/01/2017
 * Time: 17:01
 * Created with IntelliJ IDEA.
 */
abstract class AnnotationTo {
    private static final String ID_DELIMITER = ":";
    private static final String COMMA = ",";
    private static final String PIPE = "|";
    private static final DateFormat YYYYMMDD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    String withFromAsString(List<Annotation.ConnectedXRefs<Annotation.SimpleXRef>> connectedXRefs) {
        if (connectedXRefs == null || connectedXRefs.size() == 0) {
            return "";
        }
        return connectedXRefs.stream()
                             .map(this::simpleRefAndToString)
                             .collect(Collectors.joining(PIPE));
    }

    private String simpleRefAndToString(Annotation.ConnectedXRefs<Annotation.SimpleXRef> itemList) {
        return itemList.getConnectedXrefs()
                       .stream()
                       .map(Annotation.SimpleXRef::asXref)
                       .collect(Collectors.joining(COMMA));
    }

    String extensionsAsString(List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> connectedXRefs) {
        if (connectedXRefs == null || connectedXRefs.size() == 0) {
            return "";
        }
        return connectedXRefs.stream()
                             .map(this::qualifiedRefAndToString)
                             .collect(Collectors.joining(PIPE));
    }

    private String qualifiedRefAndToString(Annotation.ConnectedXRefs<Annotation.QualifiedXref> itemList) {
        return itemList.getConnectedXrefs()
                       .stream()
                       .map(Annotation.QualifiedXref::asXref)
                       .collect(Collectors.joining(COMMA));
    }

    public String[] idToComponents(Annotation annotation) {

        return annotation.id.split(ID_DELIMITER);
    }

    public String toYMD(Date date) {
        return YYYYMMDD_DATE_FORMAT.format(date);
    }


    protected String idOrSlimmedId(Annotation annotation) {
        return Objects.isNull(annotation.slimmedIds) || annotation.slimmedIds.isEmpty() ? annotation.goId : annotation
                .slimmedIds.get(0);
    }
}
