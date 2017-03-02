package uk.ac.ebi.quickgo.index.annotation.coterms;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Encapsulates configurable properties used during the generation of co-occurring term data.
 *
 * Created 02/03/17
 * @author Edd
 */
public class CoTermsConfigProperties {
    private int cotermsChunk;
    private int coTermLogInterval;
    private String manualCoTermsPath;
    private String allCoTermsPath;

    private CoTermsConfigProperties(Builder builder) {
        this.cotermsChunk = checkValidValue(builder.cotermsChunk);
        this.coTermLogInterval = checkValidValue(builder.coTermLogInterval);
        this.manualCoTermsPath = checkValidValue(builder.manualCoTermsPath);
        this.allCoTermsPath = checkValidValue(builder.allCoTermsPath);
    }

    private <T> T checkValidValue(T value) {
        checkArgument(value != null, "Value cannot be null");
        return value;
    }

    int getCoTermsChunk() {return cotermsChunk;}

    int getCoTermLogInterval() {return coTermLogInterval;}

    String getManualCoTermsPath() {return manualCoTermsPath;}

    String getAllCoTermsPath() {return allCoTermsPath;}

    public static class Builder {
        private int cotermsChunk;
        private int coTermLogInterval;
        private String manualCoTermsPath;
        private String allCoTermsPath;

        public Builder withCotermsChunk(int cotermsChunk) {
            this.cotermsChunk = cotermsChunk;
            return this;
        }

        public Builder withCoTermLogInterval(int coTermLogInterval) {
            this.coTermLogInterval = coTermLogInterval;
            return this;
        }

        public Builder withManualCoTermsPath(String manualCoTermsPath) {
            this.manualCoTermsPath = manualCoTermsPath;
            return this;
        }

        public Builder withAllCoTermsPath(String allCoTermsPath) {
            this.allCoTermsPath = allCoTermsPath;
            return this;
        }

        public CoTermsConfigProperties build() {
            return new CoTermsConfigProperties(this);
        }
    }
}
