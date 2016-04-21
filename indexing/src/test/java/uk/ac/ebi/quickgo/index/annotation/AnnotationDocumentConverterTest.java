package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import org.junit.Before;
import org.junit.Test;

/**
 * Created 21/04/16
 * @author Edd
 */
public class AnnotationDocumentConverterTest {
    private AnnotationDocumentConverter converter;
    private Annotation annotation;

    @Before
    public void setUp() {
        converter = new AnnotationDocumentConverter();

        annotation = new Annotation();
    }

    @Test(expected = DocumentReaderException.class)
    public void nullAnnotationThrowsException() throws Exception {
        converter.process(null);
    }


}