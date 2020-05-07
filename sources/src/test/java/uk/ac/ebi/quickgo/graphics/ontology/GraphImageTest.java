package uk.ac.ebi.quickgo.graphics.ontology;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.ac.ebi.quickgo.graphics.ontology.GraphPresentation.defaultHeight;
import static uk.ac.ebi.quickgo.graphics.ontology.TermNode.*;

class GraphImageTest {

  @Nested
  class DrawCompleteGoNodeHeaderColorInformation {
    private int height300 = 300;
    private GraphPresentation defaultStyle = new GraphPresentation.Builder().build();
    private GraphPresentation style300 = new GraphPresentation.Builder().termBoxHeight(height300).build();

    @Nested
    class getGoNodeHeaderColorInformationHeight {
      @Test
      void single_heightWillAlwaysBeHalfOfStyleHeight_default() {
        GraphImage gi = graphImage(defaultStyle);

        assertEquals(defaultHeight / 2, gi.getGoNodeHeaderColorInformationHeight());
      }

      @Test
      void single_heightWillAlwaysBeHalfOfStyleHeight_custom() {
        GraphImage gi = graphImage(style300);

        assertEquals(height300 / 2, gi.getGoNodeHeaderColorInformationHeight());
      }
    }

    @Nested
    class computeHeightGoNodeHeaderColorInformation {
      @Test
      void zeroTotalHeight_whenTermsAreEmpty_default() {
        GraphImage gi = graphImage(defaultStyle);

        assertEquals(0, gi.computeHeightGoNodeHeaderColorInformation());
      }

      @Test
      void zeroTotalHeight_whenTermsAreEmpty_custom() {
        GraphImage gi = graphImage(style300);

        assertEquals(0, gi.computeHeightGoNodeHeaderColorInformation());
      }

      @Test
      void zero_totalHeight_default_whenTermsAreNotGoTerms() {
        List<TermNode> terms = Collections.singletonList(new TermNode("name", "id", defaultStyle));
        GraphImage gi = graphImage(defaultStyle, terms);

        assertEquals(0, gi.computeHeightGoNodeHeaderColorInformation());
      }

      @Test
      void zero_totalHeight_custom_whenTermsAreNotGoTerms() {
        List<TermNode> terms = Collections.singletonList(new TermNode("name", "id", style300));
        GraphImage gi = graphImage(style300, terms);

        assertEquals(0, gi.computeHeightGoNodeHeaderColorInformation());
      }

      @Test
      void totalHeightWillBe_3AspectGoPlusExtra_default_onlyWhenTermsAreGoTerms() {
        GOTerm term = new GOTerm("GO:0001", "go term", "P", "false");
        List<TermNode> terms = Collections.singletonList(new TermNode(term, defaultStyle));
        GraphImage gi = graphImage(defaultStyle, terms);

        assertEquals((defaultHeight / 2) * 4, gi.computeHeightGoNodeHeaderColorInformation());
      }

      @Test
      void totalHeightWillBe_3GoAspectPlusExtra_custom_onlyWhenTermsAreGoTerms() {
        GOTerm term = new GOTerm("GO:0001", "go term", "P", "false");
        List<TermNode> terms = Collections.singletonList(new TermNode(term, style300));
        GraphImage gi = graphImage(style300, terms);

        assertEquals(height300 * 2, gi.computeHeightGoNodeHeaderColorInformation());
      }
    }

    @Nested
    class isDisplayGoNodeHeaderColorInformation {
      private GraphPresentation styleKeyFalse = new GraphPresentation.Builder().showKey(false).build();

      @Test
      void showIdsFalse_willNotShow_informationGraph() {
        GraphPresentation styleShowIdsFalse = new GraphPresentation.Builder().showIDs(false).showKey(true).build();
        GraphImage gi = graphImage(styleShowIdsFalse);

        assertFalse(gi.isDisplayGoNodeHeaderColorInformation());
      }

      @Test
      void keyOff_idOff_willNotShow_informationGraph() {
        GraphPresentation styleShowIdsFalse = new GraphPresentation.Builder().showIDs(false).showKey(false).build();
        GraphImage gi = graphImage(styleShowIdsFalse);

        assertFalse(gi.isDisplayGoNodeHeaderColorInformation());
      }

      @Test
      void keyOff_willNotShow_informationGraph() {
        GraphImage gi = graphImage(styleKeyFalse);

        assertFalse(gi.isDisplayGoNodeHeaderColorInformation());
      }

      @Test
      void whenTermsAreEmpty_willNotShow_informationGraph() {
        GraphImage gi = graphImage(defaultStyle);

        assertFalse(gi.isDisplayGoNodeHeaderColorInformation());
      }

      @Test
      void whenTermsNotContainGoTerms_willNotShow_informationGraph() {
        List<TermNode> terms = Collections.singletonList(new TermNode("name", "id", defaultStyle));
        GraphImage gi = graphImage(defaultStyle, terms);

        assertFalse(gi.isDisplayGoNodeHeaderColorInformation());
      }

