package uk.ac.ebi.quickgo.graphics.ontology;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.ebi.quickgo.graphics.ontology.GraphPresentation.*;

/**
 * The GraphPresentation is a data class, test it's creation, the setting of defaults.
 * @author Tony Wardell
 * Date: 13/02/2018
 * Time: 15:17
 * Created with IntelliJ IDEA.
 */
class GraphPresentationTest {
    private final GraphPresentation style = new GraphPresentation.Builder().build();
    private final GraphPresentation style22 = new GraphPresentation.Builder().fontSize(22).build();
    private final GraphPresentation style33 = new GraphPresentation.Builder().fontSize(33).build();
    private final GraphPresentation style40 = new GraphPresentation.Builder().fontSize(40).build();

    @Test
    void usingBuilderWithNoArgumentsUsesDefaults() {
        GraphPresentation graphPresentation = style;
        assertThat(defaultShowKey, is(equalTo(graphPresentation.key)));
        assertThat(defaultShowTermIds, is(equalTo(graphPresentation.termIds)));
        assertThat(defaultWidth, is(equalTo(graphPresentation.width)));
        assertThat(defaultHeight, is(equalTo(graphPresentation.height)));
        assertThat(defaultShowSlimColours, is(equalTo(graphPresentation.subsetColours)));
        assertThat(defaultShowChildren, is(equalTo(graphPresentation.showChildren)));
        assertThat(defaultFontSize, is(equalTo(graphPresentation.fontSize)));
    }

    @Test
    void suppliedArgumentsAreUsed() {
        GraphPresentation.Builder builder = new GraphPresentation.Builder();
        builder.showKey(!defaultShowKey)
                .showIDs(!defaultShowTermIds)
                .termBoxWidth(defaultWidth + 50)
                .termBoxHeight(defaultHeight + 30)
                .showSlimColours(!defaultShowSlimColours)
                .showChildren(!defaultShowChildren)
                .fontSize(defaultFontSize + 10);
        GraphPresentation graphPresentation = builder.build();
        assertThat(!defaultShowKey, is(equalTo(graphPresentation.key)));
        assertThat(!defaultShowTermIds, is(equalTo(graphPresentation.termIds)));
        assertThat(defaultWidth + 50, is(equalTo(graphPresentation.width)));
        assertThat(defaultHeight + 30, is(equalTo(graphPresentation.height)));
        assertThat(!defaultShowSlimColours, is(equalTo(graphPresentation.subsetColours)));
        assertThat(!defaultShowChildren, is(equalTo(graphPresentation.showChildren)));
        assertThat(defaultFontSize + 10, is(equalTo(graphPresentation.fontSize)));
    }

    @Nested
    class relativeSize{
        @Test
        void font11ShouldReturn1() {
            int retVal = style.relativeSize(1);
            assertThat(retVal, is(1));
        }

        @Test
        void font11ShouldReturn2() {
            int retVal = style.relativeSize(2);
            assertThat(retVal, is(2));
        }

        @Test
        void font11ShouldReturn5() {
            int retVal = style.relativeSize(5);
            assertThat(retVal, is(5));
        }

        @Test
        void font22ShouldReturn2() {
            int retVal = style22.relativeSize(1);
            assertThat(retVal, is(2));
        }

        @Test
        void font22ShouldReturn4() {
            int retVal = style22.relativeSize(2);
            assertThat(retVal, is(4));
        }

        @Test
        void font22ShouldReturn10() {
            int retVal = style22.relativeSize(5);
            assertThat(retVal, is(10));
        }

        @Test
        void font33ShouldReturn2() {
            int retVal = style33.relativeSize(1);
            assertThat(retVal, is(3));
        }

        @Test
        void font33ShouldReturn8() {
            int retVal = style33.relativeSize(2);
            assertThat(retVal, is(6));
        }

        @Test
        void font33ShouldReturn15() {
            int retVal = style33.relativeSize(5);
            assertThat(retVal, is(15));
        }
    }

    @Nested
    class getSlimBoxWidth {
        @Test
        void defaultWillAdd55() {
            assertThat(style.getSlimBoxWidth(), is(defaultWidth + 55));
        }

        @Test
        void fontSizeIncrease_double() {
            int halfOfTheIncreaseMultiply55 = 55 * 2 / 2;
            assertThat(style22.getSlimBoxWidth(), is(defaultWidth + 55 + halfOfTheIncreaseMultiply55));
        }

        @Test
        void fontSizeIncrease_triple() {
            int halfOfTheIncreaseMultiply55 = 55 * 3 / 2;
            assertThat(style33.getSlimBoxWidth(), is(defaultWidth + 55 + halfOfTheIncreaseMultiply55));
        }

