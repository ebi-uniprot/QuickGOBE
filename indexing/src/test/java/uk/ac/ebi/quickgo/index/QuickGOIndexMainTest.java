package uk.ac.ebi.quickgo.index;

import uk.ac.ebi.quickgo.index.common.listener.LogJobListener;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Test that the indexing job's exit code is correctly returned to the caller (e.g., a bash script).
 *
 * Created 19/06/17
 * @author Edd
 */
public class QuickGOIndexMainTest {
    private static final int SUCCESS_EXIT_CODE = 0;

    @Test
    public void successfulSpringBootApplicationHasCorrectExitStatus() {
        assertThat(
                QuickGOIndexMain.run(QuickGOIndexMainTest.SuccessfulTestApp.class, new String[]{}),
                is(SUCCESS_EXIT_CODE));
    }

    @Test
    public void failedSpringBootApplicationHasCorrectExitStatus() {
        assertThat(
                QuickGOIndexMain.run(QuickGOIndexMainTest.FailingTestApp.class, new String[]{}),
                is(not(SUCCESS_EXIT_CODE)));
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableBatchProcessing
    abstract static class TestApp {
        static final int ITEM_COUNT = 10;
        int itemCount = 0;

        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private StepBuilderFactory steps;

        @Bean
        public Job testJob() throws Exception {
            return this.jobBuilderFactory
                    .get("test job")
                    .start(testStep())
                    .listener(new LogJobListener())
                    .build();
        }

        @Bean
        protected Step testStep() throws Exception {
            return this.steps
                    .get("test step")
                    .<String, String>chunk(1)
                    .reader(getStringItemReader())
                    .writer(createWriter())
                    .build();
        }

        private ItemReader<String> getStringItemReader() {
            return () -> {
                if (itemCount++ < ITEM_COUNT) {
                    return "source item " + itemCount;
                } else {
                    return null;
                }
            };
        }

        abstract ItemWriter<String> createWriter();
    }

    /**
     * This test application reads and writes successfully all items encountered
     */
    static class SuccessfulTestApp extends TestApp {
        @Override
        ItemWriter<String> createWriter() {
            return System.out::println;
        }

        public static void main(String[] args) throws Exception {
            System.exit(QuickGOIndexMain.run(SuccessfulTestApp.class, args));
        }
    }

    /**
     * This test application reads all items successfully, but fails to write some of the items
     * due to a deliberately thrown {@link Exception}. This simulates an erroneous batch job.
     */
    static class FailingTestApp extends TestApp {
        static final int WHEN_TO_FAIL = 9;

        ItemWriter<String> createWriter() {
            return list -> {
                if (itemCount >= WHEN_TO_FAIL) {
                    throw new HttpSolrClient.RemoteSolrException("localhost", 999, "No registered leader", new RuntimeException());
                } else {
                    System.out.println(list);
                }
            };
        }

        public static void main(String[] args) throws Exception {
            System.exit(QuickGOIndexMain.run(SuccessfulTestApp.class, args));
        }
    }
}