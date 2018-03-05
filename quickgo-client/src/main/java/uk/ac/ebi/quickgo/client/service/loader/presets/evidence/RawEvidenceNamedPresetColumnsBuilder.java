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
class RawEvidenceNamedPresetColumnsBuilder extends RawNamedPresetColumnsBuilder {
    private int goEvidencePosition;

    public RawEvidenceNamedPresetColumnsBuilder(int namePosition) {
        super(namePosition);
        this.goEvidencePosition = UNINITIALIZED_POSITION;
    }

    public RawEvidenceNamedPresetColumnsImpl build() {
        return new RawEvidenceNamedPresetColumnsImpl(this);
    }

    public RawEvidenceNamedPresetColumnsBuilder withGoEvidence(int _goEvidencePosition) {
        checkColumnPosition(_goEvidencePosition);
        this.goEvidencePosition = _goEvidencePosition;
        return this;
    }

    static RawEvidenceNamedPresetColumnsBuilder createWithNamePosition(int namePosition) {
        return new RawEvidenceNamedPresetColumnsBuilder(namePosition);
    }

    protected static class RawEvidenceNamedPresetColumnsImpl extends RawNamedPresetColumnsImpl {
        private int goEvidencePosition;

        protected RawEvidenceNamedPresetColumnsImpl(RawEvidenceNamedPresetColumnsBuilder builder) {
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
