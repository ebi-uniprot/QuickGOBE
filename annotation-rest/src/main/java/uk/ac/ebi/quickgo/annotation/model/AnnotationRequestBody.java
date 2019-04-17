package uk.ac.ebi.quickgo.annotation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.DEFAULT_GO_USAGE;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.USAGE_RELATIONSHIP_PARAM;
import static uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern.Flag.CASE_INSENSITIVE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationRequestBody {
  private GoDescription and;
  private GoDescription not;

  public static void putDefaultValuesIfAbsent(AnnotationRequestBody requestBody) {
    if(requestBody == null)
      return;
    if (requestBody.getAnd() == null) {
      requestBody.setAnd(new AnnotationRequestBody.GoDescription());
    }
    fillDefaultGoDescriptionIfNotPresent(requestBody.getAnd());

    if (requestBody.getNot() == null) {
      requestBody.setNot(new AnnotationRequestBody.GoDescription());
    }
    fillDefaultGoDescriptionIfNotPresent(requestBody.getNot());

  }

  private static void fillDefaultGoDescriptionIfNotPresent(AnnotationRequestBody.GoDescription goDescription) {
    if (goDescription.getGoTerms() == null) {
      goDescription.setGoTerms(new ArrayList<>());
    }
    if (goDescription.getGoUsage() == null || goDescription.getGoUsage().trim().isEmpty()) {
      goDescription.setGoUsage(DEFAULT_GO_USAGE);
    }
    if (goDescription.getGoUsageRelationships() == null || goDescription.getGoUsageRelationships().length == 0) {
      goDescription.setGoUsageRelationships(AnnotationRequest.DEFAULT_GO_USAGE_RELATIONSHIPS);
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GoDescription {
    private List<String> goTerms;
    private String[] goUsageRelationships;
    private String goUsage;

    public void setGoUsageRelationships(String[] goUsageRelationships) {
      this.goUsageRelationships = Stream.of(goUsageRelationships)
        .map(String::toLowerCase)
        .toArray(String[]::new);
    }

    public void setGoUsage(String goUsage) {
      this.goUsage = goUsage == null ? null : goUsage.toLowerCase();
    }

    @ArrayPattern(regexp = "^GO:[0-9]{7}$", flags = CASE_INSENSITIVE, paramName = "goTerms")
    public List<String> getGoTerms() {
      return goTerms;
    }

    @ArrayPattern(regexp = "^is_a|part_of|occurs_in|regulates$", flags = CASE_INSENSITIVE,
      paramName = USAGE_RELATIONSHIP_PARAM)
    public String[] getGoUsageRelationships() {
      return goUsageRelationships;
    }

    @Pattern(regexp = "^slim|descendants|exact$", flags = Pattern.Flag.CASE_INSENSITIVE,
      message = "Invalid goUsage: ${validatedValue}")
    public String getGoUsage() {
      return goUsage;
    }

    public static class GoDescriptionBuilder {
      public GoDescriptionBuilder goUsageRelationships(String... goUsageRelationships) {
        this.goUsageRelationships = Stream.of(goUsageRelationships)
          .map(String::toLowerCase)
          .toArray(String[]::new);
        return this;
      }

      public GoDescriptionBuilder goUsage(String goUsage) {
        this.goUsage = goUsage == null ? null : goUsage.toLowerCase();
        return this;
      }
    }
  }
}


