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

  @Nested
  class TermIdBackGroundColor {
    private Color defaultColor = defaultBoxHeaderBackgroundColor;
    private GraphPresentation style = new GraphPresentation.Builder().build();

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