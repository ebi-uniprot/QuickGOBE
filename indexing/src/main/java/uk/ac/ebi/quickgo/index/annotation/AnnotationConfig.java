package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepoConfig;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.index.common.GZipBufferedReaderFactory;
import uk.ac.ebi.quickgo.index.common.SolrServerWriter;

import java.util.function.Function;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.infrastructure.item.file.transform.LineTokenizer;
import org.springframework.batch.infrastructure.item.validator.ValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.apache.solr.client.solrj.SolrClient;

import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.TAB;

/**
 * Sets up batch jobs for annotation indexing.
 *
 * Created 20/04/16
 * @author Edd
 */
@Configuration
@Import({AnnotationRepoConfig.class})
public class AnnotationConfig {
    static final String COLLECTION = SolrCollectionName.ANNOTATION;
    static final String ANNOTATION_INDEXING_JOB_NAME = "annotationIndexingJob";
    static final String ANNOTATION_INDEXING_STEP_NAME = "annotationIndexStep";

    @Value("${indexing.annotation.source}")
    private Resource[] resources;
    @Value("${indexing.annotation.header.lines:21}")
    private int headerLines;

    @Autowired
    private SolrClient annotationSolrClient;

    @Bean
    MultiResourceItemReader<Annotation> annotationMultiFileReader() {
        MultiResourceItemReader<Annotation> reader = new MultiResourceItemReader<>(annotationSingleFileReader());
        reader.setResources(resources);
        return reader;
    }

    @Bean
    FlatFileItemReader<Annotation> annotationSingleFileReader() {
        FlatFileItemReader<Annotation> reader = new FlatFileItemReader<>(annotationLineMapper());
        reader.setBufferedReaderFactory(new GZipBufferedReaderFactory());
        reader.setLinesToSkip(headerLines);
        return reader;
    }

    @Bean
    LineMapper<Annotation> annotationLineMapper() {
        DefaultLineMapper<Annotation> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(annotationLineTokenizer());
        lineMapper.setFieldSetMapper(annotationFieldSetMapper());
        return lineMapper;
    }

    @Bean
    LineTokenizer annotationLineTokenizer() {
        return new DelimitedLineTokenizer(TAB);
    }

    @Bean
    FieldSetMapper<Annotation> annotationFieldSetMapper() {
        return new StringToAnnotationMapper();
    }

    @Bean
    ItemProcessor<Annotation, AnnotationDocument> annotationDocConverter() {
        return new AnnotationDocumentConverter();
    }

    @Bean
    ItemProcessor<Annotation, Annotation> annotationValidator() {
        Validator<Annotation> annotationValidator =
                new AnnotationValidator();
        return new ValidatingItemProcessor<>(annotationValidator);
    }

    @Bean
    ItemProcessor<AnnotationDocument, AnnotationDocument> annotationShardGenerator() {
        return new AnnotationPartitionKeyGenerator(shardingKeyGenerator());
    }

    @Bean
    Function<AnnotationDocument, String> shardingKeyGenerator() {
        return (AnnotationDocument doc) -> doc.geneProductId;
    }

    @Bean
    ItemWriter<AnnotationDocument> annotationSolrServerWriter() {
        return new SolrServerWriter<>(annotationSolrClient, COLLECTION);
    }
}
