package uk.ac.ebi.quickgo.ontology;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Holds values related to the operation of the Ontology REST service.
 * <p>
 * Created by Tony on 04-Apr-17.
 */
@Component
@ConfigurationProperties(prefix = "ontology.cache.control.time")
public class OntologyRestProperties {

    private static final int MINUTES = 0;
    private static final int DEFAULT_START_HOURS = 18;
    private static final int DEFAULT_END_HOURS = 17;
    private LocalTime startTime = LocalTime.of(DEFAULT_START_HOURS, MINUTES);
    private LocalTime endTime = LocalTime.of(DEFAULT_END_HOURS, MINUTES);
    private long midnightToEndCacheTime;

    public void setStart(int hours) {
        startTime = LocalTime.of(hours, MINUTES);
    }

    public void setEnd(int hours) {
        endTime = LocalTime.of(hours, MINUTES);
        this.midnightToEndCacheTime = Duration.between(LocalTime.MIDNIGHT, endTime).getSeconds();
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public long midnightToEndCacheTime() {
        return midnightToEndCacheTime;
    }
}
