package uk.ac.ebi.quickgo.ontology.common.coterms;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
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

    public static final String ID_1 = "GO:0001234";
    public static final String ID_2 = "GO:0003870";
    public static final String ID_3 = "GO:0009058";
    public static final String ID_4 = "GO:0016857";
    public static final String ID_5 = "GO:0009999";
    public static final String ID_6 = "GO:0055085";

    public static final CoTerm CO_TERM_A1X = new CoTerm(ID_1, ID_1, 79f, 46f, 838, 933);
    public static final CoTerm CO_TERM_A1Y = new CoTerm(ID_1, ID_2, 54f, 55f, 335, 9424);
    public static final CoTerm CO_TERM_A1Z = new CoTerm(ID_1, ID_3, 24f, 24f, 5732, 355);
    public static final CoTerm CO_TERM_A2X = new CoTerm(ID_4, ID_1, 34f, 66f, 556, 872);
    public static final CoTerm CO_TERM_MX = new CoTerm(ID_6, ID_5, 99f, 47f, 34356, 456);
    public static final CoTerm CO_TERM_MY = new CoTerm(ID_6, ID_1, 24f, 4f, 465, 4564);
    public static final Predicate<CoTerm> NO_FILTER = t -> true;

    private CoTermRepositorySimpleMap coTermRepository;

    @Before
    public void setup() {
        Map<String, List<CoTerm>> coTermsAll = new HashMap<>();
        coTermsAll.put(ID_1, Arrays.asList(CO_TERM_A1X, CO_TERM_A1Y, CO_TERM_A1Z));
        coTermsAll.put(ID_4, Collections.singletonList(CO_TERM_A2X));

        Map<String, List<CoTerm>> coTermsManual = new HashMap<>();
        coTermsManual.put(ID_6, Arrays.asList(CO_TERM_MX, CO_TERM_MY));

        coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(coTermsAll, coTermsManual);

    }

    @Test
    public void retrievalIsSuccessfulFromAll() {
        List<CoTerm> results = coTermRepository.findCoTerms(ID_1, CoTermSource.ALL, Integer.MAX_VALUE, NO_FILTER);
        assertThat(results, hasSize(3));
        assertThat(results, containsInAnyOrder(CO_TERM_A1X, CO_TERM_A1Y, CO_TERM_A1Z));
    }

    @Test
    public void retrievalIsSuccessfulWithFilteringPredicate() {
        Predicate<CoTerm> filter = ct -> ct.getProbabilityRatio() == CO_TERM_A1Y.getProbabilityRatio();
        List<CoTerm> results = coTermRepository.findCoTerms(ID_1, CoTermSource.ALL, Integer.MAX_VALUE, filter);
        assertThat(results, hasSize(1));
        assertThat(results.get(0), is(CO_TERM_A1Y));
    }

    @Test
    public void retrievalIsSuccessfulWithLimitLessThanNumberOfRecordsThatWouldOtherwiseBeReturned() {
        List<CoTerm> results = coTermRepository.findCoTerms(ID_1, CoTermSource.ALL, 1, NO_FILTER);
        assertThat(results, hasSize(1));
        assertThat(results, containsInAnyOrder(CO_TERM_A1X));
    }

    @Test
    public void retrievalIsSuccessfulFromManual() {
        List<CoTerm> results = coTermRepository.findCoTerms(ID_6, CoTermSource.MANUAL, Integer.MAX_VALUE, NO_FILTER);
        assertThat(results, hasSize(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfCoTermSourceIsNull() {
        coTermRepository.findCoTerms(ID_1, null, Integer.MAX_VALUE, NO_FILTER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfSearchIdIsNull() {
        coTermRepository.findCoTerms(null, CoTermSource.ALL, Integer.MAX_VALUE, NO_FILTER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfFilterIsNull() {
        coTermRepository.findCoTerms(ID_1, CoTermSource.ALL, Integer.MAX_VALUE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfLimitIsZeroOrLess() {
        coTermRepository.findCoTerms(ID_1, CoTermSource.ALL, 0, NO_FILTER);
    }

    @Test(expected = IllegalStateException.class)
    public void manualSourceDoesNotExistSoExceptionIsThrown() throws IOException {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource);
    }

    @Test(expected = IllegalStateException.class)
    public void allSourceDoesNotExistSoExceptionIsThrown() throws Exception {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockAllResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource);
    }

    @Test(expected = IOException.class)
    public void readingSourceFailsAndThrowsIOException() throws Exception {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(true);
        when(mockAllResource.getInputStream()).thenThrow(IOException.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfMapsManualMapIsNull() {
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(null, new HashMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfMapsAllMapIsNull() {
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(new HashMap<>(), null);
    }
}
