package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * // TODO: 16/08/17
 * Created 16/08/17
 * @author Edd
 */
public class RequiredStatisticType {
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
        checkArgument(limit > 0,
                "Statistics type limit must be greater than 0");
        this.limit = limit;
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
