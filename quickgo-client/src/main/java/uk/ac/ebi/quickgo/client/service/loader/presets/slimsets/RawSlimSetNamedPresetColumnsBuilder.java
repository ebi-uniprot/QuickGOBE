package uk.ac.ebi.quickgo.client.service.loader.presets.slimsets;

import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPresetColumnsBuilder;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * A specialisation of {@link RawSlimSetNamedPresetColumnsBuilder} used to create
 * {@link RawSlimSetNamedPresetColumnsImpl}
 * instances, which capture information about the column indices relating to a {@link RawSlimSetNamedPreset}.
 */
public class RawSlimSetNamedPresetColumnsBuilder extends RawNamedPresetColumnsBuilder {
    private int rolePosition;
    private int taxIdsPosition;
    private int shortLabelPosition;

    private RawSlimSetNamedPresetColumnsBuilder(int namePosition) {
        super(namePosition);
        this.rolePosition = UNINITIALIZED_POSITION;
        this.taxIdsPosition = UNINITIALIZED_POSITION;
        this.shortLabelPosition = UNINITIALIZED_POSITION;
    }

    @Override
    public RawSlimSetNamedPresetColumnsImpl build() {
        return new RawSlimSetNamedPresetColumnsImpl(this);
    }

    public RawSlimSetNamedPresetColumnsBuilder withRolePosition(int rolePosition) {
        checkColumnPosition(rolePosition);
        this.rolePosition = rolePosition;
        return this;
    }

    public RawSlimSetNamedPresetColumnsBuilder withTaxIdsPosition(int taxIdsPosition) {
        checkColumnPosition(taxIdsPosition);
        this.taxIdsPosition = taxIdsPosition;
        return this;
    }

    public RawSlimSetNamedPresetColumnsBuilder withShortLabelPosition(int shortLabelPosition) {
        checkColumnPosition(shortLabelPosition);
        this.shortLabelPosition = shortLabelPosition;
        return this;
    }

    @Override
    public RawSlimSetNamedPresetColumnsBuilder withIdPosition(int idPosition) {
        super.withIdPosition(idPosition);
        return this;
    }

    @Override
    public RawSlimSetNamedPresetColumnsBuilder withDescriptionPosition(int descriptionPosition) {
        super.withDescriptionPosition(descriptionPosition);
        return this;
    }

    @Override
    public RawSlimSetNamedPresetColumnsBuilder withAssociationPosition(int associationPosition) {
        super.withAssociationPosition(associationPosition);
        return this;
    }

    public static RawSlimSetNamedPresetColumnsBuilder createWithNamePosition(int namePosition) {
        return new RawSlimSetNamedPresetColumnsBuilder(namePosition);
    }

    public static class RawSlimSetNamedPresetColumnsImpl extends RawNamedPresetColumnsImpl {
        private final int rolePosition;
        private final int taxIdsPosition;
        private final int shortLabelPosition;

        RawSlimSetNamedPresetColumnsImpl(RawSlimSetNamedPresetColumnsBuilder builder) {
            super(builder);
            this.rolePosition = builder.rolePosition;
            this.taxIdsPosition = builder.taxIdsPosition;
            this.shortLabelPosition = builder.shortLabelPosition;
        }

        public int getRolePosition() {
            return rolePosition;
        }

        public int getTaxIdsPosition() {
            return taxIdsPosition;
        }

        public int getShortLabelPosition() {
            return shortLabelPosition;
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
                                getRolePosition(),
                                getTaxIdsPosition(),
                                getShortLabelPosition()
                        ).max(Comparator.naturalOrder())
                                .map(columnPosition -> columnPosition + 1)
                                .orElse(DEFAULT_COLUMN_POSITION_NOT_INITIALIZED);
            }
            return maxRequiredColumnPosition;
        }
    }

}
