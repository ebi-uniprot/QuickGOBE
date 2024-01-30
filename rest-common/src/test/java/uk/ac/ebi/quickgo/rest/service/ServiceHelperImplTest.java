package uk.ac.ebi.quickgo.rest.service;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 01/04/2016
 * Time: 13:31
 * Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
class ServiceHelperImplTest {

    @Mock
    private QueryStringSanitizer queryStringSanitizer;

    private ServiceHelper serviceHelper;

    private static final String SINGLE_ID = "A0A000";

    @BeforeEach
    void setup() {
        serviceHelper = new ServiceHelperImpl(queryStringSanitizer);
    }

    @Test
    void valid() {
        when(queryStringSanitizer.sanitize(SINGLE_ID)).thenReturn(SINGLE_ID);
        List<String> sanitizedList = serviceHelper.buildIdList(Collections.singletonList(SINGLE_ID));
        assertThat(sanitizedList, containsInAnyOrder(SINGLE_ID));
    }

    @Test
    void blowupIfNull() {
        assertThrows(IllegalArgumentException.class, () -> serviceHelper.buildIdList(null));
    }
}