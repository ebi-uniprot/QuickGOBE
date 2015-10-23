package uk.ac.ebi.quickgo.output.xml.term.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.ontology.generic.Synonym;
import uk.ac.ebi.quickgo.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.output.xml.model.XMLEntity;
import uk.ac.ebi.quickgo.util.XRef;
import uk.ac.ebi.quickgo.xml.term.*;

/**
 * GO Term XML representation
 * @author cbonill
 *
 */
public class GOTermOBOXML implements XMLEntity {

	Object xmlRepresentation;
	
	public GOTermOBOXML(GOTerm goTerm) {

		ObjectFactory objectFactory = new ObjectFactory();
		
		// Synonyms
		List<Synonymtype> synonymtypes = new ArrayList<Synonymtype>();
		for (Synonym synonym : goTerm.getSynonyms()) {
			Synonymtype synonymtype = objectFactory.createSynonymtype();
			synonymtype.setSynonymText(synonym.name);
			synonymtype.setScope(synonym.type);
			synonymtypes.add(synonymtype);
		}

		
		// Alt Ids
		List<String> altIds = new ArrayList<>();
		for (XRef xRef : goTerm.getAltIds()) {
			altIds.add(xRef.getId());
		}
		
		// Definition
		Deftype deftype = objectFactory.createDeftype();
		deftype.setDefstr(goTerm.getDefinition());

		// Is a
		List<GenericTerm> isATerms = goTerm.getFilteredAncestors(EnumSet.of(uk.ac.ebi.quickgo.ontology.generic.RelationType.ISA));
		List<String> isA = new ArrayList<>();
		for (GenericTerm genericTerm : isATerms) {
			if (genericTerm.getId() != goTerm.getId()) {
				isA.add(genericTerm.getId());
			}
		}
		
		// Xref
		List<Xreftype> xreftypes = new ArrayList<>();
		for (XRef xRef : goTerm.getXrefs()) {
			Xreftype xreftype = objectFactory.createXreftype();
			xreftype.setAcc(xRef.getId());
			xreftype.setDbname(xRef.getDb());
			xreftypes.add(xreftype);
		}

		//Consider
		List<String> termRelations = new ArrayList<>();
		for(TermRelation relation : goTerm.getReplaces()){
			if(relation.typeof.description.equals(RelationType.CONSIDER.description)){
				termRelations.add(relation.parent.getId());
			}
		}
		
		// Relationships
		List<TermRelation> parentTermRelations = goTerm.getAncestors();
		List<Relationshiptype> relations = new ArrayList<>();
		for (TermRelation termRelation : parentTermRelations) {
			if (termRelation.parent.getId() != goTerm.getId() && !termRelation.typeof.description.equals(RelationType.ISA.description)) {
				Relationshiptype relationshiptype = objectFactory.createRelationshiptype();
				relationshiptype.setType(termRelation.typeof.formalCode);
				relationshiptype.setTo(termRelation.parent.getId());
				relations.add(relationshiptype);
			}
		}		
		
		// Term
		Termdeftype termdeftype = objectFactory.createTermdeftype();
		termdeftype.setId(goTerm.getId());
		termdeftype.setName(goTerm.getName());
		termdeftype.setNamespace(goTerm.getNamespace());
		termdeftype.setDef(deftype);
		termdeftype.getComment().add(goTerm.getComment());
		termdeftype.getSynonym().addAll(synonymtypes);
		termdeftype.getAltId().addAll(altIds);
		termdeftype.getConsider().addAll(termRelations);
		termdeftype.getXref().addAll(xreftypes);
		termdeftype.getIsA().addAll(isA);
		termdeftype.getRelationship().addAll(relations);
		
		this.xmlRepresentation = termdeftype;		
	}

	public Object getXmlRepresentation() {
		return xmlRepresentation;
	}

	public void setXmlRepresentation(Object xmlRepresentation) {
		this.xmlRepresentation = xmlRepresentation;
	}
}