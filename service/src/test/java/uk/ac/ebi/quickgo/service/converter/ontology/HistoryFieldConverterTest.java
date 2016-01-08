package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatFieldFromDepth;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 01/12/15
 * @author Edd
 */
public class HistoryFieldConverterTest {
    private HistoryFieldConverter converter;

    @Before
    public void setup() {
        this.converter = new HistoryFieldConverter();
    }

    @Test
    public void convertsHistory() {
        List<String> rawHistory = new ArrayList<>();
        rawHistory.add(
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf("Gonna do something like it's ..."))
                        .addField(newFlatFieldLeaf("11:59, 31 Dec, 1999"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Textual description"))
                        .buildString()
        );
        rawHistory.add(
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf("History name"))
                        .addField(newFlatFieldLeaf("Tuesday next week"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Okay"))
                        .buildString()
        );

        List<OBOTerm.History> history = converter.convertFieldList(rawHistory);
        assertThat(history.size(), is(2));
        assertThat(history.get(0).name, is("Gonna do something like it's ..."));
        assertThat(history.get(1).text, is("Okay"));
    }

}