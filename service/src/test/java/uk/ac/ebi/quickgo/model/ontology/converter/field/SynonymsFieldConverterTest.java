package uk.ac.ebi.quickgo.model.ontology.converter.field;

import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.ff.delim.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 01/12/15
 * @author Edd
 */
public class SynonymsFieldConverterTest {

    private SynonymsFieldConverter converter;

    @Before
    public void setup() {
        this.converter = new SynonymsFieldConverter();
    }

    /**
     * Check the flatted document synonyms are converted correctly to a
     * {@link uk.ac.ebi.quickgo.model.ontology.OBOTerm.Synonym}
     * DTO.
     */
    @Test
    public void convertingBadlyFlattenedSynonymsFailsWithoutError() {
        List<String> rawSynonyms = Arrays.asList("syn name 0-syn type 0", "syn name 1-syn type 1");
        List<OBOTerm.Synonym> synonyms = converter.convertField(rawSynonyms);
        assertThat(synonyms.size(), is(0));
    }

    /**
     * Check the flatted document synonyms are converted correctly to a
     * {@link uk.ac.ebi.quickgo.model.ontology.OBOTerm.Synonym}
     * DTO.
     */
    @Test
    public void converts0FlattenedSynonymsToSynonymsDTO() {
        List<String> rawSynonyms = Collections.emptyList();
        List<OBOTerm.Synonym> synonyms = converter.convertField(rawSynonyms);
        assertThat(synonyms.size(), is(0));
    }

    /**
     * Check the flatted document synonyms are converted correctly to a
     * {@link uk.ac.ebi.quickgo.model.ontology.OBOTerm.Synonym}
     * DTO.
     */
    @Test
    public void converts1FlattenedSynonymToSynonymsDTO() {
        List<String> rawSynonyms = Collections.singletonList(newFlatField()
                .addField(newFlatFieldLeaf("syn name 0"))
                .addField(newFlatFieldLeaf("syn type 0"))
                .buildString());
        List<OBOTerm.Synonym> synonyms = converter.convertField(rawSynonyms);
        assertThat(synonyms.size(), is(1));
        assertThat(synonyms.get(0).synonymName, is(equalTo("syn name 0")));
        assertThat(synonyms.get(0).synonymType, is(equalTo("syn type 0")));
    }

    /**
     * Check the flatted document synonyms are converted correctly to a
     * {@link uk.ac.ebi.quickgo.model.ontology.OBOTerm.Synonym}
     * DTO.
     */
    @Test
    public void converts2FlattenedSynonymsToSynonymsDTO() {
        List<String> rawSynonyms = Arrays.asList(
                newFlatField()
                        .addField(newFlatFieldLeaf("syn name 0"))
                        .addField(newFlatFieldLeaf("syn type 0"))
                        .buildString(),
                newFlatField()
                        .addField(newFlatFieldLeaf("syn name 1"))
                        .addField(newFlatFieldLeaf("syn type 1"))
                        .buildString()
        );
        List<OBOTerm.Synonym> synonyms = converter.convertField(rawSynonyms);
        assertThat(synonyms.size(), is(2));
        assertThat(synonyms.get(0).synonymName, is(equalTo("syn name 0")));
        assertThat(synonyms.get(0).synonymType, is(equalTo("syn type 0")));

        assertThat(synonyms.get(1).synonymName, is(equalTo("syn name 1")));
        assertThat(synonyms.get(1).synonymType, is(equalTo("syn type 1")));
    }
}