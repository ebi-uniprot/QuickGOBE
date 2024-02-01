package uk.ac.ebi.quickgo.common.validator;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 14:04
 *         Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DbXRefEntityValidationTest {

	private DbXRefEntityValidation dbXrefEntities;

	@Mock
	private DbXRefEntity rnaCentralEntity;

	@Mock
	private DbXRefEntity intactEntity;

	@Mock
	private DbXRefEntity uniprotEntity;

	@BeforeEach
	void setup(){
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
	void isValidId(){
		assertThat(dbXrefEntities.test("A0A000"), is(true));
	}

	@Test
	void isValidRNACentralID(){
		assertThat(dbXrefEntities.test("71URS0000000001_733"), is(true));
	}

    @Test
    void isValidIntActID() {
        assertThat(dbXrefEntities.test("EBI-11166735"), is(true));
    }

	@Test
    void isValidComplexID() {
        assertThat(dbXrefEntities.test("CPX-101"), is(true));
	}

	@Test
	void invalidDatabaseAndTypeName(){
		assertThat(dbXrefEntities.test("ABC"), is(false));
	}

	@Test
	void isInvalidId(){
		assertThat(dbXrefEntities.test("9999"), is(false));
	}

	@Test
	void throwsErrorIfEntitiesIsNull(){
        assertThrows(IllegalArgumentException.class, () -> dbXrefEntities = DbXRefEntityValidation.createWithData(null));
    }
}
