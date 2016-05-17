package uk.ac.ebi.quickgo.geneproduct.model;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

import java.util.List;

/**
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 14:04
 *         Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneProductDbXRefIDFormatsTest {

	List<GeneProductDbXRefIDFormat> listOfFormats;

	private GeneProductDbXRefIDFormats dbXrefEntities;

	@Mock
	private List<GeneProductDbXRefIDFormat> resultList;

	@Mock
	GeneProductDbXRefIDFormat mockEntity1;

	@Mock
	GeneProductDbXRefIDFormat mockEntity2;

	@Mock
	GeneProductDbXRefIDFormat mockEntity3;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup(){

		listOfFormats = new ArrayList<>();
		listOfFormats.add(mockEntity1);
		listOfFormats.add(mockEntity2);
		listOfFormats.add(mockEntity3);

		when(mockEntity1.getDatabase()).thenReturn("AGI_LocusCode");
		when(mockEntity2.getDatabase()).thenReturn("Ensembl");
		when(mockEntity3.getDatabase()).thenReturn("UniProt");
		when(mockEntity1.getEntityTypeName()).thenReturn("protein");
		when(mockEntity2.getEntityTypeName()).thenReturn("protein");
		when(mockEntity3.getEntityTypeName()).thenReturn("protein");
		when(mockEntity3.matches("ABC")).thenReturn(true);
		when(mockEntity3.matches("ZZZ")).thenReturn(false);
		dbXrefEntities = GeneProductDbXRefIDFormats.createWithData(listOfFormats, "UniProt", "protein");

	}

	@Test
	public void isValidId(){
		assertThat(dbXrefEntities.isValidId("ABC"), is(true));
	}

	@Test
	public void isValidIdWhenSupplyingDatabaseAndTypeName(){
		assertThat(dbXrefEntities.isValidId("ABC", "UniProt", "protein"), is(true));
	}

	@Test
	public void invalidDatabaseAndTypeName(){
		assertThat(dbXrefEntities.isValidId("ABC", "UniProt", "proteinX"), is(false));
	}

	@Test
	public void isInvalidId(){

		assertThat(dbXrefEntities.isValidId("ZZZ"), is(false));

	}

	@Test
	public void throwsErrorIfEntitiesIsNull(){
		thrown.expect(NullPointerException.class);
		dbXrefEntities = GeneProductDbXRefIDFormats.createWithData(null, "UniProt", "protein");
	}
}
