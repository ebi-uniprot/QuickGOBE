package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import java.util.Comparator;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A builder used to create {@link RawNamedPresetColumns} instances, which capture information
 * about the column indices relating to a {@link RawNamedPreset}.
 *
 * Created 13/09/16
 * @author Edd
 */
class RawNamedPresetColumnsBuilder {
    static final int UNINITIALIZED_POSITION = -1;

    private int idPosition;
    private int namePosition;
    private int descriptionPosition;
    private int relevancyPosition;
    private int urlPosition;

    private RawNamedPresetColumnsBuilder(int namePosition) {
        checkColumnPosition(namePosition);
        this.namePosition = namePosition;

        this.idPosition = UNINITIALIZED_POSITION;
        this.descriptionPosition = UNINITIALIZED_POSITION;
        this.relevancyPosition = UNINITIALIZED_POSITION;
        this.urlPosition = UNINITIALIZED_POSITION;
    }

    public RawNamedPresetColumns build() {
        return new RawNamedPresetColumnsImpl(this);
    }

    static RawNamedPresetColumnsBuilder createWithNamePosition(int namePosition) {
        return new RawNamedPresetColumnsBuilder(namePosition);
    }

    RawNamedPresetColumnsBuilder withIdPosition(int idPosition) {
        checkColumnPosition(idPosition);
        this.idPosition = idPosition;
        return this;
    }

    RawNamedPresetColumnsBuilder withDescriptionPosition(int descriptionPosition) {
        checkColumnPosition(descriptionPosition);
        this.descriptionPosition = descriptionPosition;
        return this;
    }

    RawNamedPresetColumnsBuilder withRelevancyPosition(int relevancyPosition) {
        checkColumnPosition(relevancyPosition);
        this.relevancyPosition = relevancyPosition;
        return this;
    }

    RawNamedPresetColumnsBuilder withURLPosition(int urlPosition) {
        checkColumnPosition(urlPosition);
        this.urlPosition = urlPosition;
        return this;
    }

    private void checkColumnPosition(int columnPosition) {
        checkArgument(columnPosition >= 0, "Column position must be greater than or equal to 0");
    }

    private static class RawNamedPresetColumnsImpl implements RawNamedPresetColumns {
        static final int MAX_REQUIRED_COLUMN_POSITION_NOT_INITIALIZED = -1;
        private static final int DEFAULT_COLUMN_POSITION_NOT_INITIALIZED = 0;
        private final int urlPosition;
        private int maxRequiredColumnPosition = MAX_REQUIRED_COLUMN_POSITION_NOT_INITIALIZED;
        private int idPosition;
        private int namePosition;
        private int descriptionPosition;
        private int relevancyPosition;

        private RawNamedPresetColumnsImpl(RawNamedPresetColumnsBuilder builder) {
            this.idPosition = builder.idPosition;
            this.namePosition = builder.namePosition;
            this.descriptionPosition = builder.descriptionPosition;
            this.relevancyPosition = builder.relevancyPosition;
            this.urlPosition = builder.urlPosition;
        }

        @Override public int getIdPosition() {
            return idPosition;
        }

        @Override public int getNamePosition() {
            return namePosition;
        }

        @Override public int getDescriptionPosition() {
            return descriptionPosition;
        }

        @Override public int getRelevancyPosition() {
            return relevancyPosition;
        }

        @Override public int getURLPosition() {
            return urlPosition;
        }

        @Override public int getMaxRequiredColumnCount() {
            if (maxRequiredColumnPosition == MAX_REQUIRED_COLUMN_POSITION_NOT_INITIALIZED) {
                maxRequiredColumnPosition =
                        Stream.of(
                                getDescriptionPosition(),
                                getIdPosition(),
                                getNamePosition(),
                                getRelevancyPosition()
                        ).max(Comparator.naturalOrder())
                                .map(columnPosition -> columnPosition + 1)
                                .orElse(DEFAULT_COLUMN_POSITION_NOT_INITIALIZED);
            }
            return maxRequiredColumnPosition;
        }
    }

}
