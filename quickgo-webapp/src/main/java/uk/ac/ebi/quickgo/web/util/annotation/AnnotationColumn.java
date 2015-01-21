package uk.ac.ebi.quickgo.web.util.annotation;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.EGOAspect;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.FileService;
import uk.ac.ebi.quickgo.web.util.url.AnnotationTotal;
import uk.ac.ebi.quickgo.web.util.url.JsonClass;

/**
 * Enum with the annotation columns that can be displayed
 * @author cbonill
 *
 */
public enum AnnotationColumn {
	//Selected by default
	PROTEIN("dbObjectID","Gene Product ID","protein",true,true),
	SYMBOL("dbObjectSymbol","Symbol","symbol",true,false),
	QUALIFIER("qualifiers","Qualifier","qualifier",true,false),
	GOID("termIDSlimmingTo","GO Identifier","goId",true,true),
	TERMNAME("termNameSlimmingTo","GO Term Name","termname",true,false),
	ASPECT("goAspect","Aspect","aspect",true,false),
	EVIDENCE("goEvidence","Evidence","evidence",true,true),
	REFERENCE("references","Reference","reference",true,true),
	WITH("with","With","with",true,true),
	TAXON("taxonomyId","Taxon","taxon",true,true),
	ASSIGNEDBY("assignedBy","Assigned By","assignedby",true,true),
	EXTENSION("extension","Annotation Extension","extension",true,false),

	//Unselected by default
	DATABASE("db","Database","database",false,true),
	DATE("date","Date","date",false,false),
	NAME("dbObjectName","Name","name",false,false),
	SYNONYM("dbObjectSynonyms","Synonym","synonym",false,false),
	TYPE("dbObjectType","Type","type",false,false),
	TAXONNAME("taxonomyName","Taxonomy name","taxonname",false,false),
	SEQUENCE("sequenceLength","Sequence","sequence",false,false),
	ORIGINALTERMID("goID","Original Term ID","originaltermid",false,true),
	ORIGINALTERMNAME("termName","Original Term Name","originaltermname",false,false);

	private String id;
	private String description;
	private String name;
	private boolean checkedByDefault;
	private boolean showURL;

	private AnnotationColumn(String id, String description, String name, boolean checkedByDefault, boolean showURL){
		this.id= id;
		this.description = description;
		this.name = name;
		this.checkedByDefault = checkedByDefault;
		this.showURL = showURL;
	}

	public static AnnotationColumn[] getAnnotationHeaders(String[] values){
		if(values.length == 1 && values[0].equals("")){//Return the ones checked by default
			List<AnnotationColumn> annotationHeadersList = new ArrayList<>();
			for(AnnotationColumn annotationHeader : AnnotationColumn.values()){
				if(annotationHeader.isCheckedByDefault()){
					annotationHeadersList.add(annotationHeader);
				}
			}
			return annotationHeadersList.toArray(new AnnotationColumn[annotationHeadersList.size()]);
		} else {
			List<AnnotationColumn> annotationHeaders = new ArrayList<>();
			for(String value: values){
				annotationHeaders.add(AnnotationColumn.valueOf(value.toUpperCase()));
			}
			return annotationHeaders.toArray(new AnnotationColumn[annotationHeaders.size()]);
		}
	}

	public static AnnotationColumn[] sort(AnnotationColumn[] list){
		List<AnnotationColumn> result = new ArrayList<>();
		result.addAll(Arrays.asList(list));
		List<AnnotationColumn> unsorted = new ArrayList<>();
		for(AnnotationColumn annotationHeader : AnnotationColumn.values()){
			if(!result.contains(annotationHeader)){
				unsorted.add(annotationHeader);
			}
		}
		result.addAll(unsorted);
		return result.toArray(new AnnotationColumn[result.size()]);
	}

