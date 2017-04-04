package uk.ac.ebi.quickgo.index.ontology;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class is responsible for writing the contents of a sitemap
 * to file, at the end of the ontology indexing step.
 *
 * Created 04/04/17
 * @author Edd
 */
public class SiteMapStepListener implements StepExecutionListener {
    private final WebSitemapGenerator sitemapGenerator;

    SiteMapStepListener(WebSitemapGenerator sitemapGenerator) {
        checkArgument(sitemapGenerator != null, "SiteMapGenerator cannot be null");
        
        this.sitemapGenerator = sitemapGenerator;
    }

    @Override public void beforeStep(StepExecution stepExecution) {
        // no-op
    }

    @Override public ExitStatus afterStep(StepExecution stepExecution) {
        sitemapGenerator.write();
        sitemapGenerator.writeSitemapsWithIndex();
        return stepExecution.getExitStatus();
    }
}
