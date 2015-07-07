package uk.ac.ebi.quickgo.webservice.model;

/**
 * @Author Tony Wardell
 * Date: 06/03/2015
 * Time: 16:38
 * Created with IntelliJ IDEA.
 */
public class DBJson {
	private String dbId;
	private String xrefDatabase;

	public DBJson(String dbId, String xrefDatabase) {

		this.dbId = dbId;
		this.xrefDatabase = xrefDatabase;
	}

	public String getDbId() {
		return dbId;
	}

	public String getXrefDatabase() {
		return xrefDatabase;
	}
}
