package uk.ac.ebi.quickgo.graphics.ontology;

import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTermSet;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static uk.ac.ebi.quickgo.graphics.ontology.GraphPresentation.*;

public class TermNode implements INode, IPositionableNode {
    static final Color defaultBoxHeaderBackgroundColor = new Color(0x00709B);
    static final Color functionGoTermBoxHeaderBgColor = Color.darkGray;
    static final Color componentGoTermBoxHeaderBgColor = new Color(0x93A661);
    private Font font;

    private GenericTerm term;
    private String name;
    private String id;

    private int x;
    private int y;
    private int width;
    private int height;
    public int topLine = 0;

    int[] colours = new int[0];
    private GraphPresentation style;

    public TermNode(String name, String id, GraphPresentation style) {
        this.name = name;
        this.id = id;
        this.style = style;
        if (style.termIds && id.length() > 0) {
            topLine = style.getIdHeaderFontSize();
        }
        height = style.height;
        width = style.width;
        font = style.font;
    }

    public TermNode(GenericTerm term, GraphPresentation style) {
        this(term.getName().replace('_', ' '), term.getId(), style);
        this.term = term;

        if (style.subsetColours) {
            List<GenericTermSet> subsets = term.getSubsets();
            colours = new int[subsets.size()];
            for (int i = 0; i < subsets.size(); i++) {
                colours[i] = subsets.get(i).getColour();
            }
        }
    }

    public GenericTerm getTerm() {
        return term;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int left() {
        return x - (width / 2);
    }

    public int right() {
        return x + (width / 2);
    }

    public int top() {
        return y - (height / 2);
    }

    public int bottom() {
        return y + (height / 2);
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

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    Color fillColour = Color.white;
    Color lineColour = Color.black;

    public void setFillColour(Color c) {
        if (c != null) {
            this.fillColour = c;
        }
    }

    public void render(Graphics2D g2) {
        g2.setFont(font);

        g2.setColor(fillColour);
        g2.fillRect(left(), top(), width, height);

        g2.setColor(lineColour);
        g2.setStroke(style.getBoxBorder());
        g2.drawRect(left(), top(), width, height);

        for (int i = 0; i < colours.length; i++) {
            g2.setColor(new Color(colours[i]));
            g2.fillRect(left() + (i * slimWidth()) + 1,bottom() - slimHeight() + 1, slimWidth(), slimHeight());
        }
        g2.setColor(lineColour);

        FontMetrics fm = g2.getFontMetrics();

        reflow(name, fm, g2);

        int ypos = y - (yheight / 2) + topLine;
        for (TextLine line : lines) {
            line.draw(g2, x, ypos);
            ypos += line.height();
        }

        if (style.termIds) {
            renderID(g2);
        }
    }

    int slimWidth() {
        //for reasonable slim color marker at bottom, width is 10 with default box width of 85
        float dividerToGetReasonableWidth = 8.5f;
        return (int) (style.width / dividerToGetReasonableWidth);
    }

    int slimHeight(){
        //for reasonable slim color marker at bottom, height is 4 with default box height of 55
        float dividerToGetReasonableHeight = 13.75f;
        return (int) (style.height / dividerToGetReasonableHeight);
    }

    public void renderID(Graphics2D g2) {
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(id, g2);

        drawFilledBackgroundForHeaderOfTermBox(g2, r);

        g2.setColor(Color.WHITE);
        g2.drawString(id, (float) (left() + (width - r.getWidth()) / 2), (float) (top() - r.getMinY() + 2));
    }

    private void drawFilledBackgroundForHeaderOfTermBox(Graphics2D g2, Rectangle2D r){
        int widthToCoverOutlineOuter = width + 1;
        g2.setColor(getTermIdBackgroundColor());
        g2.fillRect(left(), top(), widthToCoverOutlineOuter, (int) r.getHeight() + 4);
    }

    static class TextLine {
        String text;
        Rectangle2D bounds;
        private Graphics2D g2;
        private FontMetrics fm;
        private Font f;

        public TextLine(Graphics2D g2, FontMetrics fm) {
            this.g2 = g2;
            this.fm = fm;
            this.f = fm.getFont();
        }

        boolean fit(String text, int from, int to, int width) {
            String t = text.substring(from, to);

            Rectangle2D r = fm.getStringBounds(t, g2);
            if (r.getWidth() > width) {
                return false;
            } else {
                this.text = t;
                this.bounds = r;

                return true;
            }
        }

        public int length() {
            return text == null ? 0 : text.length();
        }

        public void draw(Graphics2D g2, int x, int y) {
            g2.drawString(text, (float) (x - bounds.getWidth() / 2 - bounds.getMinX()), y + f.getSize2D());
        }

        public int height() {
            return f.getSize();
        }
    }

    List<TextLine> lines = new ArrayList<>();
    int yheight;

    private void reflow(String text, FontMetrics fm, Graphics2D g2) {
        int hmargin = 2;

        int start = 0;
        lines.clear();
        yheight = topLine;
        while (start < text.length()) {
            TextLine current = new TextLine(g2, fm);
            int end = start;
            while (end <= text.length() && current.fit(text, start, end, width - hmargin)) {
                end = nextSpace(text, end + 1);
            }

            if (current.length() == 0) {
                end = start;
                while (end <= text.length() && current.fit(text, start, end, width - hmargin)) {
                    end++;
                }
            }

            if ((current.length() == 0) || (yheight + current.height() >= height)) {
                break;
            }

            yheight += current.height();
            lines.add(current);
            start += current.length();
            while (start < text.length() && text.charAt(start) == ' ') {
                start++;
            }
        }
    }

    private int nextSpace(String text, int from) {
        if (from > text.length()) {
            return from;
        } else {
            int ixSpace = text.indexOf(" ", from);
            if (ixSpace == -1) {
                ixSpace = text.length();
            }
            return ixSpace;
        }
    }

    Color getTermIdBackgroundColor() {
        if (isGoTerm()) {
            GOTerm goTerm = (GOTerm) term;
            if (goTerm.getAspect() == GOTerm.EGOAspect.F) {
                return functionGoTermBoxHeaderBgColor;
            }
            else if (goTerm.getAspect() == GOTerm.EGOAspect.C) {
                return componentGoTermBoxHeaderBgColor;
            }
        }
        return defaultBoxHeaderBackgroundColor;
    }

    boolean isGoTerm(){
        return Objects.nonNull(term) && term.isGOTerm() && term instanceof GOTerm;
    }
}
