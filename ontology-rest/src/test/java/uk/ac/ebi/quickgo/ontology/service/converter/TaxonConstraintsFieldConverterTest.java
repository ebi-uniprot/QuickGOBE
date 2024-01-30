package uk.ac.ebi.quickgo.ontology.service.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf.*;

/**
 * Created 01/12/15
 * @author Edd
 */
class TaxonConstraintsFieldConverterTest {
    private static final String ANCESTOR_ID = "GO:0005623";
    private static final String ANCESTOR_NAME = "cell";
    private static final String RELATIONSHIP = "only_in_taxon";
    private static final String TAX_ID_TYPE = "NCBITaxon";
    private static final String TAX_ID = "131568";
    private static final String TAX_NAME = "cellular organisms";

    private static final String CITATION_ID1 = "PMID:00000003";
    private static final String CITATION_ID2 = "PMID:00000004";

    private TaxonConstraintsFieldConverter converter;

    @BeforeEach
    void setup() {
        this.converter = new TaxonConstraintsFieldConverter();
    }

    @Test
    void convertsTaxonConstraintWithNoCitations() {
        List<String> rawTaxonConstraints = new ArrayList<>();

        rawTaxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf(ANCESTOR_ID))
                .addField(newFlatFieldLeaf(ANCESTOR_NAME))
                .addField(newFlatFieldLeaf(RELATIONSHIP))
                .addField(newFlatFieldLeaf(TAX_ID))
                .addField(newFlatFieldLeaf(TAX_ID_TYPE))
                .addField(newFlatFieldLeaf(TAX_NAME))
                .addField(FlatFieldBuilder.newFlatField())
                .buildString());

        List<OBOTerm.TaxonConstraint> taxonConstraints = converter.convertFieldList(rawTaxonConstraints);
        assertThat(taxonConstraints.size(), is(1));

        OBOTerm.TaxonConstraint expectedTaxCons = taxonConstraints.get(0);
        assertThat(expectedTaxCons.ancestorId, is(ANCESTOR_ID));
        assertThat(expectedTaxCons.ancestorName, is(ANCESTOR_NAME));
        assertThat(expectedTaxCons.relationship, is(RELATIONSHIP));
        assertThat(expectedTaxCons.taxId, is(TAX_ID));
        assertThat(expectedTaxCons.taxIdType, is(TAX_ID_TYPE));
        assertThat(expectedTaxCons.taxName, is(TAX_NAME));
        assertThat(expectedTaxCons.citations, hasSize(0));
    }

    @Test
    void convertsTaxonConstraintWithWith1Citation() {
        List<String> rawTaxonConstraints = new ArrayList<>();

        rawTaxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(FlatFieldBuilder.newFlatField()
                        .addField(newFlatFieldLeaf(CITATION_ID1)))
                .buildString());

        List<OBOTerm.TaxonConstraint> taxonConstraints = converter.convertFieldList(rawTaxonConstraints);
        assertThat(taxonConstraints.size(), is(1));

        OBOTerm.TaxonConstraint expectedTaxCons = taxonConstraints.get(0);
        assertThat(expectedTaxCons.ancestorId, is(nullValue()));
        assertThat(expectedTaxCons.ancestorName, is(nullValue()));
        assertThat(expectedTaxCons.relationship, is(nullValue()));
        assertThat(expectedTaxCons.taxId, is(nullValue()));
        assertThat(expectedTaxCons.taxIdType, is(nullValue()));
        assertThat(expectedTaxCons.taxName, is(nullValue()));

        List<OBOTerm.Literature> expectedCitations = expectedTaxCons.citations;
        assertThat(expectedCitations, hasSize(1));

        OBOTerm.Literature expectedCitation = expectedCitations.get(0);
        assertThat(expectedCitation.id, is(CITATION_ID1));
    }

    @Test
    void convertsTaxonConstraintWithWith2Citations() {
        List<String> rawTaxonConstraints = new ArrayList<>();

        rawTaxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(newFlatFieldLeaf())
                .addField(FlatFieldBuilder.newFlatField()
                        .addField(newFlatFieldLeaf(CITATION_ID1))
                        .addField(newFlatFieldLeaf(CITATION_ID2)))
                .buildString());

        List<OBOTerm.TaxonConstraint> taxonConstraints = converter.convertFieldList(rawTaxonConstraints);
        assertThat(taxonConstraints.size(), is(1));

        OBOTerm.TaxonConstraint expectedTaxCons = taxonConstraints.get(0);
        assertThat(expectedTaxCons.ancestorId, is(nullValue()));
        assertThat(expectedTaxCons.ancestorName, is(nullValue()));
        assertThat(expectedTaxCons.relationship, is(nullValue()));
        assertThat(expectedTaxCons.taxId, is(nullValue()));
        assertThat(expectedTaxCons.taxIdType, is(nullValue()));
        assertThat(expectedTaxCons.taxName, is(nullValue()));

        List<OBOTerm.Literature> expectedCitations = expectedTaxCons.citations;
        assertThat(expectedCitations, hasSize(2));

        List<String> expectedCitationIds = extractAttributeFromConstraints(expectedCitations, (OBOTerm.Literature
                lit) -> lit.id);

        assertThat(expectedCitationIds, contains(CITATION_ID1, CITATION_ID2));
    }

    @Test
    void converts2TaxonConstraints() {
        String ancestorId2 = "GO:0005624";

        List<String> rawTaxonConstraints = new ArrayList<>();

        rawTaxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf(ANCESTOR_ID))
                .addField(newFlatFieldLeaf(ANCESTOR_NAME))
                .addField(newFlatFieldLeaf(RELATIONSHIP))
                .addField(newFlatFieldLeaf(TAX_ID))
                .addField(newFlatFieldLeaf(TAX_ID_TYPE))
                .addField(newFlatFieldLeaf(TAX_NAME))
                .addField(newFlatField())
                .buildString());
        rawTaxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf(ancestorId2))
                .addField(newFlatFieldLeaf(ANCESTOR_NAME))
                .addField(newFlatFieldLeaf(RELATIONSHIP))
                .addField(newFlatFieldLeaf(TAX_ID))
                .addField(newFlatFieldLeaf(TAX_ID_TYPE))
                .addField(newFlatFieldLeaf(TAX_NAME))
                .addField(newFlatField())
                .buildString());

        List<OBOTerm.TaxonConstraint> taxonConstraints = converter.convertFieldList(rawTaxonConstraints);
        assertThat(taxonConstraints.size(), is(2));

        List<String> expectedAncestorIds = extractAttributeFromConstraints(taxonConstraints,
                (OBOTerm.TaxonConstraint constraint) -> constraint.ancestorId);

        assertThat(expectedAncestorIds, containsInAnyOrder(ANCESTOR_ID, ancestorId2));
    }

    @Test
    void gracefullyHandleWrongFieldCount() {
        Optional<OBOTerm.TaxonConstraint> result = converter.apply(newFlatField().addField(
                newFlatFieldLeaf("wrong " +
                        "format"))
                .buildString());
        assertThat(result.isPresent(), is(false));
    }

    private <S,T> List<T> extractAttributeFromConstraints(Collection<S> constraints,
            Function<S, T> attributeExtractor) {
        return constraints.stream()
                .map(attributeExtractor)
                .collect(Collectors.toList());
    }
}