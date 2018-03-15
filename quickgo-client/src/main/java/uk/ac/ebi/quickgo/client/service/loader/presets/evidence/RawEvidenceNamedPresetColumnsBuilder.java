package uk.ac.ebi.quickgo.client.service.loader.presets.evidence;

import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPresetColumnsBuilder;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * A specialisation of {@link RawEvidenceNamedPresetColumnsBuilder} used to create
 * {@link RawEvidenceNamedPresetColumnsImpl}
 * instances, which capture information about the column indices relating to a {@link RawEvidenceNamedPreset}.
 *
 * Created 05/03/18
 * @author Tony Wardell
 */
public class RawEvidenceNamedPresetColumnsBuilder extends RawNamedPresetColumnsBuilder {
    private int goEvidencePosition;

    private RawEvidenceNamedPresetColumnsBuilder(int namePosition) {
        super(namePosition);
        this.goEvidencePosition = UNINITIALIZED_POSITION;
    }

    @Override
    public RawEvidenceNamedPresetColumnsImpl build() {
        return new RawEvidenceNamedPresetColumnsImpl(this);
    }

    public RawEvidenceNamedPresetColumnsBuilder withGoEvidence(int goEvidencePosition) {
        checkColumnPosition(goEvidencePosition);
        this.goEvidencePosition = goEvidencePosition;
        return this;
    }

    @Override
    public RawEvidenceNamedPresetColumnsBuilder withIdPosition(int idPosition) {
        super.withIdPosition(idPosition);
        return this;
    }

    @Override
    public RawEvidenceNamedPresetColumnsBuilder withRelevancyPosition(int relevancyPosition) {
        super.withRelevancyPosition(relevancyPosition);
        return this;
    }

    public static RawEvidenceNamedPresetColumnsBuilder createWithNamePosition(int namePosition) {
        return new RawEvidenceNamedPresetColumnsBuilder(namePosition);
    }

    public static class RawEvidenceNamedPresetColumnsImpl extends RawNamedPresetColumnsImpl {
        private final int goEvidencePosition;

        RawEvidenceNamedPresetColumnsImpl(RawEvidenceNamedPresetColumnsBuilder builder) {
            super(builder);
            this.goEvidencePosition = builder.goEvidencePosition;
        }

        public int getGoEvidencePosition() {
            return goEvidencePosition;
        }

        @Override public int getMaxRequiredColumnCount() {
            if (maxRequiredColumnPosition == MAX_REQUIRED_COLUMN_POSITION_NOT_INITIALIZED) {
                maxRequiredColumnPosition =
                        Stream.of(
                                getDescriptionPosition(),
                                getIdPosition(),
                                getNamePosition(),
                                getRelevancyPosition(),
                                getAssociationPosition(),
                                getGoEvidencePosition()
                        ).max(Comparator.naturalOrder())
                                .map(columnPosition -> columnPosition + 1)
                                .orElse(DEFAULT_COLUMN_POSITION_NOT_INITIALIZED);
            }
            return maxRequiredColumnPosition;
        }
    }

}
