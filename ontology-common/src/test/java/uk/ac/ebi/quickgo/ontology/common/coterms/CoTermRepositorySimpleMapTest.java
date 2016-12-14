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

    private static final int headerLines = 1;

    @Test
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfManualResourceIsNull() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Resource manualCoTermsSource is null.");
        Resource mockAllResource = mock(Resource.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(null, mockAllResource, headerLines);
    }

    @Test
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfAllResourceIsNull() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Resource allCoTermSource is null.");
        Resource mockManualResource = mock(Resource.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, null, headerLines);
    }

    @Test
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfManualResourceIsNonExistent() throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Resource manualCoTermsSource does not exist.");

        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, headerLines);
    }

    @Test
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfAllResourceIsNonExistent() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Resource allCoTermSource does not exist.");

        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(false);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, headerLines);
    }

    @Test(expected = IOException.class)
    public void createCoTermRepositorySimpleMapFailsWithExceptionIfAllResourceIsBad() throws Exception {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(true);
        when(mockAllResource.getURI()).thenThrow(IOException.class);
        CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, headerLines);
    }

    @Test
    public void exceptionIfHeaderLinesNegative() throws IOException{
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
