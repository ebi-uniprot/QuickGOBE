package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 01/12/15
 * @author Edd
 */
public class AnnotationGuideLineFieldConverterTest {

    private AnnotationGuideLineFieldConverter converter;

    @Before
    public void setup() {
        this.converter = new AnnotationGuideLineFieldConverter();
    }

    @Test
    public void convertsAnnotationGuideLines() {
        List<String> rawAnnotationGuideLines = new ArrayList<>();
        String description0 = "description 0";
        rawAnnotationGuideLines.add(newFlatField()
                .addField(newFlatFieldLeaf(description0))
                .addField(newFlatFieldLeaf("http://www.guardian.co.uk"))
                .buildString()
        );
        String url1 = "http://www.pinkun.com";
        rawAnnotationGuideLines.add(newFlatField()
                .addField(newFlatFieldLeaf("description 1"))
                .addField(newFlatFieldLeaf(url1))
                .buildString()
        );

        List<OBOTerm.AnnotationGuideLine> annotationGuideLines = converter.convertFieldList(rawAnnotationGuideLines);
        assertThat(annotationGuideLines.size(), is(2));
        assertThat(annotationGuideLines.get(0).description, is(description0));
        assertThat(annotationGuideLines.get(1).url, is(url1));
    }

    @Test
    public void gracefullyHandleWrongFieldCount() {
        Optional<OBOTerm.AnnotationGuideLine> result = converter.apply(newFlatField().addField(newFlatFieldLeaf("wrong " +
                "format"))
                .buildString());
        assertThat(result.isPresent(), is(false));
    }
}