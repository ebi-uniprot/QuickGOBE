package uk.ac.ebi.quickgo.geneproduct.model;

import java.util.List;
import java.util.Map;

/**
 *
 * Holds the entries from 'DB_XREFS_ENTITIES.dat.gz'
 * Ask this class if the id is a valid gene product id.
 * Can either use defaults or supply the identifying database and typeName (protein or gene).
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 13:36
 *         Created with IntelliJ IDEA.
 */
public class DbXrefEntities {

	private final String defaultTypeName;
	private Map<GeneProductXrefEntity.Key,  List<GeneProductXrefEntity>> geneProductXrefEntities;
	private final String defaultDatabase;

	public DbXrefEntities(Map<GeneProductXrefEntity.Key, List<GeneProductXrefEntity>> geneProductXrefEntities, String defaultDb, String defaultTypeName) {
		this.geneProductXrefEntities = geneProductXrefEntities;
		this.defaultDatabase = defaultDb;
		this.defaultTypeName = defaultTypeName;
	}


	public boolean isValidId(String id ) {

		//Use the default database and
		return isValidId( id, defaultDatabase, defaultTypeName );
	}

	public boolean isValidId(String id, String database, String typeName ) {

		//If we haven't managed to load the validation regular expression then pass everything
		if(geneProductXrefEntities==null) return true;

		//Create key to do lookup
		GeneProductXrefEntity.Key key = GeneProductXrefEntity.key(database, typeName);
		final List<GeneProductXrefEntity> geneProductXrefEntities = this.geneProductXrefEntities.get(key);

		//If there is no entity for this combination then the id cannot be correct..
		if(geneProductXrefEntities==null) return false;

		//..otherwise look up the id
		return geneProductXrefEntities.get(0).matches(id);

	}
}
