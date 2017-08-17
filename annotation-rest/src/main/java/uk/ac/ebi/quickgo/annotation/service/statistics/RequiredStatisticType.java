package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.Optional;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Optional.empty;
import static java.util.Optional.of;
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
    private int limit = 0;

    private RequiredStatisticType(String name) {
        checkArgument(name != null && !name.trim().isEmpty(),
                "RequiredStatisticType name cannot be null or empty");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Optional<Integer> getLimit() {
        if (limit > 0) {
            return of(limit);
        } else {
            return empty();
        }
    }

    public void setLimit(int limit) {
        if (limit <= 0) {
            LOGGER.warn("Attempt to set RequiredStatisticType limit to {}. Value must be greater than 0.", limit);
        } else {
            this.limit = limit;
        }
    }

    @Override public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + limit;
        return result;
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
        RequiredStatisticType requiredStatisticType = new RequiredStatisticType(name);
        requiredStatisticType.setLimit(limit);
        return requiredStatisticType;
    }
}
