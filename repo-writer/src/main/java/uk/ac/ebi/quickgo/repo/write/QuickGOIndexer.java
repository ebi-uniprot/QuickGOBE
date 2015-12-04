package uk.ac.ebi.quickgo.repo.write;

import uk.ac.ebi.quickgo.repo.write.job.IndexingJobConfig;

import org.springframework.context.annotation.Import;

/**
 * Created 02/12/15
 * @author Edd
 */
@Import({IndexingJobConfig.class})
public class QuickGOIndexer {
}
