package uk.ac.ebi.quickgo.ontology.service.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Created 01/12/15
 * @author Edd
 */
class BlackListFieldConverterTest {

    private BlackListFieldConverter converter;
    public static final int FLAT_FIELD_DEPTH = 0;

    @BeforeEach
    void setup() {
        this.converter = new BlackListFieldConverter();
    }

    @Test
    void convertsBlacklist() {
        List<String> rawBlacklist = new ArrayList<>();

        rawBlacklist.add(FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("GO:0000001"))                   // id
                .addField(FlatFieldLeaf.newFlatFieldLeaf("NOT-qualified manual"))         // category
                .addField(FlatFieldLeaf.newFlatFieldLeaf("protein"))                      // entity type
                .addField(FlatFieldLeaf.newFlatFieldLeaf("A5I1R9"))                       // entity id
                .addField(FlatFieldLeaf.newFlatFieldLeaf("441771"))                       // taxon id
                .addField(FlatFieldLeaf.newFlatFieldLeaf("A5I1R9_CLOBH"))                 // entity name
                .addField(FlatFieldLeaf.newFlatFieldLeaf("GO:0007005"))                   // ancestor go id
                .addField(FlatFieldLeaf.newFlatFieldLeaf("1 NOT-qualified manual etc"))   // reason
                .addField(FlatFieldLeaf.newFlatFieldLeaf("IER12345"))                     // methodId
                .buildString());

        rawBlacklist.add(FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("GO:0000001"))                   // id
                .addField(FlatFieldLeaf.newFlatFieldLeaf("IS-qualified manual"))          // category
                .addField(FlatFieldLeaf.newFlatFieldLeaf("protein"))                      // entity type
                .addField(FlatFieldLeaf.newFlatFieldLeaf("B5I1R9"))                       // entity id
                .addField(FlatFieldLeaf.newFlatFieldLeaf("441771"))                       // taxon id
                .addField(FlatFieldLeaf.newFlatFieldLeaf("B5I1R9_CLOBH"))                 // entity name
                .addField(FlatFieldLeaf.newFlatFieldLeaf("GO:0007006"))                   // ancestor go id
                .addField(FlatFieldLeaf.newFlatFieldLeaf("1 NOT-qualified manual etc"))   // methodId
                .addField(FlatFieldLeaf.newFlatFieldLeaf())                               // no parameter means it's got no value
                .buildString());

        List<GOTerm.BlacklistItem> blacklistItems = converter.convertFieldList(rawBlacklist);
        assertThat(blacklistItems.size(), is(2));
        assertThat(blacklistItems.get(0).goId, is("GO:0000001"));
        assertThat(blacklistItems.get(1).category, is("IS-qualified manual"));
        assertThat(blacklistItems.get(1).methodId, is(nullValue()));
    }

    @Test
    void gracefullyHandleWrongFieldCount() {
        Optional<GOTerm.BlacklistItem> result = converter.apply(
                FlatFieldBuilder.newFlatField().addField(FlatFieldLeaf.newFlatFieldLeaf("wrong " +
                "format"))
                .buildString());
        assertThat(result.isPresent(), is(false));
    }
}