package uk.ac.ebi.quickgo.annotation.download.http;

import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.*;

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

    @Test
    public void createMediaType_subTypeTest() {
        var subType = MediaTypeFactory.createMediaType(GPAD_SUB_TYPE).getSubtype();
        assertThat(subType, equalTo(GPAD_SUB_TYPE));
    }

    @Test
    public void createMediaType_equalMatchTest() {
        var type = MediaTypeFactory.createMediaType(GAF_SUB_TYPE);
        assertThat(type, equalTo(GAF_MEDIA_TYPE));
    }

    @Test
    public void createMediaType_TypeMatchTest() {
        var type = MediaTypeFactory.createMediaType(TSV_SUB_TYPE).getType();
        assertThat(type, equalTo(TEXT_TYPE));
    }

    @Test
    public void createMediaType_characterSetTest() {
        var charSet = MediaTypeFactory.createMediaType(TSV_SUB_TYPE).getCharset();
        assertThat(charSet, equalTo(DEFAULT_CHARSET));
    }
}
