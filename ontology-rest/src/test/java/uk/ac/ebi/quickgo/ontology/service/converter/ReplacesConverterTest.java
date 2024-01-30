package uk.ac.ebi.quickgo.ontology.service.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link ReplaceConverter} class.
 */
class ReplacesConverterTest {
    private ReplaceConverter converter;

    @BeforeEach
    void setUp() throws Exception {
        converter = new ReplaceConverter();
    }

    @Test
    void emptyReplaceStringIsConvertedToEmptyOptional() throws Exception {
        String toConvert = "";

        Optional<OBOTerm.Replace> convertedOptional = converter.apply(toConvert);

        assertThat(convertedOptional.isPresent(), is(false));
    }

    @Test
    void replaceStringWithJustIdIsConvertedIntoEmptyOptional() throws Exception {
        String toConvert = "id1";

        Optional<OBOTerm.Replace> convertedOptional = converter.apply(toConvert);

        assertThat(convertedOptional.isPresent(), is(false));
    }

    @Test
    void replaceStringWithIdAndTypeIsConvertedIntoPopulatedReplaceOptional() throws Exception {
        String goTermId = "id1";
        String relationType = "replacedBy";

        String toConvert = FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf(goTermId))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(relationType))
                .buildString();


        Optional<OBOTerm.Replace> convertedOptional = converter.apply(toConvert);

        OBOTerm.Replace expectedReplace = convertedOptional.get();

        assertThat(expectedReplace, is(notNullValue()));
        assertThat(expectedReplace.id, is(goTermId));
        assertThat(expectedReplace.type, is(relationType));
    }
}
