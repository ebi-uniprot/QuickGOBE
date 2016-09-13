package uk.ac.ebi.quickgo.client.presets.read.ff;

/**
 * Represents the column indices required to create a {@link RawNamedPreset} instance.
 *
 * Created 13/09/16
 * @author Edd
 */
interface RawNamedPresetColumns {
    int getIdPosition();

    int getNamePosition();

    int getDescriptionPosition();

    int getRelevancyPosition();

    int getMaxRequiredColumnCount();
}
