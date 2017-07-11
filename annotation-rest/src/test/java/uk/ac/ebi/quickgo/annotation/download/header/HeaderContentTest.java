package uk.ac.ebi.quickgo.annotation.download.header;

import org.junit.Test;

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

    @Test
    public void creationSuccessful(){
        HeaderContent.Builder builder = new HeaderContent.Builder();
        builder.setUri(URL);
        builder.setDate(DATE);
        builder.setIsSlimmed(true);
        HeaderContent headerContent = builder.build();
        assertThat(headerContent.getUri(), is(URL));
        assertThat(headerContent.getDate(), is(DATE));
        assertThat(headerContent.isSlimmed(), is(true));
    }


}
