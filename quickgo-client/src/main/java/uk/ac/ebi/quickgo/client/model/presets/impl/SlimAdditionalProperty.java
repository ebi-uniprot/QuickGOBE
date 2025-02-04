package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;

/**
 * Additional properties used in {@link PresetItem}s when representing GO slims.
 *
 * Created 07/12/16
 * @author Edd
 */
enum SlimAdditionalProperty {
    NAME("name"), ASPECT("aspect"), ROLE("role"), TAX_IDS("taxIds"), SHORT_LABEL("shortLabel");

    private final String key;

    SlimAdditionalProperty(String key) {
        this.key = key;
    }

    String getKey() {
        return key;
    }
}
