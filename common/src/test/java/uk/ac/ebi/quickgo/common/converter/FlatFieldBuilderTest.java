package uk.ac.ebi.quickgo.common.converter;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.*;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 26/11/15
 * @author Edd
 */
class FlatFieldBuilderTest {

    private static final String START_DELIM = LEVEL_SEPARATOR_START;
    private static final String END_DELIM = LEVEL_SEPARATOR_END;
    private static final String SEPARATOR = VALUE_SEPARATOR;

    @Test
    void createStringUsingEmptyFlatField()  {
        FlatField emptyFlatField = newFlatField();

        String actualString = emptyFlatField.buildString();

        assertThat(actualString, is(expectedField()));
    }

    @Test
    void createStringUsingFlatFieldWith1Value()  {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"));

        String actualString = flatField.buildString();

        assertThat(actualString, is(expectedField("level1:A")));
    }

    @Test
    void createStringUsingFlatFieldWith2ValuesAtLevel1()  {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatFieldLeaf("level1:B"));

        String actualString = flatField.buildString();

        assertThat(actualString, is(expectedField("level1:A", "level1:B")));
    }

    @Test
    void createStringUsingFlatFieldWith2Values1AtEachLevel()  {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A")));

        String actualString = flatField.buildString();

        assertThat(actualString,
                is(expectedField("level1:A", expectedField("level2:A"))));
    }

    @Test
    void createStringWithFlatFieldWith2ValuesAtFirstLevelAnd1ValueAtSecondLevel()  {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A")))
                .addField(newFlatFieldLeaf("level1:B"));

        String actualString = flatField.buildString();

        assertThat(actualString,
                is(expectedField("level1:A", expectedField("level2:A"), "level1:B")));
    }

    @Test
    void createStringUsingFlatFieldWith2ValuesAtSecondLevelAnd1ValueAtFirstLevel()  {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A"))
                        .addField(newFlatFieldLeaf("level2:B")));

        String actualString = flatField.buildString();

        assertThat(actualString,
                is(expectedField("level1:A", expectedField("level2:A", "level2:B"))));
    }

    @Test
    void createStringUsingFlatFieldWith3LevelDepthWith1ValueEach()  {
        FlatField flatField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A"))
                        .addField(newFlatField()
                                .addField(newFlatFieldLeaf("level3:A"))));

        String actualString = flatField.buildString();

        assertThat(actualString,
                is(expectedField("level1:A", expectedField("level2:A", expectedField("level3:A")))));
    }

    @Test
    void createsFlatFieldFromEmptyString()  {
        FlatField actualFlatField = parse(expectedField());

        assertThat(actualFlatField, is(newFlatField()));
    }

    @Test
    void createsFlatFieldFromStringWith1ValueAtLevel1()  {
        FlatField actualFlatField = parse(expectedField("level1:A"));

        FlatField expectedField = newFlatField().addField(newFlatFieldLeaf("level1:A"));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    void createsFlatFieldFromStringWith2ValuesAtLevel1()  {
        FlatField actualFlatField = parse(expectedField("level1:A", "level1:B"));

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatFieldLeaf("level1:B"));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    void createsFlatFieldFromStringWith1ValueAtLevel1And1ValueAtLevel2()  {
        FlatField actualFlatField = parse(expectedField("level1:A", expectedField("level2:A")));

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A")));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    void createsFlatFieldFromStringWith2ValuesAtLevel1And1ValueAtLevel2()  {
        FlatField actualFlatField = parse(expectedField("level1:A", expectedField("level2:A"), "level1:B"));

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A")))
                .addField(newFlatFieldLeaf("level1:B"));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    void createsFlatFieldFromStringWith1ValueAtLevel1And2ValuesAtLevel2()  {
        FlatField actualFlatField = parse(expectedField("level1:A", expectedField("level2:A", "level2:B")));

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A"))
                        .addField(newFlatFieldLeaf("level2:B")));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    void createsFlatFieldFromStringWith3Levels1ValueForEachLevel()  {
        FlatField actualFlatField =
                parse(
                        expectedField("level1:A",
                                expectedField("level2:A",
                                        expectedField("level3:A"))));

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:A"))
                        .addField(newFlatField()
                                .addField(newFlatFieldLeaf("level3:A"))));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    void handleEmptyFieldsConsistently() {
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
    void handleEmptyFieldsConsistentlyAtEnd() {
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

    /** Check one can create a new flat field object, write itself as a String A, then parse
     * this written value into a new flat field object, and write it again as String B. A and B
     * must be equal.
     */
    @Test
    void parseFlatFieldBuilderString2NestingLevels() {
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
    void parseFlatFieldBuilderString3NestingLevels() {
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
    void parseFlatFieldBuilderString4NestingLevels() {
        FlatFieldBuilder flatFieldBuilderOrig = newFlatField()
                .addField(newFlatFieldLeaf("level1:A"))
                .addField(newFlatFieldLeaf("level1:B"))
                .addField(
                        newFlatField()
                                .addField(newFlatFieldLeaf("level2:A"))
                                .addField(newFlatField()
                                        .addField(newFlatFieldLeaf("level3:A"))
                                        .addField(newFlatFieldLeaf("level3:B"))
                                        .addField(newFlatFieldLeaf("level3:C")))
                                .addField(newFlatFieldLeaf("level2:B")))
                .addField(
                        newFlatField()
                                .addField(newFlatFieldLeaf("level2:C"))
                                .addField(newFlatField()
                                        .addField(newFlatFieldLeaf("level3:D"))
                                        .addField(newFlatField()
                                                .addField(newFlatFieldLeaf("level4:A"))
                                                .addField(newFlatFieldLeaf("level4:B")))
                                        .addField(newFlatFieldLeaf("level3:E")))
                                .addField(newFlatFieldLeaf("level2:C")))
                .addField(newFlatFieldLeaf("level1:C"));
        String origStr = flatFieldBuilderOrig.buildString(); // serialise

        FlatField flatFieldBuilderParsed = parse(origStr);
        String parsedStr = flatFieldBuilderParsed.buildString();

        assertThat(parsedStr, is(origStr));

        System.out.println(parsedStr);
    }

    @Test
    void canConvertTerminatingNullFlatFieldLeaf() {
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
    void canConvertTerminatingEmptyFlatFieldLeaf() {
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

    @Test
    void canConvertFieldContainingSquareBrackets() {
        String fieldValue = "holo-[acyl-carrier-protein] synthase complex";
        FlatField actualFlatField = parse(expectedField(fieldValue));

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf(fieldValue));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    void canConvertMultipleFieldsContainingSquareBrackets() {
        String complex = "fatty acid [synthase] complex";
        String date = "2007-08-09";
        String action = "Deleted";
        String type = "SYNONYM";
        String proteinComplex = "holo-[acyl-carrier-protein] synthase complex";

        FlatField actualFlatField =
                parse(
                        expectedField(
                                complex,
                                date,
                                action,
                                type,
                                proteinComplex));

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf(complex))
                .addField(newFlatFieldLeaf(date))
                .addField(newFlatFieldLeaf(action))
                .addField(newFlatFieldLeaf(type))
                .addField(newFlatFieldLeaf(proteinComplex));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    void canConvertNestedFieldsContainingSquareBrackets() {
        FlatField actualFlatField =
                parse(expectedField("level1:value [1]",
                        expectedField("level2:value [2]",
                                expectedField("level3:value [3]"))));

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:value [1]"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:value [2]"))
                        .addField(newFlatField()
                                .addField(newFlatFieldLeaf("level3:value [3]"))));

        assertThat(actualFlatField, is(expectedField));
    }

    @Test
    void canConvertNestedFieldsContainingSpecialCharacters() {
        String specialChars = "`¬|\\£$%^&*()_-=+./<>?:@~#{}[]";
        String flatFieldText = expectedField("level1:" + specialChars,
                expectedField("level2:" + specialChars,
                        expectedField("level3:" + specialChars)));

        System.out.println(flatFieldText);
        FlatField actualFlatField =
                parse(flatFieldText);

        FlatField expectedField = newFlatField()
                .addField(newFlatFieldLeaf("level1:" + specialChars))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("level2:" + specialChars))
                        .addField(newFlatField()
                                .addField(newFlatFieldLeaf("level3:" + specialChars))));

        assertThat(actualFlatField, is(expectedField));
    }

    private String expectedField(String... values) {
        return START_DELIM + Stream.of(values).collect(Collectors.joining(SEPARATOR)) + END_DELIM;
    }
}