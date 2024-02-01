package uk.ac.ebi.quickgo.graphics.ontology;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.model.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.ebi.quickgo.graphics.ontology.TermNode.*;

class TermNodeTest {
  private GraphPresentation style = new GraphPresentation.Builder().build();

  @Nested
  class slimWidth_coloredMarkerAtBottomWhenShowSlimColorIsTrue{
    @Test
    void slimBoxWidthFor85_willBe10() {
      TermNode node = new TermNode("name", "id", style);
      assertEquals(10, node.slimWidth());
    }

    @Test
    void whenWidthDouble_sizeWillBeDouble() {
      style  = new GraphPresentation.Builder().termBoxWidth(85 * 2).build();;
      TermNode node = new TermNode("name", "id", style);
      assertEquals(20, node.slimWidth());
    }

    @Test
    void toGetReasonableWidth_shouldBeDividedWith8point5(){
      style  = new GraphPresentation.Builder().termBoxWidth(102).build();;
      TermNode node = new TermNode("name", "id", style);
      assertEquals((int)(102/8.5), node.slimWidth());
    }
  }

  @Nested
  class slimHeight_coloredMarkerAtBottomWhenShowSlimColorIsTrue{
    @Test
    void slimBoxHeightFor55_willBe4() {
      TermNode node = new TermNode("name", "id", style);
      assertEquals(4, node.slimHeight());
    }

    @Test
    void whenHeightTriple_sizeWillBeTriple() {
      style  = new GraphPresentation.Builder().termBoxHeight(55 * 3).build();;
      TermNode node = new TermNode("name", "id", style);
      assertEquals(12, node.slimHeight());
    }

    @Test
    void toGetReasonableHeight_shouldBeDividedWith13point75(){
      style  = new GraphPresentation.Builder().termBoxHeight(1120).build();;
      TermNode node = new TermNode("name", "id", style);
      assertEquals((int)(1120/13.75), node.slimHeight());
    }
  }

  @Nested
  class TermIdBackGroundColor {
    private final Color defaultColor = defaultBoxHeaderBackgroundColor;

    @Test
    void defaultColor_whenTermIsNotProvided() {
      TermNode node = new TermNode("name", "id", style);
      assertEquals(defaultColor, node.getTermIdBackgroundColor());
    }

    @Test
    void defaultColor_ecoTerm_generic() {
      TermNode node = new TermNode(new GenericTerm("ECO:123", "test", "true"), style);
      assertEquals(defaultColor, node.getTermIdBackgroundColor());
    }

    @Test
    void defaultColor_ecoTerm() {
      ECOTerm term = new ECOTerm();
      term.name = "test";
      term.id = "ECO:101";
      term.isObsolete = false;
      TermNode node = new TermNode(term, style);
      assertEquals(defaultColor, node.getTermIdBackgroundColor());
    }

    @Test
    void defaultColor_goRootObsoleteTerm_generic() {
      TermNode node = new TermNode(new GenericTerm("GO:0003673", "obsolete Gene_Ontology", "true"), style);
      assertEquals(defaultColor, node.getTermIdBackgroundColor());
    }

    @Test
    void defaultColor_goRootObsoleteTerm() {
      TermNode node = new TermNode(new GOTerm("GO:0003673", "obsolete Gene_Ontology", "R", "true"), style);
      assertEquals(defaultColor, node.getTermIdBackgroundColor());
    }

    @Test
    void processGoTermWillHaveDefaultColor() {
      TermNode node = new TermNode(new GOTerm("GO:000001", "process", "P", "false"), style);
      assertEquals(defaultColor, node.getTermIdBackgroundColor());
    }

    @Test
    void functionGoTerm_darkGrayColor() {
      TermNode node = new TermNode(new GOTerm("GO:000002", "component", "F", "false"), style);
      assertEquals(functionGoTermBoxHeaderBgColor, node.getTermIdBackgroundColor());
    }

    @Test
    void componentGoTerm_darkerKellyColor() {
      TermNode node = new TermNode(new GOTerm("GO:000003", "function", "C", "false"), style);
      assertEquals(componentGoTermBoxHeaderBgColor, node.getTermIdBackgroundColor());
    }
  }
}