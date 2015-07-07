package uk.ac.ebi.quickgo.webservice.model;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.web.util.NameURL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 06/01/2015
 * Time: 16:18
 * Created with IntelliJ IDEA.
 */
public class GoAnnotationJson implements Cloneable{
	private String protein;
	private String symbol;
	private String qualifier;
	private String goId;
	private String termName;
	private String aspect;
	private String evidenceGo;
	private String evidenceEco;
	private String reference;
	private List<String> withList;
	private int taxon;
	private String assignedBy;
	private String database;
	private String date;
	private String name;
	private String synonym;
	private String type;
	private String taxonName;
	private int sequence;
	private String originalTermId;
	private String originalTermName;


	//slimming stuff
//	private static final long serialVersionUID = -4850838934084333982L;
//
//	Annotation annotation;
//
//	// Attributes that contain URL
//	NameURL db;
//	NameURL dbObjectID;
//	NameURL goEvidence;
//	NameURL references;
//	//NameURL assignedBy;
//	List<NameURL> with;

	String termIDSlimmingToString;
	NameURL termIDSlimmingTo;
	String termNameSlimmingTo;
	private List<String> extensionList;
	private String fullExtension;
	private String fullWith;


	//----------------------------------------------- Getters and Setters -----------------------------------------

	public String getProtein() {
		return protein;
	}

	public void setProtein(String protein) {
		this.protein = protein;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getGoId() {
		return goId;
	}

	public void setGoId(String goId) {
		this.goId = goId;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getAspect() {
		return aspect;
	}

	public void setAspect(String aspect) {
		this.aspect = aspect;
	}

	public String getEvidenceGo() {
		return evidenceGo;
	}

	public void setEvidenceGo(String evidenceGo) {
		this.evidenceGo = evidenceGo;
	}

	public String getEvidenceEco() {
		return evidenceEco;
	}

	public void setEvidenceEco(String evidenceEco) {
		this.evidenceEco = evidenceEco;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<String> getWithList() {
		return withList;
	}

	public void setWithList(List<String> withList) {
		this.withList = withList;
	}

	public int getTaxon() {
		return taxon;
	}

	public void setTaxon(int taxon) {
		this.taxon = taxon;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTaxonName() {
		return taxonName;
	}

	public void setTaxonName(String taxonName) {
		this.taxonName = taxonName;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getOriginalTermId() {
		return originalTermId;
	}

	public void setOriginalTermId(String originalTermId) {
		this.originalTermId = originalTermId;
	}

	public String getOriginalTermName() {
		return originalTermName;
	}

	public void setOriginalTermName(String originalTermName) {
		this.originalTermName = originalTermName;
	}



	//------------------------------------- Slimming Getters and Setters -----------------------------------------


	public String getTermIDSlimmingToString() {
		return termIDSlimmingToString;
	}

	public void setTermIDSlimmingToString(String termIDSlimmingToString) {
		this.termIDSlimmingToString = termIDSlimmingToString;
	}

	public NameURL getTermIDSlimmingTo() {
		return termIDSlimmingTo;
	}

	public void setTermIDSlimmingTo(NameURL termIDSlimmingTo) {
		this.termIDSlimmingTo = termIDSlimmingTo;
	}

	public String getTermNameSlimmingTo() {
		return termNameSlimmingTo;
	}

	public void setTermNameSlimmingTo(String termNameSlimmingTo) {
		this.termNameSlimmingTo = termNameSlimmingTo;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Object aClone =  super.clone();  //Create shallow copy.

		//Need to copy seperately non-primatives to achieve a deep copy
		List<String> clonesWithList = new ArrayList<>();
		if(this.withList!=null) {
			for (Iterator<String> iterator = withList.iterator();iterator.hasNext();) {
				String next = iterator.next();
				clonesWithList.add(next);
			}
			((GoAnnotationJson) aClone).setWithList(clonesWithList);
		}
		return  aClone;
	}

	public GoAnnotationJson copy() throws CloneNotSupportedException {
		return (GoAnnotationJson)this.clone();
	}

	public void setExtensionList(List<String> extensionList) {
		this.extensionList = extensionList;
	}

	public List<String> getExtensionList() {
		return extensionList;
	}

	public void setFullExtension(String fullExtension) {
		this.fullExtension = fullExtension;
	}

	public String getFullExtension() {
		return fullExtension;
	}

	public void setFullWith(String fullWith) {
		this.fullWith = fullWith;
	}

	public String getFullWith() {
		return fullWith;
	}
}
