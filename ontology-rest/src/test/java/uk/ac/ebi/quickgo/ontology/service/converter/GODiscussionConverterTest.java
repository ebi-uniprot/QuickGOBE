package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link GODiscussionConverter} class.
 */
public class GODiscussionConverterTest {
    private GODiscussionConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new GODiscussionConverter();
    }

    @Test
    public void convertsValidTextBasedGoDiscussion() throws Exception {
        String title = "Viral Processes";
        String url = "http://wiki.geneontology.org/index.php/Virus_terms";

        String goDiscussionText = createGoDiscussionText(title, url);

        Optional<GOTerm.GODiscussion> expectedGoDiscussionOpt = converter.apply(goDiscussionText);

        assertThat(expectedGoDiscussionOpt.isPresent(), is(true));

        GOTerm.GODiscussion expectedGODiscussion = expectedGoDiscussionOpt.get();

        assertThat(expectedGODiscussion.title, is(title));
        assertThat(expectedGODiscussion.url, is(url));
    }

    @Test
    public void returnsEmptyOptionalWhenTextBasedGoDiscussionHasWrongNumberOfFields() throws Exception {
        String wrongTextFormatGoDiscussion = "Wrong format";

        Optional<GOTerm.GODiscussion> expectedGoDiscussionOpt = converter.apply(wrongTextFormatGoDiscussion);

        assertThat(expectedGoDiscussionOpt.isPresent(), is(false));
    }

    private String createGoDiscussionText(String title, String url) {
        return FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf(title))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(url))
                .buildString();
    }
}