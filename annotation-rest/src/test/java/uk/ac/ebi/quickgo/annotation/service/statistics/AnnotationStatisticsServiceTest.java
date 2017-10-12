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
    private RequiredStatistics listStatistics;

    @Mock
    private RequiredStatistics downloadStatistics;

    @Before
    public void setUp() throws Exception {
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                                                       statsConverterMock, listStatistics, downloadStatistics);
    }

    @Test
    public void nullFilterFactoryThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter factory cannot be null");

        statsService = new AnnotationStatisticsService(null, searchServiceMock,
                                                       statsConverterMock, listStatistics, downloadStatistics);
    }

    @Test
    public void nullSearchServiceThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Search service cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, null,
                                                       statsConverterMock, listStatistics, downloadStatistics);
    }

    @Test
    public void nullStatsConverterThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats request converter cannot be null");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                                                       null, listStatistics, downloadStatistics);
    }

    @Test
    public void nullListStatsThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required stats list cannot be null.");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                statsConverterMock, null, downloadStatistics);
    }

    @Test
    public void nullDownloadStatisticsThrowsExceptionInConstructor() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required stats download cannot be null.");

        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                                                       statsConverterMock, listStatistics, null);
    }

    @Test
    public void calculatingNullAnnotationRequestThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation request cannot be null");

        statsService.calculate(null);
    }

    @Test
    public void calculatingListStatsWithNullContentThrowsException() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Required stats list for list cannot be null.");

        when(listStatistics.getStats()).thenReturn(null);
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                                                       statsConverterMock, listStatistics, downloadStatistics);
        AnnotationRequest request = Mockito.mock(AnnotationRequest.class);

        statsService.calculate(request);
    }

    @Test
    public void calculatingDownloadStatsWithNullContentThrowsException() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Required stats list for download cannot be null.");

        when(downloadStatistics.getStats()).thenReturn(null);
        statsService = new AnnotationStatisticsService(filterFactoryMock, searchServiceMock,
                                                       statsConverterMock, listStatistics, downloadStatistics);
        AnnotationRequest request = Mockito.mock(AnnotationRequest.class);

        statsService.calculate(request);
    }

}
