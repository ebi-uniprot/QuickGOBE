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
    private final int approximateCount;
    private final List<StatisticsValue> values;

    /**
     * Create an instances of StatisticsByType
     * @param type the type i.e. name of the thing the statistics represents e.g. 'goId', 'aspect'
     * @param approximateCount An estimated (due to the limitations of the persistence layer) count of the unique
     * elements represented by this object. For example, if the type was 'goId', the approximateCount will be the
     * number of unique goIds within the statistics search result. This MIGHT NOT be the same as the size of the
     * values collection, since this can be curtailed by the limit defined for the facet query response.
     */
    public StatisticsByType(String type, int approximateCount) {
        Preconditions.checkArgument(type != null && !type.isEmpty(), "Statistics type cannot be null or empty.");
        Preconditions.checkArgument(approximateCount >= 0, "Distinct Value Count should be be greater than zero.");
        this.type = type;
        this.approximateCount = approximateCount;
        this.values = new ArrayList<>();
    }

    public StatisticsByType(String type) {
        this(type, 0);
    }

    public String getType() {
        return type;
    }

    /**
     * An estimated count of the unique elements represented by this object. If the estimated count is less than the
     * size of the results this object contains, then this method will return the count of the number of statistics
     * elements this instance holds,  otherwise the estimated value will be returned, truncated to the  nearest tens of
     * thousands (if over ten thousand) to reflect it's lack of accuracy.
     * @return int of
     */
    public int getApproximateCount() {
        return approximateCount < values.size() ? values.size() : truncateToTensOfThousands(approximateCount);
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

        if (approximateCount != that.approximateCount) {
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
                ", approximateCount=" + approximateCount +
                ", values=" + values +
                '}';
    }

    private int truncateToTensOfThousands(int approximateCount) {
        if (approximateCount < 10001) {
            return approximateCount;
        }
        int value = new BigDecimal(approximateCount).divide(new BigDecimal(1000), RoundingMode.HALF_UP).intValue();
        return Integer.parseInt(String.format("%d000", value));
    }
}
