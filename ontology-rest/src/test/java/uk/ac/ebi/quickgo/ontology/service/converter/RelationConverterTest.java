package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link RelationConverter} class.
 */
public class RelationConverterTest {
    private RelationConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new RelationConverter();
    }

    @Test
    public void emptyRelationStringIsConvertedToEmptyOptional() throws Exception {
        String toConvert = "";

        Optional<OBOTerm.Relation> convertedOptional = converter.apply(toConvert);

        assertThat(convertedOptional.isPresent(), is(false));
    }

    @Test
    public void relationStringWithJustIdIsConvertedIntoEmptyOptional() throws Exception {
        String toConvert = "id1";

        Optional<OBOTerm.Relation> convertedOptional = converter.apply(toConvert);

        assertThat(convertedOptional.isPresent(), is(false));
    }

    @Test
    public void relationStringWithIdAndTypeIsConvertedIntoPopulatedRelationOptional() throws Exception {
        String goTermId = "id1";
        String relationType = "relationType";

        String toConvert = FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf(goTermId))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(relationType))
                .buildString();


        Optional<OBOTerm.Relation> convertedOptional = converter.apply(toConvert);

        OBOTerm.Relation expectedRelation = convertedOptional.get();

        assertThat(expectedRelation, is(notNullValue()));
        assertThat(expectedRelation.id, is(goTermId));
        assertThat(expectedRelation.type, is(relationType));
    }
}
