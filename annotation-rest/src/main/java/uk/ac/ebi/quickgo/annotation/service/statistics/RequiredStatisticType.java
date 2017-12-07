package uk.ac.ebi.quickgo.annotation.service.statistics;

import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents information about particular types within a {@link RequiredStatistic}, for example,
 * GO terms, taxons, evidences, etc.
 *
 * Each {@link RequiredStatisticType} can have a {@code limit} set, indicating how many of each type to be
 * shown once the statistics calculation has been performed. By not setting {@code limit}, a default
 * of 10 will be displayed.
 *
 * Created 16/08/17
 * @author Edd
 */
public class RequiredStatisticType {
    private static final Logger LOGGER = getLogger(RequiredStatisticType.class);
    private final String name;
    private final int limit;
    static final int DEFAULT_LIMIT = 10;

    private RequiredStatisticType(String name) {
        this(name, DEFAULT_LIMIT);
    }

    private RequiredStatisticType(String name, int limit) {
        checkArgument(name != null && !name.trim().isEmpty(),
                "RequiredStatisticType name cannot be null or empty");

        this.name = name;

        if (limit <= 0) {
            LOGGER.warn("Attempt to set RequiredStatisticType limit to {}. Value must be greater than 0.", limit);
            this.limit = DEFAULT_LIMIT;
        } else {
            this.limit = limit;
        }
    }

    public String getName() {
        return name;
    }

    public int getLimit() {
        return limit;
    }

    @Override public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RequiredStatisticType that = (RequiredStatisticType) o;

        if (limit != that.limit) {
            return false;
        }
        return name != null ? name.equals(that.name) : that.name == null;
    }

    static RequiredStatisticType statsType(String name) {
        return new RequiredStatisticType(name);
    }

    static RequiredStatisticType statsType(String name, int limit) {
        return new RequiredStatisticType(name, limit);
    }
}
