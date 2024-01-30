package uk.ac.ebi.quickgo.ontology.service.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 01/12/15
 * @author Edd
 */
class XORelationsFieldConverterTest {
    private XORelationsFieldConverter converter;

    @BeforeEach
    void setup() {
        this.converter = new XORelationsFieldConverter();
    }

    @Test
    void convertsXOntologyRelations() {
        String id0 = "id0";
        String term0 = "term0";
        String namespace0 = "ns0";
        String url0 = "url0";
        String relation0 = "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16919";

        String id1 = "id1";
        String term1 = "term1";
        String namespace1 = "ns1";
        String url1 = "url1";
        String relation1 = "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16912";

        List<String> rawXORels = new ArrayList<>();
        rawXORels.add(
                newFlatField()
                        .addField(newFlatFieldLeaf(id0))
                        .addField(newFlatFieldLeaf(term0))
                        .addField(newFlatFieldLeaf(namespace0))
                        .addField(newFlatFieldLeaf(url0))
                        .addField(newFlatFieldLeaf(relation0))
                        .buildString()
        );
        rawXORels.add(
                newFlatField()
                        .addField(newFlatFieldLeaf(id1))
                        .addField(newFlatFieldLeaf(term1))
                        .addField(newFlatFieldLeaf(namespace1))
                        .addField(newFlatFieldLeaf(url1))
                        .addField(newFlatFieldLeaf(relation1))
                        .buildString()
        );

        List<OBOTerm.XORelation> xORefs = converter.convertFieldList(rawXORels);
        assertThat(xORefs.size(), is(2));
        assertThat(xORefs.get(0).id, is(id0));
        assertThat(xORefs.get(0).url, is(url0));
        assertThat(xORefs.get(1).namespace, is(namespace1));
        assertThat(xORefs.get(1).url, is(url1));
    }

    @Test
    void gracefullyHandleWrongFieldCount() {
        Optional<OBOTerm.XORelation> result = converter.apply(newFlatField().addField(newFlatFieldLeaf("wrong format"))
                .buildString());
        assertThat(result.isPresent(), is(false));
    }
}