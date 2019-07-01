package uk.ac.ebi.quickgo.common.validator;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 14:04
 *         Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class DbXRefEntityValidationTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DbXRefEntityValidation dbXrefEntities;

	@Mock
	private DbXRefEntity rnaCentralEntity;

	@Mock
	private DbXRefEntity intactEntity;

	@Mock
	private DbXRefEntity uniprotEntity;

	@Before
	public void setup(){
		List<DbXRefEntity> listOfFormats = new ArrayList<>();
		listOfFormats.add(rnaCentralEntity);
		listOfFormats.add(intactEntity);
		listOfFormats.add(uniprotEntity);

		when(rnaCentralEntity.getDatabase()).thenReturn("RNAcentral");
		when(rnaCentralEntity.getEntityType()).thenReturn("CHEBI:33697");
		when(rnaCentralEntity.matches("71URS0000000001_733")).thenReturn(true);

		when(intactEntity.getDatabase()).thenReturn("IntAct");
		when(intactEntity.getEntityType()).thenReturn("GO:0043234");
		when(intactEntity.matches("EBI-11166735")).thenReturn(true);

        when(intactEntity.getDatabase()).thenReturn("ComplexPortal");
        when(intactEntity.getEntityType()).thenReturn("GO:0032991");
        when(intactEntity.matches("CPX-101")).thenReturn(true);

		when(uniprotEntity.getDatabase()).thenReturn("UniProtKB");
		when(uniprotEntity.getEntityType()).thenReturn("PR:000000001");
		when(uniprotEntity.matches("A0A000")).thenReturn(true);

		dbXrefEntities = DbXRefEntityValidation.createWithData(listOfFormats);
	}

	@Test
	public void isValidId(){
		assertThat(dbXrefEntities.test("A0A000"), is(true));
	}

	@Test
	public void isValidRNACentralID(){
		assertThat(dbXrefEntities.test("71URS0000000001_733"), is(true));
	}

    @Test
    public void isValidIntActID() {
        assertThat(dbXrefEntities.test("EBI-11166735"), is(true));
    }

	@Test
    public void isValidComplexID() {
        assertThat(dbXrefEntities.test("CPX-101"), is(true));
	}

	@Test
	public void invalidDatabaseAndTypeName(){
		assertThat(dbXrefEntities.test("ABC"), is(false));
	}

	@Test
	public void isInvalidId(){
		assertThat(dbXrefEntities.test("9999"), is(false));
	}

	@Test
	public void throwsErrorIfEntitiesIsNull(){
		thrown.expect(IllegalArgumentException.class);
		dbXrefEntities = DbXRefEntityValidation.createWithData(null);
	}
}
