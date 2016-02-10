package uk.ac.ebi.quickgo.model.ontology.go;

/**
 * @Author Tony Wardell
 * Date: 05/01/2016
 * Time: 14:33
 * Extend the black list entry to something that can be saved on a per term basis.
 *
 * Comes from the file TERM_BLACKLIST_ENTRIES.dat
 * GO_ID           CATEGORY                ENTITY_TYPE     ENTITY_ID       TAXON_ID   ENTITY_NAME     ANCESTOR_GO_ID  REASON                                                                                                          METHOD_ID
 * GO:0000001      NOT-qualified manual    protein         A5I1R9          441771     A5I1R9_CLOBH    GO:0007005      1 NOT-qualified manual annotation exists with evidence code ECO:0000318 from this reference: GO_REF:0000033     \N
 * GO:0000001      NOT-qualified manual    protein         B5YH54          289376     B5YH54_THEYD    GO:0007005      1 NOT-qualified manual annotation exists with evidence code ECO:0000318 from this reference: GO_REF:0000033     \N
 * GO:0000001      NOT-qualified manual    protein         O00165          9606       HAX1_HUMAN      GO:0007005      1 NOT-qualified manual annotation exists with evidence code ECO:0000315 from this reference: PMID:17008324      \N
 *
 */
public class GOTermBlacklist extends AnnotationBlacklistEntry{


	private String category, entityType, ancestorGOID, entityName;

	public GOTermBlacklist(String goId, String category,  String entityType, String entityID, String taxonId, String entityName,
						   String ancestorGOID, String reason, String methodId) {
		super(entityID, taxonId, goId, reason, methodId);
		this.entityName=entityName;
		this.category=category;
		this.entityType=entityType;
		this.ancestorGOID=ancestorGOID;

	}

	public String getCategory() {
		return category;
	}

	public String getEntityType() {
		return entityType;
	}

	public String getAncestorGOID() {
		return ancestorGOID;
	}

	public String getEntityName() {	return entityName;	}
}
