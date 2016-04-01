package uk.ac.ebi.quickgo.rest.search;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @Author Tony Wardell
 * Date: 30/03/2016
 * Time: 16:48
 * Created with IntelliJ IDEA.
 */
public class ControllerHelperTest {

	private ControllerHelper controllerHelper;

	@Before
	public void setUp() {
		this.controllerHelper = new ControllerHelperImpl();
	}

	@Test
	public void createsListFromNullCSV() {
		assertThat(controllerHelper.csvToList(null).size(), is(0));
	}

	@Test
	public void createsListFromCSVForNoItems() {
		assertThat(controllerHelper.csvToList("").size(), is(0));
	}

	@Test
	public void createsListFromCSVForOneItem() {
		assertThat(controllerHelper.csvToList("a").size(), is(1));
	}

	@Test
	public void createsListFromCSVForTwoItems() {
		assertThat(controllerHelper.csvToList("a,b").size(), is(2));
	}
}
