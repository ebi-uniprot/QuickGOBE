package uk.ac.ebi.quickgo.annotation.coterms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Contains the configuration data for the endpoint that provides data about co-occurring terms (GO terms that appear
 * in multiple GO Term annotations).
 * @author Tony Wardell
 * Date: 17/03/2017
 * Time: 15:09
 * Created with IntelliJ IDEA.
 */
@Component
@ConfigurationProperties(prefix="coterm")
public class CoTermProperties {

    private static final int DEFAULT_LIMIT = 50;
    private static final Resource DEFAULT_MANUAL_RESOURCE = new ClassPathResource("/coterms/CoTermsManual");
    private static final Resource DEFAULT_ALL_RESOURCE = new ClassPathResource("/coterms/CoTermsAll");
    private static final int DEFAULT_HEADER_LINES = 1;

    public int limit = DEFAULT_LIMIT;
    public Resource manual = DEFAULT_MANUAL_RESOURCE;
    public Resource all = DEFAULT_ALL_RESOURCE;
    public int headerLines = DEFAULT_HEADER_LINES;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Resource getManual() {
        return manual;
    }

    public void setManual(Resource manual) {
        this.manual = manual;
    }

    public Resource getAll() {
        return all;
    }

    public void setAll(Resource all) {
        this.all = all;
    }

    public int getHeaderLines() {
        return headerLines;
    }

    public void setHeaderLines(int headerLines) {
        this.headerLines = headerLines;
    }

    @Override public String toString() {
        return "CoTermProperties{" +
                "limit=" + limit +
                ", manual=" + manual +
                ", all=" + all +
                ", headerLines=" + headerLines +
                '}';
    }
}
