package uk.ac.ebi.quickgo.webservice.model;

import uk.ac.ebi.quickgo.ontology.generic.RelationType;

/**
 * @Author Tony Wardell
 * Date: 10/02/2015
 * Time: 16:22
 * Created with IntelliJ IDEA.
 */
public class ChildTermRelationJson {
	private String id;
	private String name;
	private RelationType typeOf;

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}


	public String getName() {
		return name;
	}

	public void setTypeOf(RelationType typeOf) {
		this.typeOf = typeOf;
	}

	public RelationType getTypeOf() {
		return typeOf;
	}
}
