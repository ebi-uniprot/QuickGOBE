package uk.ac.ebi.quickgo.index.annotation;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.RAW_GP_RELATED_GO_IDS_REGEX;

public class AnnotationParsingHelperTest {

  @Test
  public void emptyString_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("");
    Assert.assertFalse(matcher.matches());
  }

  @Test
  public void singleGoTerm_AsGpRelatedGoIds_IsValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0005886");
    Assert.assertTrue(matcher.matches());
  }

  @Test
  public void twoGoTermsSeparatedWithComma_AsGpRelatedGoIds_AreValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0005886,GO:0009966");
    Assert.assertTrue(matcher.matches());
  }

  @Test
  public void multipleGoTermsSeparatedWithComma_AsGpRelatedGoIds_AreValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0005886,GO:0009966,GO:1902724,GO:1902725,GO:1902726");
    Assert.assertTrue(matcher.matches());
  }

  @Test
  public void commaInEndOfGoTerm_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("GO:0062023,GO:1902723,");
    Assert.assertFalse(matcher.matches());
  }

  @Test
  public void smallCaseGo_AsGpRelatedGoIds_IsNotValid(){
    Matcher matcher = RAW_GP_RELATED_GO_IDS_REGEX.matcher("go:0062023,Go:1902723,gO:0005886");
    Assert.assertFalse(matcher.matches());
  }
}
