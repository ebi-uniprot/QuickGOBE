package uk.ac.ebi.quickgo.ontology.common.coterms;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 14:57
 * Created with IntelliJ IDEA.
 */
public class CoTermRepositorySimpleMapTest {

    private CoTermRepositorySimpleMap coTermRepository;

    @Before
    public void setup() {
        Map<String, List<CoTerm>> coTermsAll = new HashMap<>();
        coTermsAll.put("GO:0001234", Arrays.asList(new CoTerm("GO:0001234", "GO:0001234", 79f, 46f, 838, 933),
                new CoTerm("GO:0001234", "GO:0003870", 54f, 55f, 335, 9424),
                new CoTerm("GO:0001234", "GO:0009058", 24f, 24f, 5732, 355)));
        coTermsAll.put("GO:0016857",
                Collections.singletonList(new CoTerm("GO:0016857", "GO:0001234", 34f, 66f, 556, 872)));

        Map<String, List<CoTerm>> coTermsManual = new HashMap<>();
        coTermsManual.put("GO:0001234", Arrays.asList(new CoTerm("GO:0001234", "GO:0009999", 99f, 47f, 34356, 456),
                new CoTerm("GO:0001234", "GO:0055085", 24f, 4f, 465, 4564)));

        coTermRepository = new CoTermRepositorySimpleMap(coTermsAll, coTermsManual);

    }

    @Test
    public void retrievalIsSuccessfulFromAll() {
        List<CoTerm> results = coTermRepository.findCoTerms("GO:0001234", CoTermSource.ALL, 7, t -> true);
        assertThat(results, hasSize(3));
    }

    @Test
    public void retrievalIsSuccessfulWithFilteringPredicate() {
        Predicate<CoTerm> filter = ct -> ct.getProbabilityRatio() == 54f;
        List<CoTerm> results = coTermRepository.findCoTerms("GO:0001234", CoTermSource.ALL, 7, filter);
        assertThat(results, hasSize(1));
        assertThat(results.get(0).getId(), is("GO:0001234"));
        assertThat(results.get(0).getCompare(), is("GO:0003870"));
    }

    @Test
    public void retrievalIsSuccessfulWithLimitLessThanNumberOfRecordsThatWouldOtherwiseBeReturned() {
        List<CoTerm> results = coTermRepository.findCoTerms("GO:0001234", CoTermSource.ALL, 1, t -> true);
        assertThat(results, hasSize(1));
    }

    @Test
    public void retrievalIsSuccessfulFromManual() {
        List<CoTerm> results = coTermRepository.findCoTerms("GO:0001234", CoTermSource.MANUAL, 7, t -> true);
        assertThat(results, hasSize(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfCoTermSourceIsNull() {
        coTermRepository.findCoTerms("GO:0001234", null, 7, t -> true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfSearchIdIsNull() {
        coTermRepository.findCoTerms(null, CoTermSource.ALL, 7, t -> true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfFilterIsNull() {
        coTermRepository.findCoTerms("GO:0001234", CoTermSource.ALL, 7, null);
    }

    @Test(expected = IllegalStateException.class)
    public void manualSourceDoesNotExistSoExceptionIsThrown() throws IOException {
        Resource mockManualResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.CoTermLoader coTermLoader = coTermRepository.new CoTermLoader(mockManualResource,
                mock(Resource.class));
        coTermLoader.load();
    }

    @Test(expected = IllegalStateException.class)
    public void allSourceDoesNotExistSoExceptionIsThrown() throws Exception {
        Resource mockAllResource = mock(Resource.class);
        when(mockAllResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.CoTermLoader coTermLoader = coTermRepository.new CoTermLoader(mock(Resource.class),
                mockAllResource);
        coTermLoader.load();
    }

    @Test(expected = IOException.class)
    public void readingSourceFailsAndThrowsIOException() throws Exception {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(true);
        when(mockAllResource.getInputStream()).thenThrow(IOException.class);
        CoTermRepositorySimpleMap.CoTermLoader coTermLoader = coTermRepository.new CoTermLoader(mockManualResource,
                mockAllResource);
        coTermLoader.load();
    }
}
