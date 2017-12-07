package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

import java.util.List;

/**
 * Represents parts of the model corresponding to responses available from the resources:
 *
 * <ul>
 *     <li>/go/terms/{term}/descendants</li>
 *     <li>/go/slim?ids={terms}</li>
 * </ul>
 *
 * Currently, this model captures the parts reached by the JSON path expressions, "$.results.descendants" or
 * "$.results.slimsTo".
 *
 * Created 09/08/16
 * @author Edd
 */
public class OntologyRelatives implements ResponseType {
    private List<Result> results;

    public OntologyRelatives() {}

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
        private List<String> slimsTo;
        private List<String> descendants;

        public List<String> getSlimsTo() {
            return slimsTo;
        }

        public void setSlimsTo(List<String> slimsTo) {
            this.slimsTo = slimsTo;
        }

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
                    ", slimsTo=" + slimsTo +
                    ", descendants=" + descendants +
                    '}';
        }
    }
}
