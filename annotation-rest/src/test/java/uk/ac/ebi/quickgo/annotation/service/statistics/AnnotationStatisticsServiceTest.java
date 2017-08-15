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
    private StatsRequestConverter statsRequestConverterMock;

    @Mock
    private StatisticsTypeConfigurer statsConfigurer;

    @Before
    public void setUp() throws Exception {
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsRequestConverterMock, statsConfigurer);
    }

    @Test
    public void nullFilterFactoryThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter factory cannot be null");

        statsService = new AnnotationStatisticsService(null, searchServiceMock,
                statsRequestConverterMock, statsConfigurer);
    }

    @Test
    public void nullSearchServiceThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Search service cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, null,
                statsRequestConverterMock, statsConfigurer);
    }

    @Test
    public void nullStatsRequestConverterThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats request converter cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                null, statsConfigurer);
    }

    @Test
    public void nullStatsRequestConfigurerThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats request configurer cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsRequestConverterMock, null);
    }

    @Test
    public void calculatingNullAnnotationRequestThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation request cannot be null");

        statsService.calculate(null);
    }

    @Test
    public void calculatingStatsForAnnotationRequestWithNullStatsRequestThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Statistics request cannot be null");

        AnnotationRequest request = Mockito.mock(AnnotationRequest.class);
        when(request.createStatsRequests()).thenReturn(null);

        statsService.calculate(request);
    }
}