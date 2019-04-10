package uk.ac.ebi.quickgo.ontology.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class OBOMinimum {
  public String id;
  public String name;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public List<Children> children;

  public static class Children extends OBOMinimum {
    public OntologyRelationType relation;
    public boolean hasChildren;
  }
}
