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
public class XORelationsFieldConverterTest {
    private XORelationsFieldConverter converter;

    @Before
    public void setup() {
        this.converter = new XORelationsFieldConverter();
    }

    @Test
    public void convertsXOntologyRelations() {
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
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf(id0))
                        .addField(newFlatFieldLeaf(term0))
                        .addField(newFlatFieldLeaf(namespace0))
                        .addField(newFlatFieldLeaf(url0))
                        .addField(newFlatFieldLeaf(relation0))
                        .buildString()
        );
        rawXORels.add(
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf(id1))
                        .addField(newFlatFieldLeaf(term1))
                        .addField(newFlatFieldLeaf(namespace1))
                        .addField(newFlatFieldLeaf(url1))
                        .addField(newFlatFieldLeaf(relation1))
                        .buildString()
        );

        List<OBOTerm.XORelation> xORefs = converter.convertField(rawXORels);
        assertThat(xORefs.size(), is(2));
        assertThat(xORefs.get(0).id, is(id0));
        assertThat(xORefs.get(0).url, is(url0));
        assertThat(xORefs.get(1).namespace, is(namespace1));
        assertThat(xORefs.get(1).url, is(url1));
    }
}