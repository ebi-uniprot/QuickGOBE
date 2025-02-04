package uk.ac.ebi.quickgo.client.service.loader.presets.slimsets;

import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
/**
 * A specialisation of {@link RawNamedPreset} that includes data for the slim set preset.
 */
public class RawSlimSetNamedPreset extends RawNamedPreset {
  public String role;
  public String taxIds;
  public String shortLabel;
}
