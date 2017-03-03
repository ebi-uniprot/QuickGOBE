package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;

/**
 * Encapsulates configurable properties used during the generation of co-occurring term data.
 * This class' variables are populated using Spring's {@code @ConfigurationProperties} annotation in
 * {@link CoTermsConfig}.
 *
 * Created 02/03/17
 * @author Edd
 */
public class CoTermsConfigProperties {
    private static final int DEFAULT_CHUNK_SIZE = 1;
    private static final int DEFAULT_LOG_INTERVAL = 1000;
    private static final String DEFAULT_MANUAL_PATH = System.getProperty("user.home") + "/QuickGO/CoTermsManual";
    private static final String DEFAULT_ALL_PATH = System.getProperty("user.home") + "/QuickGO/CoTermsAll";

    private int chunkSize = DEFAULT_CHUNK_SIZE;
    private int loginterval = DEFAULT_LOG_INTERVAL;
    private String manual = DEFAULT_MANUAL_PATH;
    private String all = DEFAULT_ALL_PATH;

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getLoginterval() {
        return loginterval;
    }

    public void setLoginterval(int loginterval) {
        this.loginterval = loginterval;
    }

    public String getManual() {
        Preconditions.checkState(
                !all.equals(manual),
                "The output path for manual and all coterms files should not be the same, " +
                        "but they were both %s",
                manual);

        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    public String getAll() {
        Preconditions.checkState(
                !all.equals(manual),
                "The output path for manual and all coterms files should not be the same, " +
                        "but they were both %s",
                manual);

        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }
}
