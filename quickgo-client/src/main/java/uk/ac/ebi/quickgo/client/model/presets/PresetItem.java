package uk.ac.ebi.quickgo.client.model.presets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * Represents a preset item DTO.
 *
 * Created 15/09/16
 * @author Edd
 */
public interface PresetItem {
    String getId();

    String getName();

    String getDescription();

    @JsonIgnore Integer getRelevancy();

    String getUrl();

    List<String> getAssociations();

}
