package uk.ac.ebi.quickgo.web.util.term;

import uk.ac.ebi.quickgo.web.util.NameURL;
/**
 * To represent terms Xrefs
 * 
 * @author cbonill
 * 
 */
public class XRefBean {

	private String xrefDB;
	private String xrefID;
	private NameURL xrefDescription;

	public XRefBean(String xrefDB, String xrefID, NameURL xrefDescription) {
		this.xrefDB = xrefDB;
		this.xrefID = xrefID;
		this.xrefDescription = xrefDescription;
	}

	public String getXrefDB() {
		return xrefDB;
	}

	public void setXrefDB(String xrefDB) {
		this.xrefDB = xrefDB;
	}

	public NameURL getXrefDescription() {
		return xrefDescription;
	}

	public void setXrefDescription(NameURL xrefDescription) {
		this.xrefDescription = xrefDescription;
	}

	public String getXrefID() {
		return xrefID;
	}

	public void setXrefID(String xrefID) {
		this.xrefID = xrefID;
	}

}