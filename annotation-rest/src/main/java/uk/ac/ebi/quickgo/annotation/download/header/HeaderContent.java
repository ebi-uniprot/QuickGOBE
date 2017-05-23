package uk.ac.ebi.quickgo.annotation.download.header;


/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 10:44
 * Created with IntelliJ IDEA.
 */
public class HeaderContent {

    private String uri;
    private boolean isSlimmed;
    private String date;

    public String date() {
        return date;
    }

    String uri() {
        return uri;
    }

    boolean isSlimmed(){
        return isSlimmed;
    }

    public static class  Builder{
        String uri;
        boolean isSlimmed;
        String date;

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

        public HeaderContent build(){
            return new HeaderContent(this);
        }
    }

    private HeaderContent(Builder builder){
        this.uri = builder.uri;
        this.isSlimmed = builder.isSlimmed;
        this.date = builder.date;
    }
}
