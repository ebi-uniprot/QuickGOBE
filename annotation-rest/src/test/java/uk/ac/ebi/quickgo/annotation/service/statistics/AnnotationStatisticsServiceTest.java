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
    private RequiredStatistics requiredStatistics;

    @Before
    public void setUp() throws Exception {
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, requiredStatistics);
    }

    @Test
    public void nullFilterFactoryThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter factory cannot be null");

        statsService = new AnnotationStatisticsService(null, searchServiceMock,
                statsConverterMock, requiredStatistics);
    }

    @Test
    public void nullSearchServiceThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Search service cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, null,
                statsConverterMock, requiredStatistics);
    }

    @Test
    public void nullStatsConverterThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats request converter cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                null, requiredStatistics);
    }

    @Test
    public void nullRequiredStatsThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required stats cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, null);
    }

    @Test
    public void calculatingNullAnnotationRequestThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation request cannot be null");

        statsService.calculate(null);
    }

    @Test
    public void calculatingStatsWithNullRequiredStatsListThrowsException() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Required stats list cannot be null");

        when(requiredStatistics.getStats()).thenReturn(null);
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, requiredStatistics);
        AnnotationRequest request = Mockito.mock(AnnotationRequest.class);

        statsService.calculate(request);
    }
}