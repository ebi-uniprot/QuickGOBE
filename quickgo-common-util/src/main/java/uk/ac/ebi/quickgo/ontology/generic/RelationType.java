package uk.ac.ebi.quickgo.ontology.generic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: dbinns Date: 15-Jan-2010 Time: 16:07:23 To
 * change this template use File | Settings | File Templates.
 */
public enum RelationType {
	UNDEFINED("?", "Ancestor", "ancestor"),
	IDENTITY("=", "Identity", "equals"),
	ISA("I", "Is a", "is_a", new Color(0,0,0)),
	PARTOF("P", "Part of", "part_of", new Color(0,0,255)),
	REGULATES("R", "Regulates", "regulates", new Color(255,192,0)),
	POSITIVEREGULATES("+", "Positively regulates", "positively_regulates", "PR", new Color(0,255,0)),
	NEGATIVEREGULATES("-", "Negatively regulates", "negatively_regulates", "NR", new Color(255,0,0)),
	REPLACEDBY(">", "Replaced by", "replaced_by", "replaced_by", new Color(255,0,255)),
	CONSIDER("~", "Consider", "consider", "consider", new Color(192,0,255)),
	HASPART("H", "Has part", "has_part", new Color(128,0,128), Polarity.NEGATIVE, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 3.0f, 3.0f }, 0.0f)),
	OCCURSIN("O", "Occurs in", "occurs_in", "OI", new Color(0,128,128)),
	USEDIN("U", "Used in", "used_in", "UI", new Color(156,102,31)),
	CAPABLEOF("C", "Capable of", "capable_of", "CO", new Color(0,128,255), Polarity.POSITIVE, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 3.0f, 3.0f }, 0.0f)),
	CAPABLEOFPARTOF("<", "Capable of part of", "capable_of_part_of", "CP", new Color(255,128,0), Polarity.POSITIVE, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 3.0f, 3.0f }, 0.0f))
	;

	public enum Polarity {
		POSITIVE, // relation is unidirectional from child to parent
		NEGATIVE, // relation is unidirectional from parent to child
		NEUTRAL, // relation is non-directional
		BIPOLAR // relation is bi-directional
	}

	public String code;
	public String description;
	public String formalCode;
	public String alternativeCode;
	public Color colour;
	public Polarity polarity;
	public Stroke stroke;

	RelationType(String code, String description, String formalCode, String alternativeCode, Color colour, Polarity polarity, Stroke stroke) {
		this.code = code;
		this.description = description;
		this.alternativeCode = alternativeCode;
		this.colour = colour;
		this.stroke = stroke;
		this.polarity = polarity;
		this.formalCode = formalCode;
	}

	RelationType(String code, String description, String formalCode, String alternativeCode, Color colour) {
		this(code, description, formalCode, alternativeCode, colour, Polarity.POSITIVE, new BasicStroke(2f));
	}

	RelationType(String code, String description, String formalCode, Color colour, Polarity polarity, Stroke stroke) {
		this(code, description, formalCode, code, colour, polarity, stroke);
	}

	RelationType(String code, String description, String formalCode, Color colour) {
		this(code, description, formalCode, code, colour, Polarity.POSITIVE, new BasicStroke(2f));
	}

	RelationType(String code, String description, String formalCode) {
		this(code, description, formalCode, code, new Color(0, 0, 0), Polarity.POSITIVE, new BasicStroke(2f));
	}

	boolean ofType(RelationType query) {
		return (query == RelationType.UNDEFINED)
				|| (this == IDENTITY)
				|| (query == this)
				|| (query == REGULATES && (this == POSITIVEREGULATES || this == NEGATIVEREGULATES));
	}

	boolean ofAnyType(EnumSet<RelationType> types) {
		for (RelationType type : types) {
			if (ofType(type)) {
				return true;
			}
		}
		return false;
	}

	public static RelationType byCode(String code) {
		for (RelationType rt : values()) {
			if (rt.description.equals(code) || rt.code.equals(code) || code.equals(rt.alternativeCode)) {
				return rt;
			}
		}
		throw new IllegalArgumentException("No such relation type as " + code);
	}

	public static EnumSet<RelationType> toSet(String types) {
		Set<RelationType> rt = new HashSet<>();
		for (int i = 0; i < types.length(); i++) {
			rt.add(byCode("" + types.charAt(i)));
		}
		return EnumSet.copyOf(rt);
	}

	/**
	 * Given a Set of RelationType values, returns the String representation
	 * @param relations List of RelationType to convert
	 * @return String representation of a set of RelationType objects
	 */
	public static String toCodes(EnumSet<RelationType> relations) {
		String key = "";
		if (relations.contains(ISA)) {
			key = key + ISA.code;
		}
		if (relations.contains(IDENTITY)) {
			key = key + IDENTITY.code;
		}
		if (relations.contains(PARTOF)) {
			key = key + PARTOF.code;
		}
		if (relations.contains(OCCURSIN)) {
			key = key + OCCURSIN.code;
		}
		if (relations.contains(REGULATES)) {
			key = key + REGULATES.code;
		}
		if (relations.contains(POSITIVEREGULATES)) {
			key = key + POSITIVEREGULATES.code;
		}
		if (relations.contains(NEGATIVEREGULATES)) {
			key = key + NEGATIVEREGULATES.code;
		}
		if (relations.contains(USEDIN)) {
			key = key + USEDIN.code;
		}
		return key;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormalCode() {
		return formalCode;
	}

	public void setFormalCode(String formalCode) {
		this.formalCode = formalCode;
	}

	public String getAlternativeCode() {
		return alternativeCode;
	}

	public void setAlternativeCode(String alternativeCode) {
		this.alternativeCode = alternativeCode;
	}

	public Color getColour() {
		return colour;
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}

	public Polarity getPolarity() {
		return polarity;
	}

	public void setPolarity(Polarity polarity) {
		this.polarity = polarity;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}	
}
