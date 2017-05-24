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

    private String uri;
    private boolean isSlimmed;
    private String date;
    private List<String> selectedFields;

    public String date() {
        return date;
    }

    List<String> selectedFields() {
        return selectedFields;
    }

    String uri() {
        return uri;
    }

    boolean isSlimmed(){
        return isSlimmed;
    }

    public static class Builder{
        String uri;
        boolean isSlimmed;
        String date;
        List<String> selectedFields;

        public Builder uri(String uri){
            this.uri = uri;
            return this;
        }

        public Builder isSlimmed(boolean isSlimmed){
            this.isSlimmed = isSlimmed;
            return this;
        }

        public Builder date(String date){
            this.date = date;
            return this;
        }

        public Builder selectedFields(List<String> selectedFields){
            this.selectedFields = selectedFields;
            return this;
        }

        public HeaderContent build(){
            return new HeaderContent(this);
        }
    }

    private HeaderContent(Builder builder){
        this.uri = builder.uri;
        this.isSlimmed = builder.isSlimmed;
        this.date = builder.date;
        this.selectedFields = builder.selectedFields;
    }
}
