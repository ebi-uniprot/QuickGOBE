package uk.ac.ebi.quickgo.ontology.service.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created 01/12/15
 * @author Edd
 */
class AnnotationGuideLineFieldConverterTest {

    private AnnotationGuideLineFieldConverter converter;

    @BeforeEach
    void setup() {
        this.converter = new AnnotationGuideLineFieldConverter();
    }

    @Test
    void convertsAnnotationGuideLines() {
        List<String> rawAnnotationGuideLines = new ArrayList<>();
        String description0 = "description 0";
        rawAnnotationGuideLines.add(FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf(description0))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("http://www.guardian.co.uk"))
                .buildString()
        );
        String url1 = "http://www.pinkun.com";
        rawAnnotationGuideLines.add(FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("description 1"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(url1))
                .buildString()
        );

        List<OBOTerm.AnnotationGuideLine> annotationGuideLines = converter.convertFieldList(rawAnnotationGuideLines);
        assertThat(annotationGuideLines.size(), is(2));
        assertThat(annotationGuideLines.get(0).description, is(description0));
        assertThat(annotationGuideLines.get(1).url, is(url1));
    }

    @Test
    void gracefullyHandleWrongFieldCount() {
        Optional<OBOTerm.AnnotationGuideLine> result = converter.apply(
                FlatFieldBuilder.newFlatField().addField(FlatFieldLeaf.newFlatFieldLeaf("wrong " +
                "format"))
                .buildString());
        assertThat(result.isPresent(), is(false));
    }
}