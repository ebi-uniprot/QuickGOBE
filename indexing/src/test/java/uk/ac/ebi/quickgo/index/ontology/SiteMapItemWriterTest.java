package uk.ac.ebi.quickgo.index.ontology;

import com.redfin.sitemapgenerator.WebSitemapUrl;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createECODoc;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createGODoc;

/**
 * Created 04/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SiteMapItemWriterTest {
    @Mock
    private MockableWebSitemapGenerator webSitemapGenerator;

    @Captor
    private ArgumentCaptor<WebSitemapUrl> argumentCaptor;

    @Test(expected = IllegalArgumentException.class)
    public void nullSiteUrlCausesException() {
        String siteUrl = null;
        new SiteMapItemWriter(webSitemapGenerator, siteUrl);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSiteMapGeneratorCausesException() {
        String siteUrl = "http://www.ebi.ac.uk";
        new SiteMapItemWriter(null, siteUrl);
    }

    @Test
    public void canCreateValidItemWriter() {
        String siteUrl = "http://www.ebi.ac.uk";
        SiteMapItemWriter itemWriter = new SiteMapItemWriter(webSitemapGenerator, siteUrl);
        assertThat(itemWriter, is(notNullValue()));
    }

    @Test
    public void writeWillAddUrlsToSiteMap() throws Exception {
        String siteUrl = "http://www.ebi.ac.uk/QuickGO/term";
        SiteMapItemWriter itemWriter = new SiteMapItemWriter(webSitemapGenerator, siteUrl);

        String goId = "GO:0000001";
        String ecoId = "ECO:0000001";
        itemWriter.write(asList(createGODoc(goId, "go name"), createECODoc(ecoId, "eco name")));

        verify(webSitemapGenerator, times(2)).addUrl(argumentCaptor.capture());
        List<WebSitemapUrl> urlsToWrite = argumentCaptor.getAllValues();

        assertThat(urlsToWrite, hasSize(2));

        List<String> urlStringsToWrite =
                urlsToWrite.stream().map(url -> url.getUrl().toString()).collect(Collectors.toList());
        assertThat(urlStringsToWrite, containsInAnyOrder(siteUrl + "/" + goId, siteUrl + "/" + ecoId));
    }
}