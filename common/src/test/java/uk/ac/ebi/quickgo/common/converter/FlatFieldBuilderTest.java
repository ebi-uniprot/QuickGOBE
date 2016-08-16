package uk.ac.ebi.quickgo.common.converter;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.*;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 26/11/15
 * @author Edd
 */
public class FlatFieldBuilderTest {
    @Test
    public void createStringUsingEmptyFlatField() throws Exception {
        FlatField emptyFlatField = newFlatField();

        String actualString = emptyFlatField.buildString();

        assertThat(actualString, is("[]"));
    }

    @Test
    public void createStringUsingFlatFieldWith1Value() throws Exception {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"));

        String actualString = flatField.buildString();

        assertThat(actualString, is("[level1:A]"));
    }

    @Test
    public void createStringUsingFlatFieldWith2ValuesAtLevel1() throws Exception {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatFieldLeaf("level1:B"));

        String actualString = flatField.buildString();

        assertThat(actualString, is("[level1:A|level1:B]"));
    }

    @Test
    public void createStringUsingFlatFieldWith2Values1AtEachLevel() throws Exception {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A")));

        String actualString = flatField.buildString();

        assertThat(actualString, is("[level1:A|[level2:A]]"));
    }

    @Test
    public void createStringWithFlatFieldWith2ValuesAtFirstLevelAnd1ValueAtSecondLevel() throws Exception {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A")))
                .addField(newFlatFieldLeaf("level1:B"));

        String actualString = flatField.buildString();

        assertThat(actualString, is("[level1:A|[level2:A]|level1:B]"));
    }

    @Test
    public void createStringUsingFlatFieldWith2ValuesAtSecondLevelAnd1ValueAtFirstLevel() throws Exception {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A"))
                        .addField(newFlatFieldLeaf("level2:B")));

        String actualString = flatField.buildString();

        assertThat(actualString, is("[level1:A|[level2:A|level2:B]]"));
    }

    @Test
    public void createStringUsingFlatFieldWith3LevelDepthWith1ValueEach() throws Exception {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A"))
                        .addField(newFlatField()
                                .addField(newFlatFieldLeaf("level3:A"))));

        String actualString = flatField.buildString();

        assertThat(actualString, is("[level1:A|[level2:A|[level3:A]]]"));
    }

    @Test
    public void createsFlatFieldFromEmptyString() throws Exception {
        FlatField actualFlatField = parse("[]");

        assertThat(actualFlatField, is(newFlatField()));
    }

    @Test
    public void createsFlatFieldFromStringWith1ValueAtLevel1() throws Exception {
        FlatField actualFlatField = parse("[level1:A]");

        FlatField expectedField = newFlatField().addField(newFlatFieldLeaf("level1:A"));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    public void createsFlatFieldFromStringWith2ValuesAtLevel1() throws Exception {
        FlatField actualFlatField = parse("[level1:A|level1:B]");

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatFieldLeaf("level1:B"));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    public void createsFlatFieldFromStringWith1ValueAtLevel1And1ValueAtLevel2() throws Exception {
        FlatField actualFlatField = parse("[level1:A|[level2:A]]");

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A")));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    public void createsFlatFieldFromStringWith2ValuesAtLevel1And1ValueAtLevel2() throws Exception {
        FlatField actualFlatField = parse("[level1:A|[level2:A]|level1:B]");

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A")))
                .addField(newFlatFieldLeaf("level1:B"));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    public void createsFlatFieldFromStringWith1ValueAtLevel1And2ValuesAtLevel2() throws Exception {
        FlatField actualFlatField = parse("[level1:A|[level2:A|level2:B]]");

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A"))
                        .addField(newFlatFieldLeaf("level2:B")));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    public void createsFlatFieldFromStringWith3Levels1ValueForEachLevel() throws Exception {
        FlatField actualFlatField = parse("[level1:A|[level2:A|[level3:A]]]");

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A"))
                        .addField(newFlatField()
                                .addField(newFlatFieldLeaf("level3:A"))));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    public void handleEmptyFieldsConsistently() {
        String origStr = FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("1"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(""))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("3")).buildString();
        System.out.println(origStr);

        FlatField flatFieldBuilderParsed = parse(origStr);
        String parsedStr = flatFieldBuilderParsed.buildString();
        System.out.println(parsedStr);

        assertThat(origStr, is(parsedStr));
    }

    @Test
    public void handleEmptyFieldsConsistentlyAtEnd() {
        FlatFieldBuilder origFlatFieldBuilder = FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("XX:00001"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("XX"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("because it's also bad"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf())
                .addField(FlatFieldLeaf.newFlatFieldLeaf("something else"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf());
        String origStr = origFlatFieldBuilder // no parameter means it's got no value
                .buildString();
        System.out.println(origStr);

        FlatField flatFieldBuilder = parse(origStr);
        assertThat(origFlatFieldBuilder.getFields().size(), is(flatFieldBuilder.getFields().size()));
        assertThat(origFlatFieldBuilder.getFields().size(), is(6));
    }

    /** Check one can newInstance a flat field object, write itself as a String A, then parse
     * this written value into a new flat field object, and write it again as String B. A and B
     * must be equal.
     */
    @Test
    public void parseFlatFieldBuilderString2NestingLevels() {
        FlatFieldBuilder flatFieldBuilderOrig = FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("1"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("2"))
                .addField(
                        FlatFieldBuilder.newFlatField()
                                .addField(FlatFieldLeaf.newFlatFieldLeaf("a"))
                                .addField(FlatFieldLeaf.newFlatFieldLeaf("b")))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("3"));
        String origStr = flatFieldBuilderOrig.buildString(); // serialise

        FlatField flatFieldBuilderParsed = parse(origStr);
        String parsedStr = flatFieldBuilderParsed.buildString();

        assertThat(parsedStr, is(origStr));

        System.out.println(parsedStr);
    }

    @Test
    public void parseFlatFieldBuilderString3NestingLevels() {
        FlatFieldBuilder flatFieldBuilderOrig = FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:A"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:B"))
                .addField(
                        FlatFieldBuilder.newFlatField()
                                .addField(FlatFieldLeaf.newFlatFieldLeaf("level2:A"))
                                .addField(FlatFieldBuilder.newFlatField()
                                        .addField(FlatFieldLeaf.newFlatFieldLeaf("level3:A"))
                                        .addField(FlatFieldLeaf.newFlatFieldLeaf("level3:B"))
                                        .addField(FlatFieldLeaf.newFlatFieldLeaf("level3:C")))
                                .addField(FlatFieldLeaf.newFlatFieldLeaf("level2:B")))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:C"));
        String origStr = flatFieldBuilderOrig.buildString(); // serialise

        FlatField flatFieldBuilderParsed = parse(origStr);
        String parsedStr = flatFieldBuilderParsed.buildString();

        assertThat(parsedStr, is(origStr));

        System.out.println(parsedStr);
    }

    @Test
    public void parseFlatFieldBuilderString4NestingLevels() {
        FlatFieldBuilder flatFieldBuilderOrig = FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:A"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:B"))
                .addField(
                        FlatFieldBuilder.newFlatField()
                                .addField(FlatFieldLeaf.newFlatFieldLeaf("level2:A"))
                                .addField(FlatFieldBuilder.newFlatField()
                                        .addField(FlatFieldLeaf.newFlatFieldLeaf("level3:A"))
                                        .addField(FlatFieldLeaf.newFlatFieldLeaf("level3:B"))
                                        .addField(FlatFieldLeaf.newFlatFieldLeaf("level3:C")))
                                .addField(FlatFieldLeaf.newFlatFieldLeaf("level2:B")))
                .addField(
                        FlatFieldBuilder.newFlatField()
                                .addField(FlatFieldLeaf.newFlatFieldLeaf("level2:C"))
                                .addField(FlatFieldBuilder.newFlatField()
                                        .addField(FlatFieldLeaf.newFlatFieldLeaf("level3:D"))
                                        .addField(FlatFieldBuilder.newFlatField()
                                                .addField(FlatFieldLeaf.newFlatFieldLeaf("level4:A"))
                                                .addField(FlatFieldLeaf.newFlatFieldLeaf("level4:B")))
                                        .addField(FlatFieldLeaf.newFlatFieldLeaf("level3:E")))
                                .addField(FlatFieldLeaf.newFlatFieldLeaf("level2:C")))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:C"));
        String origStr = flatFieldBuilderOrig.buildString(); // serialise

        FlatField flatFieldBuilderParsed = parse(origStr);
        String parsedStr = flatFieldBuilderParsed.buildString();

        assertThat(parsedStr, is(origStr));

        System.out.println(parsedStr);
    }

    @Test
    public void canConvertTerminatingNullFlatFieldLeaf() {
        FlatFieldBuilder ff1Model = FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:A"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:B"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:C"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(null));
        String ff1Str = ff1Model.buildString();
        System.out.println(ff1Str);

        FlatField ff2FromFf1Model = parse(ff1Str);
        String ff2FromFf1Str = ff2FromFf1Model.buildString();
        System.out.println(ff2FromFf1Str);

        assertThat(ff2FromFf1Model, is(ff1Model));
        assertThat(ff2FromFf1Str, is(ff1Str));
    }

    @Test
    public void canConvertTerminatingEmptyFlatFieldLeaf() {
        FlatFieldBuilder ff1Model = FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:A"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:B"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("level1:C"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(""));
        String ff1Str = ff1Model
                .buildString();
        System.out.println(ff1Str);

        FlatField ff2FromFf1Model = parse(ff1Str);
        String ff2FromFf1Str = ff2FromFf1Model.buildString();
        System.out.println(ff2FromFf1Str);

        assertThat(ff2FromFf1Model, is(ff1Model));
        assertThat(ff2FromFf1Str, is(ff1Str));
    }
}