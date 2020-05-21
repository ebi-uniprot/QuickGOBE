package uk.ac.ebi.quickgo.graphics.ontology;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class DrawableEdge<N extends INode> implements IRoutableEdge<N> {
    N parent;
    N child;

    protected Shape route;
    protected Color colour;
    Stroke arrowLineStyle;

    Shape parentArrowHead;
    Shape childArrowHead;
    private final Stroke arrowStroke;

    public static Shape standardArrow(float length, float width, float inset) {
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo(width / 2, length);
        arrow.lineTo(0, 0);
        arrow.lineTo(-width / 2, length);
        arrow.lineTo(0, length - inset);
        arrow.closePath();
        return arrow;
    }

    public static Shape standardArrow(float length, float width) {
        return standardArrow(length, width, 0);
    }

    public static Shape standardArrow(float length) {
        return standardArrow(length, length / 2, 0);
    }

    public DrawableEdge(N parent, N child, Color colour, Stroke arrowLineStyle, Shape parentArrowHead, Shape childArrowHead, Stroke arrowHeadStyle) {
        this.parent = parent;
        this.child = child;
        this.colour = colour;
        this.arrowLineStyle = arrowLineStyle;
        this.parentArrowHead = parentArrowHead;
        this.childArrowHead = childArrowHead;
        this.arrowStroke = arrowHeadStyle;
    }

    public void setParentArrowHead(Shape parentArrowHead) {
        this.parentArrowHead = parentArrowHead;
    }

    public void setChildArrowHead(Shape childArrowHead) {
        this.childArrowHead = childArrowHead;
    }

    /**
     * implementation of IRoutableEdge
     */
    @Override
    public N getParent() {
        return parent;
    }

    @Override
    public N getChild() {
        return child;
    }

    @Override
    public void setRoute(Shape route) {
        this.route = route;
    }

    /**
     * Draw the edge
     *
     * @param g2 Canvas
     */
    public void render(Graphics2D g2) {
        g2.setStroke(arrowLineStyle);
        g2.setColor(colour);

        g2.draw(route);

        g2.setStroke(arrowStroke);
        if (parentArrowHead != null || childArrowHead != null) {
            drawArrows(g2, route, parentArrowHead, childArrowHead);
        }
    }

    public static void drawArrows(Graphics2D g2, Shape route, Shape parentArrow, Shape childArrow) {
        PathIterator pi = route.getPathIterator(null, 2);

        double[] posn = new double[6];
        pi.currentSegment(posn);
        double x1, y1, xd1 = 0, yd1 = 0, x2, y2, xd2 = 0, yd2 = 0;
        boolean initial = true;
        x1 = x2 = posn[0];
        y1 = y2 = posn[1];
        pi.next();
        while (!pi.isDone()) {
            int type = pi.currentSegment(posn);
            double x = posn[0];
            double y = posn[1];
            if (type == PathIterator.SEG_CLOSE) {
                x = x1;
                y = y1;
            }

            pi.next();

            if (initial) {
                xd1 = x - x1;
                yd1 = y - y1;
                if (xd1 != 0 || xd2 != 0) {
                    initial = false;
                }
            }

            if (x2 != posn[0] || y2 != posn[1]) {
                xd2 = x2 - x;
                yd2 = y2 - y;
            }

            x2 = x;
            y2 = y;
        }

        if (parentArrow != null) {
            drawArrow(g2, x1, y1, xd1, yd1, parentArrow);
        }
        if (childArrow != null) {
            drawArrow(g2, x2, y2, xd2, yd2, childArrow);
        }
    }

    private static void drawArrow(Graphics2D g2, double x, double y, double xd, double yd, Shape arrowhead) {
        if (xd != 0 || yd != 0) {
            AffineTransform saveXform = g2.getTransform();

            g2.translate(x, y);
            g2.rotate(-Math.atan2(xd, yd));

            g2.fill(arrowhead);
            g2.draw(arrowhead);

            g2.setTransform(saveXform);
        }
    }
}
