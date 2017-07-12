package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

import java.util.List;

/**
 * Represents part of the model corresponding to the response available from the resource:
 *
 * <ul>
 *     <li>/go/terms/{term}/descendants</li>
 * </ul>
 *
 * Currently, this model captures the parts reached by the JSON path expression, "$.results.descendants".
 *
 * Created 09/08/16
 * @author Edd
 */
public class OntologyDescendants implements ResponseType {
    private List<Result> results;

    public OntologyDescendants() {}

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
