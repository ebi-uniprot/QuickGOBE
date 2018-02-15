package uk.ac.ebi.quickgo.graphics.ontology;

import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTermSet;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.quickgo.graphics.ontology.GraphPresentation.*;

public class TermNode implements INode, IPositionableNode {
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
            topLine = fontSize + 1;
        }
        height = style.height;
        width = style.width;
        font = FONT;
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

    Color fillColour = Color.white;
    Color lineColour = Color.black;
    Stroke border = new BasicStroke(1);

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
        g2.setStroke(border);
        g2.drawRect(left(), top(), width, height);

        for (int i = 0; i < colours.length; i++) {
            g2.setColor(new Color(colours[i]));
            g2.fillRect(left() + (i * 10) + 1, bottom() - 3, 10, 4);
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

    // Term id background colour
    Color idColour = new Color(0x00709B);

    public void renderID(Graphics2D g2) {
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(id, g2);

        g2.setColor(idColour);
        g2.fillRect(left(), top(), width, (int) r.getHeight() + 4);

        g2.setColor(Color.WHITE);
        g2.drawString(id, (float) (left() + (width - r.getWidth()) / 2), (float) (top() - r.getMinY() + 2));
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
}
