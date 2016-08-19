package uk.ac.ebi.quickgo.annotation.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a group of statistics results.
 *
 * @author Ricardo Antunes
 */
public class StatisticsGroup {
    private final String groupName;
    private final long totalHits;

    private final List<StatisticsByType> types;

    public StatisticsGroup(String groupName, long totalHits) {
        Preconditions.checkArgument(groupName != null && !groupName.isEmpty(),
                "Stats groupName cannot be null or empty");
        Preconditions.checkArgument(totalHits >= 0, "Stats total hits can not be negative");

        this.groupName = groupName;
        this.totalHits = totalHits;
        this.types = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void addStatsType(StatisticsByType statsType) {
        Preconditions.checkArgument(statsType != null, "Statistics type cannot be null");
        types.add(statsType);
    }

    public List<StatisticsByType> getTypes() {
        return Collections.unmodifiableList(types);
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StatisticsGroup that = (StatisticsGroup) o;

        if (totalHits != that.totalHits) {
            return false;
        }
        if (!groupName.equals(that.groupName)) {
            return false;
        }
        return types.equals(that.types);

    }

    @Override public int hashCode() {
        int result = groupName.hashCode();
        result = 31 * result + (int) (totalHits ^ (totalHits >>> 32));
        result = 31 * result + types.hashCode();
        return result;
    }

    @Override public String toString() {
        return "StatisticsGroup{" +
                "groupName='" + groupName + '\'' +
                ", totalHits=" + totalHits +
                ", types=" + types +
                '}';
    }
}