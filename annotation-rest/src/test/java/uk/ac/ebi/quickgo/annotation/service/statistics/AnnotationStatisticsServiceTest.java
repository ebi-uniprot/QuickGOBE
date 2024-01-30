package uk.ac.ebi.quickgo.annotation.service.statistics;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.AggregateResponse;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

/**
 * Tests the behaviour of the {@link AnnotationStatisticsService} class.
 */
@ExtendWith(MockitoExtension.class)
class AnnotationStatisticsServiceTest {

    private AnnotationStatisticsService statsService;

    @Mock
    private FilterConverterFactory filterFactoryMock;

    @Mock
    private SearchService<Annotation> searchServiceMock;

    @Mock
    private StatsConverter statsConverterMock;

    @Mock
    private RequiredStatisticsProvider requiredStatisticsProvider;

    @Mock
    private QueryResult<Annotation> queryResult;

    @Mock
    private AggregateResponse aggregateResponse;

    @Mock
    private ConvertedFilter<QuickGOQuery> convertedFilter;

    @BeforeEach
    void setUp() {
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, requiredStatisticsProvider);
    }

    @Test
    void nullFilterFactoryThrowsExceptionInConstructor() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AnnotationStatisticsService(null,
          searchServiceMock, statsConverterMock, requiredStatisticsProvider));
        assertTrue(exception.getMessage().contains("Filter factory cannot be null"));
    }

    @Test
    void nullSearchServiceThrowsExceptionInConstructor() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AnnotationStatisticsService(
          filterFactoryMock, null, statsConverterMock, requiredStatisticsProvider));
        assertTrue(exception.getMessage().contains("Search service cannot be null"));
    }

    @Test
    void nullStatsConverterThrowsExceptionInConstructor() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AnnotationStatisticsService(
          filterFactoryMock, searchServiceMock, null, requiredStatisticsProvider));
        assertTrue(exception.getMessage().contains("Stats request converter cannot be null"));
    }

    @Test
    void nullRequiredStatisticsProviderThrowsExceptionInConstructor() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new AnnotationStatisticsService(
          filterFactoryMock, searchServiceMock, statsConverterMock, null));
        assertTrue(exception.getMessage().contains("Statistics provider cannot be null."));
    }

    @Test
    void calculatingRequiredStatisticsForStandardUsageWithNullAnnotationThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> statsService.calculateForStandardUsage(null));
        assertTrue(exception.getMessage().contains("Annotation request cannot be null"));
    }

    class TestFilterConverter implements FilterConverter<FilterRequest, QuickGOQuery> {

        @Override public ConvertedFilter<QuickGOQuery> transform(FilterRequest request) {
            return convertedFilter;
        }
    }
}
