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
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private List<GeneProductDbXRefIDFormat> listOfFormats;

	private GeneProductDbXRefIDFormats dbXrefEntities;

	@Mock
	private GeneProductDbXRefIDFormat rnaCentralEntity;

	@Mock
	private GeneProductDbXRefIDFormat intactEntity;

	@Mock
	private GeneProductDbXRefIDFormat uniprotEntity;

	@Before
	public void setup(){
		listOfFormats = new ArrayList<>();
		listOfFormats.add(rnaCentralEntity);
		listOfFormats.add(intactEntity);
		listOfFormats.add(uniprotEntity);

		when(rnaCentralEntity.getDatabase()).thenReturn("RNAcentral");
		when(rnaCentralEntity.getEntityType()).thenReturn("CHEBI:33697");
		when(rnaCentralEntity.matches("71URS0000000001_733")).thenReturn(true);

		when(intactEntity.getDatabase()).thenReturn("IntAct");
		when(intactEntity.getEntityType()).thenReturn("GO:0043234");
		when(intactEntity.matches("EBI-11166735")).thenReturn(true);

		when(uniprotEntity.getDatabase()).thenReturn("UniProtKB");
		when(uniprotEntity.getEntityType()).thenReturn("PR:000000001");
		when(uniprotEntity.matches("A0A000")).thenReturn(true);
		when(uniprotEntity.matches("999999")).thenReturn(false);

		dbXrefEntities = GeneProductDbXRefIDFormats.createWithData(listOfFormats);
	}

	@Test
	public void isValidId(){
		assertThat(dbXrefEntities.isValidId("A0A000"), is(true));
	}

	@Test
	public void isValidRNACentralID(){
		assertThat(dbXrefEntities.isValidId("71URS0000000001_733"), is(true));
	}

	@Test
	public void isValidIntActID(){
		assertThat(dbXrefEntities.isValidId("EBI-11166735"), is(true));
	}

	@Test
	public void invalidDatabaseAndTypeName(){
		assertThat(dbXrefEntities.isValidId("ABC"), is(false));
	}

	@Test
	public void isInvalidId(){
		assertThat(dbXrefEntities.isValidId("9999"), is(false));
	}

	@Test
	public void throwsErrorIfEntitiesIsNull(){
		thrown.expect(IllegalArgumentException.class);
		dbXrefEntities = GeneProductDbXRefIDFormats.createWithData(null);
	}
}