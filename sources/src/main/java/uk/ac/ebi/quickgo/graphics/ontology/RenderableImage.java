package uk.ac.ebi.quickgo.graphics.ontology;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

public abstract class RenderableImage {
    public int width;
    public int height;

    String src;

    public RenderedImage render() {
        BufferedImage image = prepare();
        final Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.white);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.black);
        render(g2);

        return image;
    }

    protected void render(Graphics2D g2) {}

    protected BufferedImage prepare() {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    protected RenderableImage(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