        @Test
        void fontSizeIncrease40() {
            int halfOfTheIncreaseMultiply55 = (int) ((40f/11f) / 2 * 55);
            assertThat(style40.getSlimBoxWidth(), is(defaultWidth + 55 + halfOfTheIncreaseMultiply55));
        }
    }

    @Nested
    class getArrowHeadStyleSize {
        @Test
        void defaultHeadSize_forDefaultFontSizeReturns2() {
            assertThat(style.getArrowHeadStyleSize(), is(2F));
        }

        @Test
        void arrowHeadShouldIncreaseInSize_asFontSizeIncrease() {
            assertThat(style22.getArrowHeadStyleSize(), is(6F));
        }

        @Test
        void arrowHeadIncrease_Triple() {
            assertThat(style33.getArrowHeadStyleSize(), is(9F));
        }

        @Test
        void arrowHeadIncrease_isNotCubePropotional() {
            assertThat(style40.getArrowHeadStyleSize(), is(10.909091F));
        }

        @Test
        void arrowHeadStyleWidthIs_getArrowHeadStyleSize() {
            assertThat(((BasicStroke)style33.getArrowHeadStyle()).getLineWidth(), is(style33.getArrowHeadStyleSize()));
        }
    }

    @Nested
    class relativeFontSizeIncrease{
        @Test
        void fontSizeRelativeToDefault() {
            assertThat(style.relativeFontSizeIncrease(), is(1F));
        }

        @Test
        void fontSizeRelativeToDefault_whenSizeIsDouble() {
            assertThat(style22.relativeFontSizeIncrease(), is(2F));
        }
    }

    @Nested
    class getBoxBorderSize{
        @Test
        void defaultSizeIs1() {
            assertThat(style.getBoxBorderSize(), is(1F));
        }

        @Test
        void borderIsRelativeToFontSize_whenSizeIncrease() {
            assertThat(style22.getBoxBorderSize(), is(2F));
        }

        @Test
        void BoxBorderStyleWidthIs_getBoxBorderSize() {
            assertThat(((BasicStroke)style40.getBoxBorder()).getLineWidth(), is(style40.getBoxBorderSize()));
        }
    }

    @Nested
    class getIdHeaderFontSize{
        @Test
        void headerSizeWillAlwaysBeGreaterThanFontSize() {
            assertThat(style.getIdHeaderFontSize(), is(style.fontSize + 1));
        }

        @Test
        void headerSizeWillIncreaseWithFontSize() {
            assertThat(style40.getIdHeaderFontSize(), is(style40.fontSize + 1));
        }
    }

    @Nested
    class arrowLineRelativeFont{
        @Test
        void onlyAcceptsBasicStroke() {
            Stroke stroke = p -> null;

            assertThrows(ClassCastException.class, ()->style.arrowLineRelativeFont(stroke));
        }

        @Test
        void ParamStrokeAndReturnedStrokeAreNotSame() {
            BasicStroke param = new BasicStroke(3);
            assertNotSame(param, style.arrowLineRelativeFont(param));
        }

        @Test
        void lineSizeWillBeReletiveToFont() {
            BasicStroke param = new BasicStroke(10);
            assertEquals(30, ((BasicStroke)style33.arrowLineRelativeFont(param)).getLineWidth());
        }
    }

    @Nested
    class publicObjectTest{
        @Test
        void fontWillBeCreateWithFontSize() {
            assertEquals(40, style40.font.getSize());
        }

        @Test
        void labelFont_willBeLess1_toFontSize() {
            assertEquals(defaultFontSize -1, style.labelFont.getSize());
        }

        @Test
        void labelFont_willBeLess1_toFontSize_relative() {
            assertEquals(style33.fontSize - 3, style33.labelFont.getSize());
        }

        @Test
        void infoFont_willBeLess2_toFontSize() {
            assertEquals(defaultFontSize - 2, style.infoFont.getSize());
        }

        @Test
        void infoFont_willBeLess2_toFontSize_relative() {
            assertEquals(style40.fontSize - 6, style40.infoFont.getSize());
        }

        @Test
        void errorFont_willBeMore5_toFontSize() {
            assertEquals(defaultFontSize + 5, style.errorFont.getSize());
        }

        @Test
        void errorFont_willBeMore5_toFontSize_relative() {
            assertEquals(style22.fontSize + 10, style22.errorFont.getSize());
        }
    }

    @Nested
    class getBottomMargin{
        private final int defaultBottomMargin =16;
        @Test
        void withDefaultFontSizeIt_willBe16() {
            assertEquals(defaultBottomMargin, style.getBottomMargin());
        }

        @Test
        void whenSizeDoubleMarginBecomeDouble() {
            assertEquals(defaultBottomMargin * 2, style22.getBottomMargin());
        }

        @Test
        void whenSizeIncreaseMarginIncrease() {
            assertEquals(defaultBottomMargin * 3, style33.getBottomMargin());
        }
    }
}
