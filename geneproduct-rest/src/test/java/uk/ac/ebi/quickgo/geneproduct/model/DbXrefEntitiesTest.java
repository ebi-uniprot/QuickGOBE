package uk.ac.ebi.quickgo.geneproduct.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

/**
 * @author Tony Wardell
 *         Date: 19/04/2016
 *         Time: 14:04
 *         Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class DbXrefEntitiesTest {

	@Mock
	Map mockMap;

	private DbXrefEntities dbXrefEntities;

	private GeneProductXrefEntity.Key key1;

	@Mock
	private List<GeneProductXrefEntity> resultList;

	@Mock
	GeneProductXrefEntity mockEntity;

	@Before
	public void setup(){

		dbXrefEntities = new DbXrefEntities(mockMap, "Wormbase", "variation");

		key1 = new GeneProductXrefEntity.Key("Wormbase", "variation"); //matches default

		when(mockMap.get(key1)).thenReturn(resultList);
		when(resultList.get(0)).thenReturn(mockEntity);

		//..finally setup what each entity would return
		when(mockEntity.matches("ABC")).thenReturn(true);
		when(mockEntity.matches("ZZZ")).thenReturn(false);
	}

	@Test
	public void isValidId(){
		assertThat(dbXrefEntities.isValidId("ABC"), is(true));
	}

	@Test
	public void isValidIdWhenSupplyingDatabaseAndTypeName(){
		assertThat(dbXrefEntities.isValidId("ABC", "Wormbase", "variation"), is(true));
	}

	@Test
	public void invalidDatabaseAndTypeName(){
		assertThat(dbXrefEntities.isValidId("ABC", "WormbaseX", "variationX"), is(false));
	}

	@Test
	public void isInvalidId(){

		assertThat(dbXrefEntities.isValidId("ZZZ"), is(false));

	}
}
