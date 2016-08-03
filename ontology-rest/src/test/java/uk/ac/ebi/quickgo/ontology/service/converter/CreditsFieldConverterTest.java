package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests the behaviour of the {@link CreditsFieldConverter} class.
 */
public class CreditsFieldConverterTest {
    private CreditsFieldConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new CreditsFieldConverter();
    }

    @Test
    public void convertsValidTextBasedCredit() throws Exception {
        String code = "BHF";
        String url = "http://www.ucl.ac.uk/cardiovasculargeneontology/";

        String creditText = createCreditText(code, url);

        Optional<OBOTerm.Credit> expectedCreditOpt = converter.apply(creditText);

        assertThat(expectedCreditOpt.isPresent(), is(true));

        OBOTerm.Credit expectedCredit = expectedCreditOpt.get();

        assertThat(expectedCredit.code, is(code));
        assertThat(expectedCredit.url, is(url));
    }

    @Test
    public void returnsEmptyOptionalWhenTextBasedCreditHasWrongNumberOfFields() throws Exception {
        String wrongTextFormatCredit = "Wrong format";

        Optional<OBOTerm.Credit> expectedCreditOpt = converter.apply(wrongTextFormatCredit);

        assertThat(expectedCreditOpt.isPresent(), is(false));
    }

    private String createCreditText(String code, String url) {
        return FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf(code))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(url))
                .buildString();
    }
}