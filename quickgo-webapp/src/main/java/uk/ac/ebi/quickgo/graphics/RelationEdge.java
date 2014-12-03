package uk.ac.ebi.quickgo.graphics;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

import uk.ac.ebi.quickgo.ontology.generic.RelationType;

public class RelationEdge extends DrawableEdge<TermNode> {
	private static final Stroke relationStroke = new BasicStroke(2f);
	private static final Shape arrow = DrawableEdge.standardArrow(8,6,2);

	RelationType type;

	public RelationEdge(TermNode parent, TermNode child, RelationType rtype) {
		super(parent, child, rtype.colour, rtype.stroke == null ? relationStroke : rtype.stroke, (rtype.polarity == RelationType.Polarity.POSITIVE || rtype.polarity == RelationType.Polarity.BIPOLAR) ? arrow : null, (rtype.polarity == RelationType.Polarity.NEGATIVE || rtype.polarity == RelationType.Polarity.BIPOLAR) ? arrow : null);
		this.type = rtype;
	}
}
