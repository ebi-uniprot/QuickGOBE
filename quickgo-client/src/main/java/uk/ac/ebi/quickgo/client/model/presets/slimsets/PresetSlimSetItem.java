package uk.ac.ebi.quickgo.client.model.presets.slimsets;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;

/**
 * A subclass of {@link PresetItem} for Evidences, used to define the GO_EVIDENCE property.
 */
public class PresetSlimSetItem extends PresetItem {

    private PresetSlimSetItem(Builder builder) {
        super(builder);
    }

    public enum Property {
        ROLE("role"),
        TAX_IDS("taxIds"),
        SHORT_LABEL("shortLabel");

        private final String key;

        Property(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
