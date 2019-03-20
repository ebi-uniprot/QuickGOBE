package uk.ac.ebi.quickgo.annotation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class AnnotationRequestBody {
  private GoDescription and;
  private GoDescription not;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GoDescription {
    private List<String> goTerms;
    private String[] goUsageRelationships;
    private String goUsage;
  }
}


