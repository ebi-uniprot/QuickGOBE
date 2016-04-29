package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 17:39
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationDocConverterImplTest {

    public static final String UNI_PROT = "UniProt";
    @Mock
    private AnnotationDocument annotationDocument;

    @Before
    public void setup(){
        when(annotationDocument.assignedBy).thenReturn(UNI_PROT);
    }

    @Test
    @Ignore //Doesn't use methods.
    public void convertSuccessfully(){
        AnnotationDocConverter docConverter = new AnnotationDocConverterImpl();
        Annotation model = docConverter.convert(annotationDocument);
        assertThat(model.assignedBy, equalTo(UNI_PROT));
    }
}
