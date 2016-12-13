package uk.ac.ebi.quickgo.ontology.common.coterms;

import java.io.IOException;
import java.util.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.Resource;

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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String ID_1 = "GO:0001234";
    private static final String ID_2 = "GO:0003870";
    private static final String ID_3 = "GO:0009058";
    private static final String ID_4 = "GO:0016857";
    private static final String ID_5 = "GO:0009999";
    private static final String ID_6 = "GO:0055085";

    private static final CoTerm CO_TERM_A = new CoTerm(ID_1, ID_1, 79f, 46f, 838, 933);
    private static final CoTerm CO_TERM_B = new CoTerm(ID_1, ID_2, 54f, 55f, 335, 9424);
    private static final CoTerm CO_TERM_C = new CoTerm(ID_1, ID_3, 24f, 24f, 5732, 355);
    private static final CoTerm CO_TERM_D = new CoTerm(ID_4, ID_1, 34f, 66f, 556, 872);
    private static final CoTerm CO_TERM_E = new CoTerm(ID_6, ID_5, 99f, 47f, 34356, 456);
    private static final CoTerm CO_TERM_F = new CoTerm(ID_6, ID_1, 24f, 4f, 465, 4564);

    private CoTermRepositorySimpleMap coTermRepository;


    @Test
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfAllMapIsNull() throws IOException{
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Map coTermsAll is null.");
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(null, null);
    }

    @Test
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfManualResourceIsNull() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Resource manualCoTermsSource is null.");
        Resource mockAllResource = mock(Resource.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(null, mockAllResource);
    }

    @Test
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfAllResourceIsNull() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Resource allCoTermSource is null.");
        Resource mockManualResource = mock(Resource.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, null);
    }


    @Test
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfManualResourceIsNonExistent() throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Resource manualCoTermsSource does not exist.");

        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource);
    }

    @Test
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfAllResourceIsNonExistent() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Resource allCoTermSource does not exist.");

        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource);
    }

    @Test(expected = IOException.class)
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfAllResourceIsBad() throws Exception {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(true);
        when(mockAllResource.getURI()).thenThrow(IOException.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource);
    }

    // Test retrieval - failure

    @Test
    public void findCoTermsThrowsExceptionIfSearchIdIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The findCoTerms id is null.");
        coTermRepository.findCoTerms(null, CoTermSource.ALL);
    }
    @Test
    public void findCoTermsThrowsExceptionIfCoTermSearchIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The findCoTerms source is null.");
        coTermRepository.findCoTerms(ID_1, null);
    }
}
