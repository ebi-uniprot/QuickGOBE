package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.List;

/**
 * Hold content used to populate and control the output of downloaded file headers.
 *
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 10:44
 * Created with IntelliJ IDEA.
 */
public class HeaderContent {

    private final String uri;
    private final boolean isSlimmed;
    private final String date;
    private final List<String> selectedFields;

    private HeaderContent(Builder builder) {
        this.uri = builder.uri;
        this.isSlimmed = builder.isSlimmed;
        this.date = builder.date;
        this.selectedFields = builder.selectedFields;
    }

    public String getDate() {
        return date;
    }

    List<String> getSelectedFields() {
        return selectedFields;
    }

    String getUri() {
        return uri;
    }

    boolean isSlimmed() {
        return isSlimmed;
    }

    public static class Builder {
        String uri;
        boolean isSlimmed;
        String date;
        List<String> selectedFields;

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder setIsSlimmed(boolean isSlimmed) {
            this.isSlimmed = isSlimmed;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setSelectedFields(List<String> selectedFields) {
            this.selectedFields = selectedFields;
            return this;
        }

        public HeaderContent build() {
            return new HeaderContent(this);
        }
    }
}