	/**
	 * Get annotation column from ID
	 * @param id Annotation column ID
	 * @return AnnotationColumn object
	 */
	public static AnnotationColumn fromID(String id) {
		// For closures is used the GO Id field
		if (id.equals(AnnotationField.ANCESTORSIPO.getValue())) {
			return GOID;
		} else if (id.equals(AnnotationField.TAXONOMYCLOSURE.getValue())) {
			return TAXON;
		}
		for (AnnotationColumn annotationColumn : values()) {
			if (annotationColumn.getId().equalsIgnoreCase(id)) {
				return annotationColumn;
			}
		}
		return null;
	}

	/**
	 * Given an annotation and a list of columns, return all the annotation fields for those columns
	 * @throws Exception
	 */
	public static String getAnnotationColumns(FileService.FILE_FORMAT format, Annotation annotation, AnnotationColumn[] columns, String separator) throws Exception{
		String annotationString = "";
		for(AnnotationColumn annotationColumn : columns){
			switch(annotationColumn){
				case PROTEIN:
					annotationString = annotationString + annotation.getDbObjectID() + separator;
					break;
				case SYMBOL:
					annotationString = annotationString + annotation.getDbObjectSymbol() + separator;
					break;
				case QUALIFIER:
					String qualifierString = "";
					if(annotation.getQualifiers() != null){
						qualifierString = StringUtils.arrayToDelimitedString(annotation.getQualifiers().toArray(), "|");
					}
					annotationString = annotationString + qualifierString + separator;
					break;
				case GOID:
					annotationString = annotationString + annotation.getGoID() + separator;
					break;
				case TERMNAME:
					annotationString = annotationString + annotation.getTermName() + separator;
					break;
				case ASPECT:
					annotationString = annotationString + EGOAspect.fromString(annotation.getGoAspect()).abbreviation + separator;
					break;
				case EVIDENCE:
					if(format == FileService.FILE_FORMAT.GAF){
						annotationString = annotationString + annotation.getGoEvidence() + separator;
					} else if(format == FileService.FILE_FORMAT.GPAD){
						annotationString = annotationString + annotation.getEcoID() + separator;
					}
					break;
				case REFERENCE:
					String referenceString = "";
					if (annotation.getReferences() != null) {
						referenceString = StringUtils.arrayToDelimitedString(annotation.getReferences().toArray(), "|");
					}
					annotationString = annotationString + referenceString + separator;
					break;
				case WITH:
					String withString = "";
					if (annotation.getWith() != null) {
						withString = StringUtils.arrayToDelimitedString(annotation.getWith().toArray(), "|");
					}
					annotationString = annotationString + withString + separator;
					break;
				case TAXON:
					annotationString = annotationString + annotation.getTaxonomyId() + separator;
					break;
				case ASSIGNEDBY:
					annotationString = annotationString + annotation.getAssignedBy() + separator;
					break;
				case DATABASE:
					annotationString = annotationString + annotation.getDb() + separator;
					break;
				case DATE:
					annotationString = annotationString + annotation.getDate() + separator;
					break;
				case NAME:
					annotationString = annotationString + annotation.getDbObjectName() + separator;
					break;
				case SYNONYM:
					String synonymString = "";
					if(annotation.getDbObjectSynonyms() != null){
						synonymString = StringUtils.arrayToDelimitedString(annotation.getDbObjectSynonyms().toArray(), "|");
					}
					annotationString = annotationString + synonymString + separator;
					break;
				case TYPE:
					annotationString = annotationString + annotation.getDbObjectType() + separator;
					break;
				case TAXONNAME:
					annotationString = annotationString + annotation.getTaxonomyName() + separator;
					break;
				case SEQUENCE:
					annotationString = annotationString + annotation.getSequenceLength() + separator;
					break;
				case ORIGINALTERMID:
					annotationString = annotationString + annotation.getGoID() + separator;
					break;
				case ORIGINALTERMNAME:
					annotationString = annotationString + annotation.getTermName() + separator;
					break;
				default:
					break;
			}
		}
		return annotationString;
	}

