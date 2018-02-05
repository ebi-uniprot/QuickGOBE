package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.AggregateResponse;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests the behaviour of the {@link AnnotationStatisticsService} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationStatisticsServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

    @Before
    public void setUp() {
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, requiredStatisticsProvider);

        when(searchServiceMock.findByQuery(any())).thenReturn(queryResult);
        when(queryResult.getAggregation()).thenReturn(aggregateResponse);
        when(filterFactoryMock.convert(any())).thenReturn(convertedFilter);
    }

    @Test
    public void nullFilterFactoryThrowsExceptionInConstructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter factory cannot be null");

        statsService = new AnnotationStatisticsService(null, searchServiceMock,
                statsConverterMock, requiredStatisticsProvider);
    }

    @Test
    public void nullSearchServiceThrowsExceptionInConstructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Search service cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, null,
                statsConverterMock, requiredStatisticsProvider);
    }

    @Test
    public void nullStatsConverterThrowsExceptionInConstructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats request converter cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                null, requiredStatisticsProvider);
    }

    @Test
    public void nullRequiredStatisticsProviderThrowsExceptionInConstructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Statistics provider cannot be null.");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, null);
    }

    @Test
    public void calculatingRequiredStatisticsForStandardUsageWithNullAnnotationThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation request cannot be null");

        statsService.calculateForStandardUsage(null);
    }

    class TestFilterConverter implements FilterConverter<FilterRequest, QuickGOQuery> {

        @Override public ConvertedFilter<QuickGOQuery> transform(FilterRequest request) {
            return convertedFilter;
        }
    }
}
