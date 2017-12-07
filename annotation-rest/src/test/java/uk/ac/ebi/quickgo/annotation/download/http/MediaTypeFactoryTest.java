package uk.ac.ebi.quickgo.annotation.download.http;

import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.EXCEL_MEDIA_TYPE;

/**
 * Test the functionality in MediaTypeFactory
 *
 * @author Tony Wardell
 * Date: 20/11/2017
 * Time: 16:34
 * Created with IntelliJ IDEA.
 */
public class MediaTypeFactoryTest {

    @Test
    public void requestedMediaTypeWithoutCharacterSet() {
        assertThat(MediaTypeFactory.fileExtension(new MediaType("text", "tsv")), is("tsv"));
    }

    @Test
    public void requestedMediaTypeWithUTF8() {
        assertThat(MediaTypeFactory.fileExtension(new MediaType("text", "tsv", StandardCharsets.UTF_8)),
                is("tsv"));
    }

    @Test
    public void requestedMediaTypeIsExcel() {
        assertThat(MediaTypeFactory.fileExtension(EXCEL_MEDIA_TYPE), is("xls"));
    }
}