	public static String getAnnotationColumnsForJson(Annotation annotation, AnnotationColumn[] columns, JsonClass jsonClass) throws Exception{

		for(AnnotationColumn annotationColumn : columns){

			switch(annotationColumn){
				case PROTEIN:
					jsonClass.setProtein(annotation.getDbObjectID());
					break;
				case SYMBOL:
					jsonClass.setSymbol(annotation.getDbObjectSymbol());
					break;
				case QUALIFIER:
					String qualifierString = "";
					if(annotation.getQualifiers() != null){
						qualifierString = StringUtils.arrayToDelimitedString(annotation.getQualifiers().toArray(), "|");
					}
					jsonClass.setQualifier(qualifierString);
					break;
				case GOID:
					jsonClass.setGoId(annotation.getGoID());
					break;
				case TERMNAME:
					jsonClass.setTermName(annotation.getTermName());
					break;
				case ASPECT:
					jsonClass.setAspect(EGOAspect.fromString(annotation.getGoAspect()).abbreviation);
					break;
				case EVIDENCE:
//					if(format == FileService.FILE_FORMAT.GAF){
//						annotationString = annotationString + annotation.getGoEvidence() + separator;
//					} else if(format == FileService.FILE_FORMAT.GPAD){
//						annotationString = annotationString + annotation.getEcoID() + separator;
//					}
					jsonClass.setEvidenceGo(annotation.getGoEvidence());
					jsonClass.setEvidenceEco(annotation.getEcoID());
					break;
				case REFERENCE:
					String referenceString = "";
					if (annotation.getReferences() != null) {
						referenceString = StringUtils.arrayToDelimitedString(annotation.getReferences().toArray(), "|");
					}
					jsonClass.setReference(referenceString);
					break;
				case WITH:
					String withString = "";
					if (annotation.getWith() != null) {
						jsonClass.setWithList(annotation.getWith());
					}

					break;
				case TAXON:
					jsonClass.setTaxon(annotation.getTaxonomyId());
					break;
				case ASSIGNEDBY:
					jsonClass.setAssignedBy(annotation.getAssignedBy());
					break;
				case DATABASE:
					jsonClass.setDatabase(annotation.getDb());
					break;
				case DATE:
					jsonClass.setDate(annotation.getDate());
					break;
				case NAME:
					jsonClass.setName(annotation.getDbObjectName());
					break;
				case SYNONYM:
					String synonymString = "";
					if(annotation.getDbObjectSynonyms() != null){
						synonymString = StringUtils.arrayToDelimitedString(annotation.getDbObjectSynonyms().toArray(), "|");
					}
					jsonClass.setSynonym(synonymString);
					break;
				case TYPE:
					jsonClass.setType(annotation.getDbObjectType());
				break;
				case TAXONNAME:
					jsonClass.setTaxonName(annotation.getTaxonomyName());
					break;
				case SEQUENCE:
					jsonClass.setSequence(annotation.getSequenceLength());
					break;
				case ORIGINALTERMID:
					jsonClass.setOriginalTermId(annotation.getGoID());
					break;
				case ORIGINALTERMNAME:
					jsonClass.setOriginalTermName(annotation.getTermName());
					break;
				default:
					break;
			}
		}
		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(writer, jsonClass);
		return writer.toString();
	}


	public static String getAnnotationTotalInJson(long annotationTotal, AnnotationTotal totalObj) throws Exception{
		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		totalObj.setTotal(annotationTotal);
		mapper.writeValue(writer, totalObj);
		return writer.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCheckedByDefault() {
		return checkedByDefault;
	}

	public void setCheckedByDefault(boolean checkedByDefault) {
		this.checkedByDefault = checkedByDefault;
	}

	public boolean isShowURL() {
		return showURL;
	}

	public void setShowURL(boolean showURL) {
		this.showURL = showURL;
	}

}
