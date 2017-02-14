package uk.ac.ebi.quickgo.index.annotation;

import org.springframework.context.annotation.Configuration;

/**
 * Created 13/02/17
 * @author Edd
 */
@Configuration
public class AnnotationRetryConfig {
//
//    @Profile("twoSolrRemoteHostErrors")
//    @Bean @Primary
//    ItemWriter<AnnotationDocument> annotationSolrServerWriter() throws Exception {
//        ItemWriter<AnnotationDocument> mockItemWriter = mock(ItemWriter.class);
//
//        doNothing() // write first chunk
//                .doThrow(new HttpSolrClient.RemoteSolrException("host", 1, "bad", null))
//                .doNothing() // write second chunk
//                .doNothing()
//                .doThrow(new HttpSolrClient.RemoteSolrException("host", 1, "bad", null))
//                .doNothing()
//                .when(mockItemWriter).write(any());
//
//        return mockItemWriter;
//    }

//    @Profile("tooManySolrRemoteHostErrors")
//    @Bean @Primary
//    ItemWriter<AnnotationDocument> annotationSolrServerWriterWithTooManyErrors() throws Exception {
//        ItemWriter<AnnotationDocument> mockItemWriter = mock(ItemWriter.class);
//
//        doNothing() // write first chunk
//                .doThrow(new HttpSolrClient.RemoteSolrException("host", 1, "bad", null))
//                .doNothing() // write second chunk
//                .doNothing()
//                .doThrow(new HttpSolrClient.RemoteSolrException("host", 1, "bad", null))
//                .doThrow(new HttpSolrClient.RemoteSolrException("host", 1, "bad", null))
//                .doNothing() // never called
//                .doNothing() // never called
//                .when(mockItemWriter).write(any());
//
//        return mockItemWriter;
//    }
}
