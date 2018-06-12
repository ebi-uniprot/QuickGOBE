package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RawNamedPreset that = (RawNamedPreset) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) && Objects.equals(relevancy, that.relevancy) &&
                Objects.equals(url, that.url) && Objects.equals(association, that.association);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, description, relevancy, url, association);
    }

    @Override
    public String toString() {
        return "RawNamedPreset{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", description='" + description +
                '\'' + ", relevancy=" + relevancy + ", url='" + url + '\'' + ", association='" + association + '\'' +
                '}';
    }
}
