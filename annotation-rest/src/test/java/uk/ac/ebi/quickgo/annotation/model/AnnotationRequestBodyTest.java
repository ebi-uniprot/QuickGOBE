package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.DEFAULT_GO_USAGE;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.DEFAULT_GO_USAGE_RELATIONSHIPS;

public class AnnotationRequestBodyTest {

  @Test
  public void beforeDefaultValues() {
    AnnotationRequestBody requestBody = AnnotationRequestBody.builder().build();

    assertThat(requestBody, notNullValue());
    assertThat(requestBody.getAnd(), nullValue());
    assertThat(requestBody.getNot(), nullValue());
  }

  @Test
  public void defaultValues() {
    AnnotationRequestBody requestBody = AnnotationRequestBody.builder().build();

    AnnotationRequestBody.putDefaultValuesIfAbsent(requestBody);

    assertThat(requestBody, notNullValue());
    assertThat(requestBody.getAnd(), notNullValue());
    assertThat(requestBody.getAnd().getGoTerms(), empty());
    assertThat(requestBody.getAnd().getGoUsage(), is(DEFAULT_GO_USAGE));
    assertThat(requestBody.getAnd().getGoUsageRelationships(), arrayContaining(DEFAULT_GO_USAGE_RELATIONSHIPS));

    assertThat(requestBody.getNot(), notNullValue());
    assertThat(requestBody.getNot().getGoTerms(), empty());
    assertThat(requestBody.getNot().getGoUsage(), is(DEFAULT_GO_USAGE));
    assertThat(requestBody.getNot().getGoUsageRelationships(), arrayContaining(DEFAULT_GO_USAGE_RELATIONSHIPS));
  }

  @Test
  public void relationshipPresent_defaultGoUsage() {
    AnnotationRequestBody body = AnnotationRequestBody.builder()
      .and(AnnotationRequestBody.GoDescription.builder().goUsageRelationships(new String[]{"is_A"}).build())
      .build();
    AnnotationRequestBody.putDefaultValuesIfAbsent(body);

    assertThat(body.getAnd().getGoUsage(), is(DEFAULT_GO_USAGE));
  }

  @Test
  public void usagePresent_defaultGoUsageRelations() {
    AnnotationRequestBody body = AnnotationRequestBody.builder()
      .not(AnnotationRequestBody.GoDescription.builder().goUsage("abc").build())
      .build();

    AnnotationRequestBody.putDefaultValuesIfAbsent(body);

    assertThat(body.getNot().getGoUsageRelationships(), arrayContaining(DEFAULT_GO_USAGE_RELATIONSHIPS));
  }

  @Test
  public void multipleGoUsageRelationships_shouldBeLowerCase() {
    AnnotationRequestBody body = AnnotationRequestBody.builder()
      .and(AnnotationRequestBody.GoDescription.builder().goUsageRelationships(new String[]{"is_A", "Part_OF"}).build())
      .build();
    AnnotationRequestBody.putDefaultValuesIfAbsent(body);

    assertThat(body.getAnd().getGoUsageRelationships(), arrayContaining("is_a", "part_of"));
  }

  @Test
  public void singleGoUsageRelationships_shouldBeLowerCase() {
    AnnotationRequestBody body = AnnotationRequestBody.builder()
      .and(AnnotationRequestBody.GoDescription.builder().goUsageRelationships("is_A").build())
      .build();
    AnnotationRequestBody.putDefaultValuesIfAbsent(body);

    assertThat(body.getAnd().getGoUsageRelationships(), arrayContaining("is_a"));
  }

  @Test
  public void goUsageRelationshipsFromSetter_shouldBeLowerCase() {
    AnnotationRequestBody.GoDescription and = new AnnotationRequestBody.GoDescription();
    and.setGoUsageRelationships(new String[]{"RELATES_to"});
    AnnotationRequestBody body = AnnotationRequestBody.builder()
      .and(and)
      .build();
    AnnotationRequestBody.putDefaultValuesIfAbsent(body);

    assertThat(body.getAnd().getGoUsageRelationships(), arrayContaining("relates_to"));
  }

  @Test
  public void goUsageFromBuilder_shouldBeLowerCase() {
    AnnotationRequestBody body = AnnotationRequestBody.builder()
      .not(AnnotationRequestBody.GoDescription.builder().goUsage("abcD").build())
      .build();

    AnnotationRequestBody.putDefaultValuesIfAbsent(body);

    assertThat(body.getNot().getGoUsage(), is("abcd"));
  }

  @Test
  public void goUsageFromSetter_shouldBeLowerCase() {
    AnnotationRequestBody.GoDescription not = new AnnotationRequestBody.GoDescription();
    not.setGoUsage("ABCD");
    AnnotationRequestBody body = AnnotationRequestBody.builder()
      .not(not)
      .build();

    AnnotationRequestBody.putDefaultValuesIfAbsent(body);

    assertThat(body.getNot().getGoUsage(), is("abcd"));
  }

  @Test
  public void goUsage_nullTest() {
    AnnotationRequestBody body = AnnotationRequestBody.builder()
      .not(AnnotationRequestBody.GoDescription.builder().goUsage(null).build())
      .build();

    AnnotationRequestBody.putDefaultValuesIfAbsent(body);

    assertThat(body.getNot().getGoUsage(), is(DEFAULT_GO_USAGE));
  }
}
