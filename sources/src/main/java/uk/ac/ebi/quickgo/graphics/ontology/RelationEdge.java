package uk.ac.ebi.quickgo.graphics.ontology;

import uk.ac.ebi.quickgo.model.ontology.generic.RelationType;

import java.awt.*;

public class RelationEdge extends DrawableEdge<TermNode> {
    private static final Stroke defaultStyleRelation = new BasicStroke(2f);
    private static final Shape arrow = DrawableEdge.standardArrow(8, 6, 2);

    RelationType type;

    public RelationEdge(TermNode parent, TermNode child, RelationType rtype, GraphPresentation style) {
        super(parent, child, rtype.colour, rtype.stroke == null ? style.arrowLineRelativeFont(defaultStyleRelation) : style.arrowLineRelativeFont(rtype.stroke),
                (rtype.polarity == RelationType.Polarity.POSITIVE || rtype.polarity == RelationType.Polarity.BIPOLAR) ?
                        arrow : null,
                (rtype.polarity == RelationType.Polarity.NEGATIVE || rtype.polarity == RelationType.Polarity.BIPOLAR) ?
                        arrow : null, style.getArrowHeadStyle());
        this.type = rtype;
    }
}
