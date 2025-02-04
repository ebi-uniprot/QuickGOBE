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
public class RawNamedPresetColumnsBuilder {
    protected static final int UNINITIALIZED_POSITION = -1;
    private int idPosition;

    private int namePosition;
    private int descriptionPosition;
    private int relevancyPosition;
    private int urlPosition;
    private int associationPosition;

    protected RawNamedPresetColumnsBuilder(int namePosition) {
        checkColumnPosition(namePosition);
        this.namePosition = namePosition;

        this.idPosition = UNINITIALIZED_POSITION;
        this.descriptionPosition = UNINITIALIZED_POSITION;
        this.relevancyPosition = UNINITIALIZED_POSITION;
        this.urlPosition = UNINITIALIZED_POSITION;
        this.associationPosition = UNINITIALIZED_POSITION;
    }

    public RawNamedPresetColumns build() {
        return new RawNamedPresetColumnsImpl(this);
    }

    static RawNamedPresetColumnsBuilder createWithNamePosition(int namePosition) {
        return new RawNamedPresetColumnsBuilder(namePosition);
    }

    public RawNamedPresetColumnsBuilder withIdPosition(int idPosition) {
        checkColumnPosition(idPosition);
        this.idPosition = idPosition;
        return this;
    }

    public RawNamedPresetColumnsBuilder withDescriptionPosition(int descriptionPosition) {
        checkColumnPosition(descriptionPosition);
        this.descriptionPosition = descriptionPosition;
        return this;
    }

    public RawNamedPresetColumnsBuilder withRelevancyPosition(int relevancyPosition) {
        checkColumnPosition(relevancyPosition);
        this.relevancyPosition = relevancyPosition;
        return this;
    }

    RawNamedPresetColumnsBuilder withURLPosition(int urlPosition) {
        checkColumnPosition(urlPosition);
        this.urlPosition = urlPosition;
        return this;
    }

    public RawNamedPresetColumnsBuilder withAssociationPosition(int associationPosition) {
        checkColumnPosition(associationPosition);
        this.associationPosition = associationPosition;
        return this;
    }

    protected void checkColumnPosition(int columnPosition) {
        checkArgument(columnPosition >= 0,
                "Column position [" + columnPosition + "] must be greater than or equal to 0");
    }

    protected static class RawNamedPresetColumnsImpl implements RawNamedPresetColumns {
        protected static final int MAX_REQUIRED_COLUMN_POSITION_NOT_INITIALIZED = -1;
        protected static final int DEFAULT_COLUMN_POSITION_NOT_INITIALIZED = 0;
        protected int maxRequiredColumnPosition = MAX_REQUIRED_COLUMN_POSITION_NOT_INITIALIZED;

        private final int urlPosition;
        private int idPosition;
        private int namePosition;
        private int descriptionPosition;
        private int relevancyPosition;
        private int associationPosition;

        protected RawNamedPresetColumnsImpl(RawNamedPresetColumnsBuilder builder) {
            this.idPosition = builder.idPosition;
            this.namePosition = builder.namePosition;
            this.descriptionPosition = builder.descriptionPosition;
            this.relevancyPosition = builder.relevancyPosition;
            this.urlPosition = builder.urlPosition;
            this.associationPosition = builder.associationPosition;
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

        @Override public int getAssociationPosition() {
            return associationPosition;
        }

        @Override public int getMaxRequiredColumnCount() {
            if (maxRequiredColumnPosition == MAX_REQUIRED_COLUMN_POSITION_NOT_INITIALIZED) {
                maxRequiredColumnPosition =
                        Stream.of(
                                getDescriptionPosition(),
                                getIdPosition(),
                                getNamePosition(),
                                getRelevancyPosition(),
                                getAssociationPosition()
                        ).max(Comparator.naturalOrder())
                                .map(columnPosition -> columnPosition + 1)
                                .orElse(DEFAULT_COLUMN_POSITION_NOT_INITIALIZED);
            }
            return maxRequiredColumnPosition;
        }
    }

}
