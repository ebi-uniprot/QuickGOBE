package uk.ac.ebi.quickgo.index.ontology;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Mockito cannot mock WebsitemapGenerator because it does not have a public parent. Therefore
 * this class simply wraps the used logic of WebsitemapGenerator, so that Mockito can mock it.
 * 
 * Created 04/04/17
 * @author Edd
 */
public class MockableWebSitemapGenerator extends WebSitemapGenerator {

    public MockableWebSitemapGenerator(String baseUrl) throws MalformedURLException {
        super(baseUrl);
    }

    @Override public List<File> write() {
        return super.write();
    }

    @Override public void writeSitemapsWithIndex() {
        super.writeSitemapsWithIndex();
    }

    @Override public WebSitemapGenerator addUrl(WebSitemapUrl url) {
        return super.addUrl(url);
    }
}
