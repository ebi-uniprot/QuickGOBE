package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

import java.util.List;

/**
 * Represents part of the model corresponding to the response available from the resource:
 *
 * <ul>
 *     <li>/go/terms/{term}</li>
 * </ul>
 *
 * or
 *
 * <ul>
 *     <li>/eco/terms/{term}</li>
 * </ul>
 *
 * This model captures the parts reached by the JSON path expression, "$.results.name".
 *
 * Created 07/04/17
 * @author Edd
 */
public class BasicOntology implements ResponseType {
    private List<Result> results;

    public BasicOntology() {}

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

        @Override public String toString() {
            return "Result{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
