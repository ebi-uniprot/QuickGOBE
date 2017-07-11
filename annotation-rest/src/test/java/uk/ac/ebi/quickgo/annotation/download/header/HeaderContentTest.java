package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.List;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 13:52
 * Created with IntelliJ IDEA.
 */
public class HeaderContentTest {

    private static final String URL = "test-url";
    private static final String DATE = "2017-05-23";
    private static final List<String> SELECTED_FIELDS = asList("geneProductId", "symbol");

    @Test
    public void creationSuccessful() {
        HeaderContent.Builder builder = new HeaderContent.Builder();
        builder.setUri(URL);
        builder.setDate(DATE);
        builder.setIsSlimmed(true);
        builder.setSelectedFields(SELECTED_FIELDS);
        HeaderContent headerContent = builder.build();
        assertThat(headerContent.getUri(), is(URL));
        assertThat(headerContent.getDate(), is(DATE));
        assertThat(headerContent.isSlimmed(), is(true));
        assertThat(headerContent.getSelectedFields(), is(SELECTED_FIELDS));
    }
}