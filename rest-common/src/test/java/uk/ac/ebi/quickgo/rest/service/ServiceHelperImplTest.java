package uk.ac.ebi.quickgo.rest.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Matchers.contains;
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
	public ExpectedException thrown= ExpectedException.none();

	private ServiceHelper serviceHelper;

	@Mock
	private QueryStringSanitizer queryStringSanitizer;

	public static final String SINGLE_ID = "A0A000";


	@Before
	public void setup(){
		serviceHelper = new ServiceHelperImpl(queryStringSanitizer);
		when(queryStringSanitizer.sanitize(SINGLE_ID)).thenReturn(SINGLE_ID);
	}


	@Test
	public void valid(){
		List<String> singleId = Arrays.asList(SINGLE_ID);
		List<String>  sanitizedList = serviceHelper.buildIdList(singleId);
		assertThat(sanitizedList, containsInAnyOrder(SINGLE_ID));

	}

	@Test
	public void blowupIfNull(){
		thrown.expect(IllegalArgumentException.class);
		List<String>  sanitizedList = serviceHelper.buildIdList(null);

	}
}
