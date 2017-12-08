package uk.ac.ebi.quickgo.annotation.model;

import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of statistics values for a given type.
 *
 * @author Ricardo Antunes
 */
public class StatisticsByType {
    private final String type;
    private final int distinctValueCount;
    private final List<StatisticsValue> values;

    public StatisticsByType(String type, int distinctValueCount) {
        Preconditions.checkArgument(type != null && !type.isEmpty(), "Statistics type cannot be null or empty.");
        Preconditions.checkArgument(distinctValueCount >= 0, "Distinct Value Count should be be greater than zero.");
        this.type = type;
        this.distinctValueCount = distinctValueCount;
        this.values = new ArrayList<>();
    }

    public StatisticsByType(String type) {
        this(type, 0);
    }

    public String getType() {
        return type;
    }

    public int getDistinctValueCount() {
        return truncateToTensOfThousands(distinctValueCount);
    }

    public void addValue(StatisticsValue value) {
        Preconditions.checkArgument(value != null, "Stats value cannot be null");
        values.add(value);
    }

    public List<StatisticsValue> getValues() {
        return values;
    }

    @Override public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StatisticsByType that = (StatisticsByType) o;

        if (distinctValueCount != that.distinctValueCount) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        return values != null ? values.equals(that.values) : that.values == null;
    }

    @Override public String toString() {
        return "StatisticsByType{" +
                "type='" + type + '\'' +
                ", distinctValueCount=" + distinctValueCount +
                ", values=" + values +
                '}';
    }

    private int truncateToTensOfThousands(int distinctValueCount) {
        if (distinctValueCount < 10001) {
            return distinctValueCount;
        }
        int value = new BigDecimal(distinctValueCount).divide(new BigDecimal(1000), RoundingMode.HALF_UP).intValue();
        return Integer.parseInt(String.format("%d000", value));
    }
}
