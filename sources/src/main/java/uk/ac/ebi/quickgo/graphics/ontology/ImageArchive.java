package uk.ac.ebi.quickgo.graphics.ontology;

import java.util.HashMap;
import java.util.Map;

public class ImageArchive {
    //static WeakValueMap<Integer, RenderableImage> content;
    private static Map<Integer, RenderableImage> content;

    private static final String imageServletAddress = "graphs";

    public static String store(RenderableImage image) {
        if (content == null) {
            content = new HashMap<>();
        }
        int id;
        id = image.hashCode();
        content.put(id, image);

        String src = imageServletAddress + "?id=" + id;
        image.src = src;
        return src;
    }

    public static RenderableImage get(int id) {
        return content.get(id);
    }

	/*public static WeakValueMap<Integer, RenderableImage> getContent() {
        return content;
	}*/

    public static Map<Integer, RenderableImage> getContent() {
        return content;
    }
}