      @Test
      void secondTermIsGo_willNotShowGraph_assumeTermsListContainSameOntologyTypes() {
        GOTerm term = new GOTerm("GO:0001", "go term", "P", "false");
        List<TermNode> terms = Arrays.asList(new TermNode("ECO:0001", "eco", defaultStyle), new TermNode(term, defaultStyle));
        GraphImage gi = graphImage(defaultStyle, terms);

        assertFalse(gi.isDisplayGoNodeHeaderColorInformation());
      }

      @Test
      void keyOff_withGoTerms_willNotShowGraph() {
        GOTerm term = new GOTerm("GO:0001", "go term", "P", "false");
        List<TermNode> terms = Collections.singletonList(new TermNode(term, styleKeyFalse));
        GraphImage gi = graphImage(styleKeyFalse, terms);

        assertFalse(gi.isDisplayGoNodeHeaderColorInformation());
      }

      @Test
      void onlyShowInformationGraph_whenFirstTermIsQuickgoTermAndKeyOn() {
        GOTerm term = new GOTerm("GO:0001", "go term", "P", "false");
        List<TermNode> terms = Collections.singletonList(new TermNode(term, defaultStyle));
        GraphImage gi = graphImage(defaultStyle, terms);

        assertTrue(gi.isDisplayGoNodeHeaderColorInformation());
      }
    }

    @Nested
    class drawGoNodeHeaderColorInformation {
      @Test
      void canSetProvidedColorForBg_and_whiteColorForWritingText() {
        GraphImage gi = spy(graphImage(defaultStyle));
        Graphics2D g2 = mock(Graphics2D.class);
        FontMetrics fontMetrics = mock(FontMetrics.class);

        when(g2.getFontMetrics()).thenReturn(fontMetrics);
        when(fontMetrics.getStringBounds(anyString(), any())).thenReturn(mock(Rectangle2D.class));

        gi.drawGoNodeHeaderColorInformation("text", Color.black, g2, 5);

        verify(g2, times(1)).setColor(Color.black);
        verify(g2, times(1)).setColor(Color.white);
      }

      @Test
      void canDrawProvidedString() {
        GraphImage gi = spy(graphImage(defaultStyle));
        Graphics2D g2 = mock(Graphics2D.class);
        FontMetrics fontMetrics = mock(FontMetrics.class);

        when(g2.getFontMetrics()).thenReturn(fontMetrics);
        when(fontMetrics.getStringBounds(anyString(), any())).thenReturn(mock(Rectangle2D.class));

        gi.drawGoNodeHeaderColorInformation("text", Color.black, g2, 5);

        verify(fontMetrics, times(1)).getStringBounds("text", g2);
        verify(g2).drawString(eq("text"), anyFloat(), anyFloat());
      }
    }

    @Nested
    class drawCompleteGoNodeHeaderColorInformation {
      @Test
      void willNotDrawKeyInformationChart_whenKeyIsOff() {
        GraphImage gi = spy(graphImage(defaultStyle));

        doReturn(false).when(gi).isDisplayGoNodeHeaderColorInformation();

        gi.drawCompleteGoNodeHeaderColorInformation(null);

        verify(gi, times(1)).drawCompleteGoNodeHeaderColorInformation(null);
        verify(gi, times(1)).isDisplayGoNodeHeaderColorInformation();
        verifyNoMoreInteractions(gi);
      }

      @Test
      void canCreate3InformationKeys_whenKeyOnAndGraphIsGoTerms() {
        GraphImage gi = spy(graphImage(defaultStyle));
        Graphics2D g2 = mock(Graphics2D.class);
        FontMetrics fontMetrics = mock(FontMetrics.class);

        doReturn(true).when(gi).isDisplayGoNodeHeaderColorInformation();
        when(g2.getFontMetrics()).thenReturn(fontMetrics);
        when(fontMetrics.getStringBounds(anyString(), any())).thenReturn(mock(Rectangle2D.class));

        gi.drawCompleteGoNodeHeaderColorInformation(g2);

        verify(gi, times(1)).drawCompleteGoNodeHeaderColorInformation(g2);
        verify(gi, times(1)).isDisplayGoNodeHeaderColorInformation();
        verify(gi, times(1)).drawGoNodeHeaderColorInformation("Process", defaultBoxHeaderBackgroundColor, g2, 1);
        verify(gi, times(1)).drawGoNodeHeaderColorInformation("Function", functionGoTermBoxHeaderBgColor, g2, 2);
        verify(gi, times(1)).drawGoNodeHeaderColorInformation("Component", componentGoTermBoxHeaderBgColor, g2, 3);
        verify(gi, times(3)).getGoNodeHeaderColorInformationHeight();
        verifyNoMoreInteractions(gi);
      }
    }

    private GraphImage graphImage(GraphPresentation style) {
      return graphImage(style, Collections.emptyList());
    }

    private GraphImage graphImage(GraphPresentation style, List<TermNode> terms) {
      return new GraphImage(0, 0, terms, null, style, Collections.emptyList());
    }
  }
}