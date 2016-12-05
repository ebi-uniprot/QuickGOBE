package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

/**
 * Captures general information about a preset, which can be used to provide preset data
 * in an externally defined DTO.
 *
 * Created 30/08/16
 * @author Edd
 */
public class RawNamedPreset {
    public String id;
    public String name;
    public String description;
    public Integer relevancy;
    public String url;
    public String association;
}
