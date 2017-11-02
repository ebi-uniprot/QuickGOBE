package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.Map;

/**
 * Source of header creators, based on the associated file/media type.
 *
 * @author Tony Wardell
 * Date: 22/05/2017
 * Time: 15:46
 * Created with IntelliJ IDEA.
 */
public class HeaderCreatorFactory {

    private final Map<String,HeaderCreator> creatorSource;

    public HeaderCreatorFactory(
            Map<String, HeaderCreator> creatorSource) {
        this.creatorSource = creatorSource;
    }

    public HeaderCreator provide(String type){
        return creatorSource.get(type);
    }
}
