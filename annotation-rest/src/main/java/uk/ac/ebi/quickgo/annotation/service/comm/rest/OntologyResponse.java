package uk.ac.ebi.quickgo.annotation.service.comm.rest;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

import java.util.List;

/**
 * Created 09/08/16
 * @author Edd
 */
public class OntologyResponse implements ResponseType {
    private List<Result> results;

    public OntologyResponse() {}

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
        public Result() {}

        private String id;
        private List<String> descendants;

        public List<String> getDescendants() {
            return descendants;
        }

        public void setDescendants(List<String> descendants) {
            this.descendants = descendants;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override public String toString() {
            return "Result{" +
                    "id='" + id + '\'' +
                    ", descendants=" + descendants +
                    '}';
        }
    }
}
