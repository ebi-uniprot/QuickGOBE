package uk.ac.ebi.quickgo.annotation.model;

import com.google.common.base.Preconditions;

/**
 * Represents the number of times a specific identifier has occurred within a result set.
 *
 * @author Ricardo Antunes
 */
public class StatisticsValue {
    private final String key;

    /**
     * The percentage of hits for the given {@link #key} in relation to the total number of entries.
     */
    private final double percentage;

    /**
     * Number of entries that hit the given {@link #key}
     */
    private final long hits;

    /**
     * The name of the entity referable by the key
     */
    private String name;

    public StatisticsValue(String key, long hits, long total) {
        Preconditions.checkArgument(key != null && !key.isEmpty(), "Stats key cannot be null or empty");
        Preconditions.checkArgument(hits >= 0, "Stats hits cannot be a negative value: " + hits);
        Preconditions.checkArgument(total >= 0, "Stats total cannot be a negative value: " + total);
        Preconditions.checkArgument(total >= hits,
                "Stats total cannot be less than hits: " + total + " < " + hits);

        this.key = key;
        this.percentage = (double) hits / (double) total;
        this.hits = hits;
    }

    public String getKey() {
        return key;
    }

    public double getPercentage() {
        return percentage;
    }

    public long getHits() {
        return hits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = key != null ? key.hashCode() : 0;
        temp = Double.doubleToLongBits(percentage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (hits ^ (hits >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StatisticsValue that = (StatisticsValue) o;

        if (Double.compare(that.percentage, percentage) != 0) {
            return false;
        }
        if (hits != that.hits) {
            return false;
        }
        if (key != null ? !key.equals(that.key) : that.key != null) {
            return false;
        }
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override public String toString() {
        return "StatisticsValue{" + "key='" + key + '\'' + ", percentage=" + percentage + ", hits=" + hits +
                ", name='" + name + '\'' + '}';
    }
}
