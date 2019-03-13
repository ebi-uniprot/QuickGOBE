package uk.ac.ebi.quickgo.index.annotation;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.RAW_GP_RELATED_GO_IDS_REGEX;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.PROPS_GP_RELATED_GO_IDS_REGEX;

public class AnnotationParsingHelperTest {

  @Test
  public void emptyString_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("");
    assertFalse(matcher.matches());
  }

  @Test
  public void OnlyGo_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:");
    assertFalse(matcher.matches());
  }

  @Test
  public void OnlyGoInSecondPlace_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:1,GO:");
    assertFalse(matcher.matches());
  }

  @Test
  public void singleGoTerm_AsGpRelatedGoIds_IsValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0005886");
    assertTrue(matcher.matches());
  }

  @Test
  public void twoGoTermsSeparatedWithComma_AsGpRelatedGoIds_AreValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0005886,GO:0009966");
    assertTrue(matcher.matches());
  }

  @Test
  public void multipleGoTermsSeparatedWithComma_AsGpRelatedGoIds_AreValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0005886,GO:0009966,GO:1902724,GO:1902725,GO:1902726");
    assertTrue(matcher.matches());
  }

  @Test
  public void commaInEndOfGoTerm_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0062023,GO:1902723,");
    assertFalse(matcher.matches());
  }

  @Test
  public void smallCaseGo_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("go:0062023,Go:1902723,gO:0005886");
    assertFalse(matcher.matches());
  }

  @Test
  public void gpRelatedGoIdsPropertyNameShouldBe_gp_related_go_ids(){
    Matcher matcher = PROPS_GP_RELATED_GO_IDS_REGEX.matcher("gp_related_go_ids=GO:1");
    assertTrue(matcher.matches());
  }

  @Test
  public void gpRelatedGoIdsWrongPropertyNameShouldBeFail(){
    Matcher matcher = PROPS_GP_RELATED_GO_IDS_REGEX.matcher("g_related_go_ids=GO:1");
    assertFalse(matcher.matches());
  }

  @Test
  public void propertyNameShouldBeSeparatedByEqualSign(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("gp_related_go_ids:G0:1");
    assertFalse(matcher.matches());
  }

  @Test
  public void propertyNameShouldBeLowerCaseOnly(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("gp_related_GO_ids:G0:1");
    assertFalse(matcher.matches());
  }
}
