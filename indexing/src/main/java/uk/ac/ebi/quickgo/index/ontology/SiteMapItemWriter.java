package uk.ac.ebi.quickgo.index.ontology;

import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import org.springframework.batch.item.Chunk;

import java.util.Date;
import org.springframework.batch.item.ItemWriter;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class is responsible for writing the URLs of sitemap, where each
 * URL links to an ontology term id, which is taken from {@link OntologyDocument}s.
 *
 * Created 04/04/17
 * @author Edd
 */
public class SiteMapItemWriter implements ItemWriter<OntologyDocument> {
    private final String urlPrefix;
    private WebSitemapGenerator sitemapGenerator;

    SiteMapItemWriter(WebSitemapGenerator sitemapGenerator, String urlPrefix) {
        checkArgument(sitemapGenerator != null, "SiteMapGenerator cannot be null");
        checkArgument(urlPrefix != null && !urlPrefix.isEmpty(), "URL prefix for sitemap cannot be null or empty");

        this.sitemapGenerator = sitemapGenerator;
        this.urlPrefix = urlPrefix;
    }

    @Override
    public void write(Chunk<? extends OntologyDocument> list) throws Exception {
        for (OntologyDocument ontologyDocument : list) {
            WebSitemapUrl url = new WebSitemapUrl
                    .Options(buildTermURL(ontologyDocument))
                    .lastMod(new Date())
                    .priority(1.0)
                    .changeFreq(ChangeFreq.DAILY)
                    .build();
            sitemapGenerator.addUrl(url);
        }
    }

    private String buildTermURL(OntologyDocument ontologyDocument) {
        return urlPrefix + "/" + ontologyDocument.id;
    }
}
