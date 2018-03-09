package uk.ac.ebi.quickgo.client.model.presets.evidence;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;

/**
 * A subclass of {@link PresetItem} for Evidences, used to define the GO_EVIDENCE property.
 * @author Tony Wardell
 * Date: 08/03/2018
 * Time: 16:11
 * Created with IntelliJ IDEA.
 */
public class PresetEvidenceItem extends PresetItem {

    private PresetEvidenceItem(Builder builder) {
        super(builder);
    }

    public enum Property {
        GO_EVIDENCE();

        private final String key;

        Property() {
            this.key = "goEvidence";
        }

        public String getKey() {
            return key;
        }
    }
}
