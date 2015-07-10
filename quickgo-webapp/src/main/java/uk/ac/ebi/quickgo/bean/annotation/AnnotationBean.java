package uk.ac.ebi.quickgo.bean.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.ontology.go.GOTerm.EGOAspect;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.web.util.NameURL;

/**
 * Wrapper class for annotations. This class is used at web level
 * @author cbonill
 *
 */
public class AnnotationBean implements Serializable{

	private static final long serialVersionUID = -4850838934084333982L;

	GOAnnotation annotation;

	// Attributes that contain URL
	NameURL db;
	NameURL dbObjectID;
	NameURL goEvidence;
	NameURL references;
	NameURL assignedBy;
	List<NameURL> with;

	String termIDSlimmingToString;
	NameURL termIDSlimmingTo;
	String termNameSlimmingTo;

	public AnnotationBean(GOAnnotation annotation) {
		this.annotation = annotation;
	}

	public NameURL getDb() {
		return this.db;
	}

	public void setDb(NameURL db) {
		this.db = db;
	}

	public NameURL getGoEvidence() {
		return new NameURL(annotation.getGoEvidence(), "http://www.geneontology.org/GO.evidence.shtml#" + annotation.getGoEvidence().toLowerCase());
	}

	public NameURL getDbObjectID() {
		return this.dbObjectID;
	}

	public void setDbObjectID(NameURL dbObjectID) {
		this.dbObjectID = dbObjectID;
	}

	public String getDbObjectSymbol() {
		return annotation.getDbObjectSymbol();
	}

	public String getDbObjectName() {
		return annotation.getDbObjectName();
	}

	public String getDbObjectType() {
		return annotation.getDbObjectType();
	}

	public String getDbObjectSynonyms() {
		if (annotation.getDbObjectSynonyms() != null) {
			return StringUtils.arrayToDelimitedString(annotation.getDbObjectSynonyms().toArray(), " | ");
		}
		return "";
	}

	public NameURL getGoID() {
		return new NameURL(annotation.getGoID(), "");
	}

	public String getEcoID() {
		return annotation.getEcoID();
	}

	public String getTermName() {
		return annotation.getTermName();
	}

	public NameURL getAssignedBy() {
		return this.assignedBy;
	}

	public void setAssignedBy(NameURL assignedBy) {
		this.assignedBy = assignedBy;
	}

	public NameURL getReferences() {
		return this.references;
	}

	public void setReferences(NameURL references) {
		this.references = references;
	}

	public List<NameURL> getWith() {
		return this.with;
	}

	public void setWith(List<NameURL> with) {
		this.with = with;
	}

	public String getQualifiers() {
		return annotation.getQualifier();
	}

	public String getInteractingTaxID() {
		return annotation.getInteractingTaxID();
	}

	public String getDate() {
		return annotation.getDate();
	}

	public List<String> getExtensions() {
		return annotation.getExtension();
	}

	public String getProperties() {
		return annotation.getProperties();
	}

	public List<String> getAncestorsI() {
		return annotation.getAncestorsI();
	}

	public List<String> getAncestorsIPO() {
		return annotation.getAncestorsIPO();
	}

	public List<String> getAncestorsIPOR() {
		return annotation.getAncestorsIPOR();
	}

	public List<String> getTargetSets() {
		return annotation.getTargetSets();
	}

	public String getGoAspect() {
		try {
			return EGOAspect.fromString(annotation.getGoAspect()).abbreviation;
		} catch (Exception e) {
			return "";
		}
	}

	public NameURL getTaxonomyId() {
		return new NameURL(String.valueOf(annotation.getTaxonomyId()), "http://www.uniprot.org/taxonomy/" + annotation.getTaxonomyId());
	}

	public String getTaxonomyName() {
		return annotation.getTaxonomyName();
	}

	public List<Integer> getTaxonomyClosure() {
		return annotation.getTaxonomyClosure();
	}

	public int getSequenceLength() {
		return annotation.getSequenceLength();
	}

	public GOAnnotation getAnnotation() {
		return annotation;
	}

	public NameURL getTermIDSlimmingTo() {
		return new NameURL(termIDSlimmingToString, "");
	}

	public String getTermNameSlimmingTo() {
		return termNameSlimmingTo;
	}

	public void setTermNameSlimmingTo(String termNameSlimmingTo) {
		this.termNameSlimmingTo = termNameSlimmingTo;
	}

	public void setTermIDSlimmingToString(String termIDSlimmingToString) {
		this.termIDSlimmingToString = termIDSlimmingToString;
	}

	public String getExtension() {
		List<String> formattedExtensions = new ArrayList<>();
		if (annotation.getExtension() != null) {
			for (String extension : annotation.getExtension()) {
				if (extension.contains(",")) {// Multiple extensions
					String[] extensionsByComma = extension.split(",");
					List<String> formatted = new ArrayList<>();
					for (String ext : extensionsByComma) {
						formatted.add(formatExtension(ext));
					}
					formattedExtensions.add(StringUtils.arrayToDelimitedString(formatted.toArray(), ","));
				} else {// 1 extension
					formattedExtensions.add(formatExtension(extension));
				}
			}
			return StringUtils.arrayToDelimitedString(
					formattedExtensions.toArray(), "|");
		}
		return "";
	}

	/**
	 * Format an annotation extension
	 * @param extension Extension to format
	 * @return Formatted extension
	 */
	private String formatExtension(String extension){
		int parenthesis1 = extension.indexOf("(");
		int parenthesis2 = extension.indexOf(")");
		return extension.substring(0,parenthesis1) + " " + extension.substring(parenthesis1 + 1,parenthesis2);
	}
}
