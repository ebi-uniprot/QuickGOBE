package uk.ac.ebi.quickgo.graphics.ontology;

import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTermSet;
import uk.ac.ebi.quickgo.model.ontology.generic.RelationType;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GraphImage extends RenderableImage {
    public static final int keyMargin = 50;
    public static final int rightMargin = 10;
    public static final int bottomMargin = 16;
    public static final int minWidth = 250;
    private static final Font labelFont = new Font("Arial", Font.PLAIN, 10);
    private static final Font infoFont = new Font("Arial", Font.PLAIN, 9);
    private static final Font errorFont = new Font("Arial", Font.PLAIN, 16);
    public final String errorMessage;
    public Collection<TermNode> terms = new ArrayList<>();
    public Collection<RelationEdge> relations = new ArrayList<>();
    public Collection<KeyNode> legend = new ArrayList<>();
    public String selected;
    private GraphPresentation style;
    public GraphImage(String errorMessage) {
        super(500, 100);
        this.errorMessage = errorMessage;
    }
    public GraphImage(int width, int height, Collection<TermNode> terms, Collection<RelationEdge> relations,
            GraphPresentation style, Collection<RelationType> relationTypes) {
        super(Math.max(minWidth, width + (style.key ? keyMargin + (style.width * 2) + rightMargin : 0)),
                height + bottomMargin);

        this.errorMessage = null;

        this.terms = terms;
        this.relations = relations;
        this.style = style;

        if (style.key) {
            Set<GenericTermSet> subsets = new HashSet<>();
            if (style.subsetColours) {
                for (TermNode node : terms) {
                    GenericTerm t = node.getTerm();
                    if (t != null) {
                        subsets.addAll(t.subsets);
                    }
                }
            }

            //	        RelationType rta[] = {RelationType.ISA,  RelationType.PARTOF, /*RelationType.HASPART,*/
            // RelationType.REGULATES, RelationType.POSITIVEREGULATES, RelationType.NEGATIVEREGULATES, RelationType
            // .OCCURSIN, RelationType.USEDIN, RelationType.CAPABLEOF, RelationType.CAPABLEOFPARTOF};

            int knHeight = style.height / 2;
            int knY = knHeight;

            int yMax = knY;
            for (RelationType rt : relationTypes) {
                KeyNode kn = new KeyNode(super.width - style.width - rightMargin, knY, style.width * 2, knHeight,
                        rt);
                legend.add(kn);
                knY += knHeight;
                yMax = kn.bottom();
            }

            int pos = 20;
            for (GenericTermSet subset : subsets) {
                TermNode bottomNode = subsetNode(subset.name, subset.name, pos);
                bottomNode.colours = new int[]{subset.colour};
                pos += 3;
                yMax = bottomNode.bottom();
            }

            int bottom = yMax + bottomMargin;
            if (this.height < bottom) {
                this.height = bottom;
            }
        }
    }

    public String id() {
        return String.valueOf(System.identityHashCode(this));
    }

    /**
     * Return only GO/ECO terms nodes (no goslim ones)
     * @return GO/ECO terms nodes (no goslim ones)
     */
    public Collection<TermNode> getOntologyTerms() {
        Collection<TermNode> ontologyTerms = new ArrayList<>();
        for (TermNode termNode : this.terms) {
            if (!termNode.getId().contains("goslim")) {
                ontologyTerms.add(termNode);
            }
        }
        return ontologyTerms;
    }

    @Override
    protected void render(Graphics2D g2) {
        if (errorMessage != null) {
            g2.setFont(errorFont);
            g2.setColor(Color.BLACK);
            g2.drawString(errorMessage, 5, 50);
        } else {
            for (RelationEdge relation : relations) {
                relation.render(g2);
            }
            for (TermNode term : terms) {
                term.render(g2);
            }
            for (KeyNode ke : legend) {
                ke.render(g2);
            }

            g2.setFont(infoFont);
            g2.setColor(Color.BLACK);
            g2.drawString("QuickGO - https://www.ebi.ac.uk/QuickGO", 5, height - g2.getFontMetrics().getDescent());
        }
    }

    private TermNode subsetNode(String name, String id, int row) {
        TermNode node = new TermNode(name, id, style);
        node.setWidth(node.getWidth() + 26);
        node.setHeight(node.getHeight() / 2);
        node.setLocation(width - node.getWidth() / 2 - rightMargin, node.getHeight() * row / 2);
        terms.add(node);
        return node;
    }

    public static class KeyNode implements INode {
        public int xCentre;
        public int yCentre;
        public int width;
        public int height;
        RelationType relType;
        Stroke border = new BasicStroke(1);

        public KeyNode(int xCentre, int yCentre, int width, int height, RelationType relType) {
            this.xCentre = xCentre;
            this.yCentre = yCentre;
            this.width = width;
            this.height = height;
            this.relType = relType;
        }

        public void render(Graphics2D g2) {
            int margin = height / 10;
            int boxSide = height - (2 * margin);
            int offsetY = boxSide / 4;
            new RelationStroke(xCentre + (width / 2) - boxSide - (2 * margin), yCentre + offsetY,
                    xCentre - (width / 2) + boxSide + (2 * margin), yCentre + offsetY, relType).render(g2);

            int left = xCentre - (width / 2) + margin;
            int top = yCentre - (height / 2) + margin;
            drawBox(g2, left, top, boxSide, boxSide, "A");
            left += (width - margin - boxSide);
            drawBox(g2, left, top, boxSide, boxSide, "B");

            g2.setFont(labelFont);
            Rectangle2D r = g2.getFontMetrics().getStringBounds(relType.description, g2);
            g2.drawString(relType.description, (float) (xCentre - (r.getWidth() / 2)),
                    (float) (yCentre + offsetY - (r.getHeight() / 2)));
        }

        public int left() {
            return xCentre - width / 2;
        }

        public int right() {
            return xCentre + width / 2;
        }

        public int top() {
            return yCentre - height / 2;
        }

        public int bottom() {
            return yCentre + height / 2;
        }

        public int getLeft() {
            return this.left();
        }

        public int getRight() {
            return this.right();
        }

        public int getTop() {
            return this.top();
        }

        public int getBottom() {
            return this.bottom();
        }

        void drawBox(Graphics2D g2, int left, int top, int width, int height, String label) {
            g2.setColor(Color.black);
            g2.setStroke(border);
            g2.drawRect(left, top, width, height);

            g2.setFont(labelFont);
            Rectangle2D r = g2.getFontMetrics().getStringBounds(label, g2);
            g2.drawString(label, (float) (left + (width / 2) - (r.getWidth() / 2)),
                    (float) (top + (height / 2) + (r.getHeight() / 2)));
        }

        public static class RelationStroke extends DrawableEdge<INode> {
            private static final Stroke relationStroke = new BasicStroke(2f);
            private static final Shape arrow = DrawableEdge.standardArrow(8, 6, 2);

            RelationType type;

            public RelationStroke(int xFrom, int yFrom, int xTo, int yTo, RelationType rtype) {
                super(null, null, Color.black, rtype.stroke == null ? relationStroke : rtype.stroke,
                        (rtype.polarity == RelationType.Polarity.POSITIVE ||
                                 rtype.polarity == RelationType.Polarity.BIPOLAR) ? arrow : null,
                        (rtype.polarity == RelationType.Polarity.NEGATIVE ||
                                 rtype.polarity == RelationType.Polarity.BIPOLAR) ? arrow : null);
                this.type = rtype;
                this.colour = rtype.colour;

                GeneralPath shape = new GeneralPath();
                shape.moveTo(xFrom, yFrom);
                shape.lineTo(xTo, yTo);
                this.setRoute(shape);
            }
        }
    }
}
