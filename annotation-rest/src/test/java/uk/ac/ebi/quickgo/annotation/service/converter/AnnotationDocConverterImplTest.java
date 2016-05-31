package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 17:39
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationDocConverterImplTest {

    @Test
    public void convertAssignedBySuccessfully(){
        AnnotationDocConverter docConverter = new AnnotationDocConverterImpl();
        Annotation model = docConverter.convert(  AnnotationDocMocker.createAnnotationDoc("A0A000"));
        assertThat(model.assignedBy, equalTo("InterPro"));
    }
}
