package uk.ac.ebi.quickgo.rest.service;

import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 01/04/2016
 * Time: 13:31
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceHelperImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private QueryStringSanitizer queryStringSanitizer;

    private ServiceHelper serviceHelper;

    private static final String SINGLE_ID = "A0A000";

    @Before
    public void setup() {
        serviceHelper = new ServiceHelperImpl(queryStringSanitizer);
        when(queryStringSanitizer.sanitize(SINGLE_ID)).thenReturn(SINGLE_ID);
    }

    @Test
    public void valid() {
        List<String> sanitizedList = serviceHelper.buildIdList(Collections.singletonList(SINGLE_ID));
        assertThat(sanitizedList, containsInAnyOrder(SINGLE_ID));
    }

    @Test
    public void blowupIfNull() {
        thrown.expect(IllegalArgumentException.class);
        serviceHelper.buildIdList(null);
    }
}