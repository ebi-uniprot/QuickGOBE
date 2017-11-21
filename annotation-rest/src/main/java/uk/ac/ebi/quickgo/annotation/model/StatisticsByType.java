package uk.ac.ebi.quickgo.annotation.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of statistics values for a given type.
 *
 * @author Ricardo Antunes
 */
public class StatisticsByType {
    private final String type;
    private final List<StatisticsValue> values;

    public StatisticsByType(String type) {
        Preconditions.checkArgument(type != null && !type.isEmpty(), "Statistics type cannot be null or empty");
        this.type = type;
        this.values = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void addValue(StatisticsValue value) {
        Preconditions.checkArgument(value != null, "Stats value cannot be null");
        values.add(value);
    }

    public List<StatisticsValue> getValues() {
        return values;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StatisticsByType that = (StatisticsByType) o;

        if (!type.equals(that.type)) {
            return false;
        }
        return values.equals(that.values);

    }

    @Override public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override public String toString() {
        return "StatisticsByType{" +
                "type='" + type + '\'' +
                ", values=" + values +
                '}';
    }
}