package uk.ac.ebi.quickgo.index.annotation;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.RAW_GP_RELATED_GO_IDS_REGEX;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.PROPS_GP_RELATED_GO_IDS_REGEX;

class AnnotationParsingHelperTest {

  @Test
  void emptyString_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("");
    assertFalse(matcher.matches());
  }

  @Test
  void OnlyGo_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:");
    assertFalse(matcher.matches());
  }

  @Test
  void OnlyGoInSecondPlace_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:1,GO:");
    assertFalse(matcher.matches());
  }

  @Test
  void singleGoTerm_AsGpRelatedGoIds_IsValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0005886");
    assertTrue(matcher.matches());
  }

  @Test
  void twoGoTermsSeparatedWithComma_AsGpRelatedGoIds_AreValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0005886,GO:0009966");
    assertTrue(matcher.matches());
  }

  @Test
  void multipleGoTermsSeparatedWithComma_AsGpRelatedGoIds_AreValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0005886,GO:0009966,GO:1902724,GO:1902725,GO:1902726");
    assertTrue(matcher.matches());
  }

  @Test
  void commaInEndOfGoTerm_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0062023,GO:1902723,");
    assertFalse(matcher.matches());
  }

  @Test
  void smallCaseGo_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("go:0062023,Go:1902723,gO:0005886");
    assertFalse(matcher.matches());
  }

  @Test
  void gpRelatedGoIdsPropertyNameShouldBe_gp_related_go_ids(){
    Matcher matcher = PROPS_GP_RELATED_GO_IDS_REGEX.matcher("gp_related_go_ids=GO:1");
    assertTrue(matcher.matches());
  }

  @Test
  void gpRelatedGoIdsWrongPropertyNameShouldBeFail(){
    Matcher matcher = PROPS_GP_RELATED_GO_IDS_REGEX.matcher("g_related_go_ids=GO:1");
    assertFalse(matcher.matches());
  }

  @Test
  void propertyNameShouldBeSeparatedByEqualSign(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("gp_related_go_ids:G0:1");
    assertFalse(matcher.matches());
  }

  @Test
  void propertyNameShouldBeLowerCaseOnly(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("gp_related_GO_ids:G0:1");
    assertFalse(matcher.matches());
  }
}
