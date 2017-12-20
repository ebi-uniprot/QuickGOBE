package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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
    private RequiredStatistics requiredStatisticsForStandardUsage;

    @Mock
    private RequiredStatistics requiredStatisticsForDownloadUsage;

    @Mock
    private RequiredStatisticsProvider requiredStatisticsProvider;

    @Before
    public void setUp() {
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, requiredStatisticsProvider);
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
        thrown.expectMessage("Required statistics for standard usage cannot be null.");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, null);
    }

    @Test
    public void calculatingRequiredStatisticsForStandardUsageWithNullAnnotationThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation request cannot be null");

        statsService.calculateForStandardUsage(null);
    }

    @Test
    public void calculatingRequiredStatisticsForStandardUsageWithNullContentThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Required statistics for standard usage cannot be null.");

        when(requiredStatisticsForStandardUsage.getRequiredStatistics()).thenReturn(null);
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, requiredStatisticsProvider);
        AnnotationRequest request = Mockito.mock(AnnotationRequest.class);

        statsService.calculateForStandardUsage(request);
    }

    @Test
    public void calculatingRequiredStatisticsForDownloadUsageWithNullContentThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Required statistics for download cannot be null.");

        when(requiredStatisticsForDownloadUsage.getRequiredStatistics()).thenReturn(null);
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, requiredStatisticsProvider);
        AnnotationRequest request = Mockito.mock(AnnotationRequest.class);

        statsService.calculateForStandardUsage(request);
    }

}
