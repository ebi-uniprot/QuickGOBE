package uk.ac.ebi.quickgo.annotation.coterms;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 14:57
 * Created with IntelliJ IDEA.
 */
class CoTermRepositorySimpleMapTest {

    private static final int headerLines = 1;

    @Test
    void createFailsIfManualResourceIsNull() throws IOException {
        Resource mockAllResource = mock(Resource.class);
        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
            CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(null, mockAllResource, headerLines)
        );
        assertTrue(exception.getMessage().contains("Resource manualCoTermsSource is null."));
    }

    @Test
    void createFailsIfAllResourceIsNull() throws IOException {
        Resource mockManualResource = mock(Resource.class);
        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
            CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, null, headerLines)
        );
        assertTrue(exception.getMessage().contains("Resource allCoTermSource is null."));
    }

    @Test
    void createFailsIfManualResourceIsNonExistent() throws IOException {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(false);
        Throwable exception = assertThrows(IllegalStateException.class, () ->
            CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, headerLines)
        );
        assertTrue(exception.getMessage().contains("Resource manualCoTermsSource does not exist."));
    }

    @Test
    void createFailsIfAllResourceIsNonExistent() throws Exception {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(false);
        Throwable exception = assertThrows(IllegalStateException.class, () ->
            CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, headerLines)
        );
        assertTrue(exception.getMessage().contains("Resource allCoTermSource does not exist."));
    }

    @Test
    void createFailsWithIfAllResourceIsBad() throws Exception {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(true);
        doThrow(IOException.class).when(mockAllResource).getURI();
        assertThrows(IOException.class, () ->
            CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, headerLines)
        );
    }

    @Test
    void createFailsIfHeaderLinesNegative() throws IOException {
        Resource mockManualResource = mock(Resource.class);
        Resource mockAllResource = mock(Resource.class);
        when(mockManualResource.exists()).thenReturn(true);
        when(mockAllResource.exists()).thenReturn(true);
        int negHeaderLines = -1;
        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
            CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(mockManualResource, mockAllResource, negHeaderLines)
        );
        assertTrue(exception.getMessage().contains("The number of header lines is less than zero."));
    }
}
