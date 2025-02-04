package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import uk.ac.ebi.quickgo.client.service.loader.presets.evidence.RawEvidenceNamedPresetColumnsBuilder;
import uk.ac.ebi.quickgo.client.service.loader.presets.slimsets.RawSlimSetNamedPresetColumnsBuilder;

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
                return RawNamedPresetColumnsBuilder.createWithNamePosition(0)
                        .withDescriptionPosition(1)
                        .build();
            case REF_COLUMNS:
                return RawNamedPresetColumnsBuilder.createWithNamePosition(0)
                        .withDescriptionPosition(1)
                        .withRelevancyPosition(2)
                        .build();
            case GENE_PRODUCT_COLUMNS:
                return RawNamedPresetColumnsBuilder.createWithNamePosition(0)
                        .withDescriptionPosition(1)
                        .withURLPosition(3)
                        .build();
            case EXT_RELATION_COLUMNS:
                return RawNamedPresetColumnsBuilder
                        .createWithNamePosition(1)
                        .withIdPosition(0)
                        .build();
            case TAXON_COLUMNS:
                return RawNamedPresetColumnsBuilder.createWithNamePosition(1)
                                                   .withIdPosition(0)
                                                   .build();
            case EXT_DATABASE_COLUMNS:
                return RawNamedPresetColumnsBuilder
                        .createWithNamePosition(0)
                        .build();
            default:
                throw new IllegalStateException("Source type: " + source + " is not handled.");
        }
    }

    public static RawEvidenceNamedPresetColumnsBuilder.RawEvidenceNamedPresetColumnsImpl createEvidenceColumns() {
        return RawEvidenceNamedPresetColumnsBuilder
                .createWithNamePosition(1)
                .withIdPosition(0)
                .withGoEvidence(2)
                .withRelevancyPosition(3)
                .build();
    }

    public static RawSlimSetNamedPresetColumnsBuilder.RawSlimSetNamedPresetColumnsImpl createSlimSetColumns() {
        return RawSlimSetNamedPresetColumnsBuilder
              .createWithNamePosition(0)
              .withIdPosition(1)
              .withDescriptionPosition(2)
              .withAssociationPosition(3)
              .withRolePosition(4)
              .withTaxIdsPosition(5)
              .withShortLabelPosition(6)
              .build();
    }

    public enum Source {
        DB_COLUMNS,
        GENE_PRODUCT_COLUMNS,
        REF_COLUMNS,
        EXT_RELATION_COLUMNS,
        TAXON_COLUMNS,
        EXT_DATABASE_COLUMNS
    }
}
