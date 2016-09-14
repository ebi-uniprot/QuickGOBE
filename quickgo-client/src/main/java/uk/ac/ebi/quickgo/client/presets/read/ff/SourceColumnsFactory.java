package uk.ac.ebi.quickgo.client.presets.read.ff;

/**
 * Factory for creating {@link RawNamedPresetColumns} instances based on a specified {@link Source}.
 * The supplied {@link Source} indicates the source which is being read.
 *
 * Created 13/09/16
 * @author Edd
 */
public class SourceColumnsFactory {

    public static RawNamedPresetColumns createFor(Source source) {
        switch (source) {
            case DB_COLUMNS:
                return RawNamedPresetColumnsBuilder
                        .createWithNamePosition(0)
                        .withDescriptionPosition(1)
                        .build();
            case ECO2GO_COLUMNS:
                return RawNamedPresetColumnsBuilder
                        .createWithNamePosition(2)
                        .withIdPosition(0)
                        .withDescriptionPosition(1)
                        .withRelevancyPosition(3)
                        .build();
            case GENE_PRODUCT_COLUMNS:
                return RawNamedPresetColumnsBuilder
                        .createWithNamePosition(0)
                        .withDescriptionPosition(1)
                        .withURLPosition(3)
                        .build();
            default:
                throw new IllegalStateException("Source type: " + source + " is not handled.");
        }
    }

    public enum Source {
        DB_COLUMNS,
        ECO2GO_COLUMNS,
        GENE_PRODUCT_COLUMNS
    }
}
