package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a particular statistic about the annotations that needs to be shown
 * whenever statistics are calculated. The list of required statistics are contained
 * within {@link RequiredStatistics}.
 *
 * Created 16/08/17
 * @author Edd
 */
class RequiredStatistic {
    private final String groupName;
    private final String groupField;
    private final String aggregateFunction;
    private final List<RequiredStatisticType> types;

    RequiredStatistic(String groupName, String groupField, String aggregateFunction, List<RequiredStatisticType> types) {
        checkArgument(groupName != null && !groupName.trim().isEmpty(),
                "Statistics group name cannot be null or empty");
        checkArgument(groupField != null && !groupField.trim().isEmpty(),
                "Statistics group field cannot be null or empty");
        checkArgument(aggregateFunction != null && !aggregateFunction.trim().isEmpty(), "Statistics " +
                "aggregate function cannot be null or empty");

        this.groupName = groupName;
        this.groupField = groupField;
        this.aggregateFunction = aggregateFunction;

        if (types == null) {
            this.types = Collections.emptyList();
        } else {
            this.types = types;
        }
    }

    String getGroupName() {
        return groupName;
    }

    String getGroupField() {
        return groupField;
    }

    String getAggregateFunction() {
        return aggregateFunction;
    }

    Collection<RequiredStatisticType> getTypes() {
        return Collections.unmodifiableList(types);
    }
}
