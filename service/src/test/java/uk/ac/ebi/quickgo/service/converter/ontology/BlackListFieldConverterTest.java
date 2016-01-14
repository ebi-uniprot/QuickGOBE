package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatFieldFromDepth;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 01/12/15
 * @author Edd
 */
public class BlackListFieldConverterTest {

    private BlackListFieldConverter converter;
    public static final int FLAT_FIELD_DEPTH = 0;

    @Before
    public void setup() {
        this.converter = new BlackListFieldConverter();
    }

    @Test
    public void convertsBlacklist() {
        List<String> rawBlacklist = new ArrayList<>();

//        String gp0 = "GP:00000";
//        rawBlacklist.add(newFlatField()
//                .addField(newFlatFieldLeaf(gp0))
//                .addField(newFlatFieldLeaf("GP"))
//                .addField(newFlatFieldLeaf("because it's bad"))
//                .addField(newFlatFieldLeaf("category 0"))
//                .addField(newFlatFieldLeaf("automatic"))
//                .buildString());
//        String cat1 = "category 1";
//        rawBlacklist.add(newFlatField()
//                .addField(newFlatFieldLeaf("XX:00001"))
//                .addField(newFlatFieldLeaf("XX"))
//                .addField(newFlatFieldLeaf("because it's also bad"))
//                .addField(newFlatFieldLeaf(cat1))
//                .addField(newFlatFieldLeaf()) // no parameter means it's got no value
//                .buildString());

        rawBlacklist.add(newFlatField()
                .addField(newFlatFieldLeaf("GO:0000001"))                   //id
                .addField(newFlatFieldLeaf("NOT-qualified manual"))         //category
                .addField(newFlatFieldLeaf("protein"))                      //entity type
                .addField(newFlatFieldLeaf("A5I1R9"))                       //entity id
                .addField(newFlatFieldLeaf("441771"))                       //taxon id
                .addField(newFlatFieldLeaf("A5I1R9_CLOBH"))                 //entity name
                .addField(newFlatFieldLeaf("GO:0007005"))                   //ancestor go id
                .addField(newFlatFieldLeaf("1 NOT-qualified manual etc"))   //reason
                .addField(newFlatFieldLeaf("IER12345"))                     //method
                .buildString());

        rawBlacklist.add(newFlatField()
                .addField(newFlatFieldLeaf("GO:0000001"))                   //id
                .addField(newFlatFieldLeaf("IS-qualified manual"))         //category
                .addField(newFlatFieldLeaf("protein"))                      //entity type
                .addField(newFlatFieldLeaf("B5I1R9"))                       //entity id
                .addField(newFlatFieldLeaf("441771"))                       //taxon id
                .addField(newFlatFieldLeaf("B5I1R9_CLOBH"))                 //entity name
                .addField(newFlatFieldLeaf("GO:0007006"))                   //ancestor go id
                .addField(newFlatFieldLeaf("1 NOT-qualified manual etc"))   //reason
                .addField(newFlatFieldLeaf()) // no parameter means it's got no value
                .buildString());

        List<OBOTerm.BlacklistItem> blacklistItems = converter.convertFieldList(rawBlacklist);
        assertThat(blacklistItems.size(), is(2));
        assertThat(blacklistItems.get(0).geneProductId, is("GO:0000001"));
        assertThat(blacklistItems.get(1).category, is("IS-qualified manual"));
        assertThat(blacklistItems.get(1).method, is(nullValue()));
    }

    @Test
    public void gracefullyHandleWrongFieldCount() {
        Optional<OBOTerm.BlacklistItem> result = converter.apply(newFlatField().addField(newFlatFieldLeaf("wrong " +
                "format"))
                .buildString());
        assertThat(result.isPresent(), is(false));
    }
}
