package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;

/**
 * Created 01/12/15
 * @author Edd
 */
public class TaxonConstraintsFieldConverterTest {
    private TaxonConstraintsFieldConverter converter;

    @Before
    public void setup() {
        this.converter = new TaxonConstraintsFieldConverter();
    }

    @Test
    public void convertsTaxonConstraints() {
        List<String> rawTaxonConstraints = new ArrayList<>();

        String ancestorId = "GO:0005623";
        String taxIdType = "NCBITaxon";
        String taxId = "131568";
        String citationId = "PMID:00000003";

        rawTaxonConstraints.add(newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf(ancestorId))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("cell"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("only_in_taxon"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("131567"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(taxIdType))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatField()
                        .addField(FlatFieldLeaf.newFlatFieldLeaf("PMID:00000001"))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf("PMID:00000002")))
                .buildString());
        rawTaxonConstraints.add(newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("GO:0005624"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("cell"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("only_in_taxon"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(taxId))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(taxIdType))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatField()
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(citationId))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf("PMID:00000004")))
                .buildString());

        List<OBOTerm.TaxonConstraint> taxonConstraints = converter.convertFieldList(rawTaxonConstraints);
        assertThat(taxonConstraints.size(), is(2));
        assertThat(taxonConstraints.get(0).ancestorId, is(ancestorId));
        assertThat(taxonConstraints.get(0).taxIdType, is(taxIdType));
        assertThat(taxonConstraints.get(1).taxId, is(taxId));
        assertThat(taxonConstraints.get(1).citations.get(0).id, is(citationId));
    }

    @Test
    public void gracefullyHandleWrongFieldCount() {
        Optional<OBOTerm.TaxonConstraint> result = converter.apply(newFlatField().addField(
                FlatFieldLeaf.newFlatFieldLeaf("wrong " +
                "format"))
                .buildString());
        assertThat(result.isPresent(), is(false));
    }

}