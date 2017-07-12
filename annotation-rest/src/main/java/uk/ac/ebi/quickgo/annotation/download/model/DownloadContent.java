package uk.ac.ebi.quickgo.annotation.download.model;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.List;
import java.util.stream.Stream;

/**
 * Data structure for content to download.
 * 
 * @author Tony Wardell
 * Date: 24/05/2017
 * Time: 09:00
 * Created with IntelliJ IDEA.
 */
public class DownloadContent {
    public final Stream<QueryResult<Annotation>> annotationStream;
    public final List<String> selectedFields;

    public DownloadContent(Stream<QueryResult<Annotation>> annotationResultStream, List<String> selectedFields) {
        this.annotationStream = annotationResultStream;
        this.selectedFields = selectedFields;
    }
}
