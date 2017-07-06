package uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.model;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

import java.util.List;

/**
 * Represents part of the model corresponding to the response available from the resource:
 *
 * <ul>
 *     <li>/geneproduct/{id}</li>
 * </ul>
 *
 * This model captures the parts reached by the JSON path expression, "$.results.name; $.results.synonyms;".
 * @author Tony Wardell
 * Date: 23/06/2017
 * Time: 15:07
 * Created with IntelliJ IDEA.
 */
public class BasicGeneProduct implements ResponseType {

    private List<Result> results;

    public BasicGeneProduct() {}

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override public String toString() {
        return "ResponseType{" +
                "results=" + results +
                '}';
    }

    public static class Result {
        private String id;
        private String name;
        private List<String> synonyms;
        private String type;

        public Result() {}

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getSynonyms() {
            return synonyms;
        }

        public void setSynonyms(List<String> synonyms) {
            this.synonyms = synonyms;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override public String toString() {
            return "Result{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", synonyms=" + synonyms +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
