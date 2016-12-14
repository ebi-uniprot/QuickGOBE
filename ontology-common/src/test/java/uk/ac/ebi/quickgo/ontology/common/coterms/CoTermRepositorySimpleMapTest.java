package uk.ac.ebi.quickgo.ontology.common.coterms;

import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.Resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 14:57
 * Created with IntelliJ IDEA.
 */
public class CoTermRepositorySimpleMapTest {

    private static final int headerLines = 1;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createFailsIfManualResourceIsNull() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Resource manualCoTermsSource is null.");
        Resource mockAllResource = mock(Resource.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(null, mockAllResource, headerLines);
    }

    @Test
    public void createFailsIfAllResourceIsNull() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Resource allCoTermSource is null.");
        Resource mockManualResource = mock(Resource.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, null, headerLines);
    }

    @Test
    public void createFailsIfManualResourceIsNonExistent() throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Resource manualCoTermsSource does not exist.");

        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, headerLines);
    }

    @Test
    public void createFailsIfAllResourceIsNonExistent() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Resource allCoTermSource does not exist.");

        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, headerLines);
    }

    @Test(expected = IOException.class)
    public void createFailsWithIfAllResourceIsBad() throws Exception {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(true);
        when(mockAllResource.getURI()).thenThrow(IOException.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, headerLines);
    }

    @Test
    public void createFailsIfHeaderLinesNegative() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The number of header lines is less than zero.");
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(true);
        int negHeaderLines = -1;
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, negHeaderLines);
    }
}
