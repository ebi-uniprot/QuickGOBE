package uk.ac.ebi.quickgo.model.ontology.converter.field;

import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.ff.delim.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 01/12/15
 * @author Edd
 */
public class BlackListFieldConverterTest {

    private BlackListFieldConverter converter;

    @Before
    public void setup() {
        this.converter = new BlackListFieldConverter();
    }

    @Test
    public void convertsBlacklist() {
        List<String> rawBlacklist = new ArrayList<>();

        String gp0 = "GP:00000";
        rawBlacklist.add(newFlatField()
                .addField(newFlatFieldLeaf(gp0))
                .addField(newFlatFieldLeaf("GP"))
                .addField(newFlatFieldLeaf("because it's bad"))
                .addField(newFlatFieldLeaf("category 0"))
                .addField(newFlatFieldLeaf("automatic"))
                .buildString());
        String cat1 = "category 1";
        rawBlacklist.add(newFlatField()
                .addField(newFlatFieldLeaf("XX:00001"))
                .addField(newFlatFieldLeaf("XX"))
                .addField(newFlatFieldLeaf("because it's also bad"))
                .addField(newFlatFieldLeaf(cat1))
                .addField(newFlatFieldLeaf()) // no parameter means it's got no value
                .buildString());

        List<OBOTerm.BlacklistItem> blacklistItems = converter.convertField(rawBlacklist);
        assertThat(blacklistItems.size(), is(2));
        assertThat(blacklistItems.get(0).geneProductId, is(gp0));
        assertThat(blacklistItems.get(1).category, is(cat1));
        assertThat(blacklistItems.get(1).method, is(nullValue()));
    }
}