package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.AnnotationParameters;
import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.EVIDENCE_CODE_USAGE_RELATIONS_PARAM;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.GO_ID_PARAM;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.GO_USAGE_RELATIONS_PARAM;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.*;

/**
 *
 * Tests methods and structure of AnnotationRequest
 *
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 11:25
 * Created with IntelliJ IDEA.
 */
class AnnotationRequestTest {

    private AnnotationRequest annotationRequest;

    @BeforeEach
    void setUp() {
        annotationRequest = new AnnotationRequest();
    }

    @Test
    void defaultPageAndLimitValuesAreCorrect() {
        assertThat(annotationRequest.getPage(), equalTo(1));
        assertThat(annotationRequest.getLimit(), equalTo(25));
    }

    @Test
    void successfullySetAndGetPageAndLimitValues() {
        annotationRequest.setPage(4);
        annotationRequest.setLimit(15);

        assertThat(annotationRequest.getPage(), equalTo(4));
        assertThat(annotationRequest.getLimit(), equalTo(15));
    }

    @Test
    void setAndGetAssignedBy() {
        String assignedBy = "UniProt";
        annotationRequest.setAssignedBy(assignedBy);

        assertThat(annotationRequest.getAssignedBy(), arrayContaining(assignedBy));
    }

    @Test
    void setAndGetWithFrom() {
        String WITH_FROM = "RGD:1623038";
        annotationRequest.setWithFrom(WITH_FROM);
        assertThat(annotationRequest.getWithFrom(), arrayContaining(WITH_FROM));
    }

    @Test
    void setAndGetOntologyAspect() {
        String aspect = "function";

        annotationRequest.setAspect(aspect);

        assertThat(annotationRequest.getAspect(), arrayContaining(aspect));
    }

    @Test
    void setAndGetGeneProductID() {
        String geneProductID = "A0A000";
        annotationRequest.setGeneProductId(geneProductID);
        assertThat(annotationRequest.getGeneProductId(), arrayContaining(geneProductID));
    }

    @Test
    void setAndGetMultipleGeneProductIDs() {
        String[] geneProductID = {"A0A000", "A0A001"};
        annotationRequest.setGeneProductId(geneProductID);
        assertThat(annotationRequest.getGeneProductId(), arrayContaining(geneProductID));
    }

    @Test
    void setAndGetEvidence() {
        String EVIDENCE_IEA = "IEA";
        annotationRequest.setGoIdEvidence(EVIDENCE_IEA);
        assertThat(annotationRequest.getGoIdEvidence(), arrayContaining(EVIDENCE_IEA));
    }

    @Test
    void setAndGetEvidenceMulti() {
        String[] EVIDENCE_MULTI = {"IEA", "IBD"};
        annotationRequest.setGoIdEvidence(EVIDENCE_MULTI);
        assertThat(annotationRequest.getGoIdEvidence(), arrayContaining(EVIDENCE_MULTI));
    }

    @Test
    void setAndGetEvidenceMultiInLowerCase() {
        String[] EVIDENCE_MULTI = {"iea", "ibd"};
        annotationRequest.setGoIdEvidence(EVIDENCE_MULTI);
        assertThat(annotationRequest.getGoIdEvidence(), is(EVIDENCE_MULTI));
    }

    @Test
    void setAndGetGoUsage() {
        String usage = EXACT_USAGE;

        annotationRequest.setGoUsage(usage);

        assertThat(annotationRequest.getGoUsage(), is(usage));
    }

    @Test
    void getDefaultGoUsage() {
        assertThat(annotationRequest.getGoUsage(), is(DEFAULT_GO_USAGE));
    }

    @Test
    void setAndGetGoIds() {
        String[] usageIds = {"GO:0000001", "GO:0000002"};

        annotationRequest.setGoId(usageIds);

        assertThat(annotationRequest.getGoId(), is(usageIds));
    }

    @Test
    void setAndGetGoUsageRelationships() {
        String[] usageRelationships = {"iS_", "paRt_of"};

        annotationRequest.setGoUsageRelationships(usageRelationships);

        String[] expectedLowerCaseRels = Stream.of(usageRelationships)
                .map(String::toLowerCase)
                .toArray(String[]::new);

        assertThat(annotationRequest.getGoUsageRelationships(), arrayContaining(expectedLowerCaseRels));
    }

    @Test
    void canCreateDefaultFilterWithGoIds() {
        String goId = "GO:0000001";

        annotationRequest.setGoId(goId);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(DESCENDANTS_USAGE)
                .addProperty(AnnotationParameters.GO_ID_PARAM.getName(), goId.toUpperCase())
                .addProperty(GO_USAGE_RELATIONS_PARAM.getName())
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void canCreateDefaultFilterWithGoIdsAndGoUsageRelationships() {
        String goId = "GO:0000001";
        String relationships = "is_A";

        annotationRequest.setGoId(goId);
        annotationRequest.setGoUsageRelationships(relationships);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(DESCENDANTS_USAGE)
                .addProperty(GO_ID_PARAM.getName(), goId.toUpperCase())
                .addProperty(GO_USAGE_RELATIONS_PARAM.getName(), relationships.toLowerCase())
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void canCreateExactFilterWithGoIds() {
        String goId = "GO:0000001";

        annotationRequest.setGoId(goId);
        annotationRequest.setGoUsage(EXACT_USAGE);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(AnnotationFields.Searchable.GO_ID, goId.toUpperCase())
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void canCreateExactFilterWithGoIdsAndUnusedGoUsageRelationships() {
        String goId = "GO:0000001";
        String relationships = "is_A";

        annotationRequest.setGoId(goId);
        annotationRequest.setGoUsage(EXACT_USAGE);
        annotationRequest.setGoUsageRelationships(relationships);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(AnnotationFields.Searchable.GO_ID, goId.toUpperCase())
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void createsFilterWithCaseInsensitiveGoUsageAndGoIds() {
        String usage = "descEndants";
        String goId = "GO:0000001";

        annotationRequest.setGoUsage(usage);
        annotationRequest.setGoId(goId);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(usage.toLowerCase())
                .addProperty(AnnotationParameters.GO_ID_PARAM.getName(), goId.toUpperCase())
                .addProperty(GO_USAGE_RELATIONSHIPS)
                .build();
        assertThat(annotationRequest.createFilterRequests(),
                contains(request));
    }

    @Test
    void createsFilterWithCaseInsensitiveUsageAndGoIdsAndGoUsageRelationships() {
        String usage = "deSCendants";
        String goId = "GO:0000001";
        String relationships = "is_A";

        annotationRequest.setGoUsage(usage);
        annotationRequest.setGoId(goId);
        annotationRequest.setGoUsageRelationships(relationships);

        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(usage.toLowerCase())
                .addProperty(AnnotationParameters.GO_ID_PARAM.getName(), goId.toUpperCase())
                .addProperty(GO_USAGE_RELATIONSHIPS, relationships.toLowerCase())
                .build();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void cannotCreateFilterWithUsageAndNoGoUsageIds() {
        annotationRequest.setGoUsage(DESCENDANTS_USAGE);
        assertThrows(ParameterException.class, () -> annotationRequest.createFilterRequests());
    }

    //-----------------
    @Test
    void setAndGetTaxon() {
        String taxonId = "1";

        annotationRequest.setTaxonId(taxonId);

        assertThat(annotationRequest.getTaxonId(), arrayContaining(taxonId));
    }

    @Test
    void setAndGetTaxonUsage() {
        String taxonUsage = "exact";
        annotationRequest.setTaxonUsage(taxonUsage);

        assertThat(annotationRequest.getTaxonUsage(), is(taxonUsage));
    }

    @Test
    void useCorrectDefaultTaxonUsage() {
        annotationRequest.createFilterRequests();

        assertThat(annotationRequest.getTaxonUsage(), is(AnnotationRequest.DEFAULT_TAXON_USAGE));
    }

    @Test
    void canCreateDefaultTaxonFilterWithTaxonIds() {
        annotationRequest.setTaxonId("1", "2");

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(getDefaultTaxonSearchField(), "1", "2")
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();

        assertThat(filterRequests, contains(request));
    }

    @Test
    void canCreateTaxonDescendantsFilterWithTaxonUsageAndTaxonIds() {
        annotationRequest.setTaxonId("1", "2");
        annotationRequest.setTaxonUsage("descendants");

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(AnnotationFields.Searchable.TAXON_ANCESTORS, "1", "2")
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();

        assertThat(filterRequests, contains(request));
    }

    @Test
    void canCreateTaxonExactFilterWithTaxonUsageAndTaxonIds() {
        annotationRequest.setTaxonId("1", "2");
        annotationRequest.setTaxonUsage("exact");

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(AnnotationFields.Searchable.TAXON_ID, "1", "2")
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();

        assertThat(filterRequests, contains(request));
    }

    @Test
    void cannotCreateFilterWithTaxonUsageAndNoTaxonIds() {
        annotationRequest.setTaxonUsage("descendants");
        assertThrows(ParameterException.class, () -> annotationRequest.createFilterRequests());
    }

    //-----------------
    @Test
    void setAndGetEvidenceCodeUsage() {
        String usage = DESCENDANTS_USAGE;

        annotationRequest.setEvidenceCodeUsage(usage);

        assertThat(annotationRequest.getEvidenceCodeUsage(), is(usage));
    }

    @Test
    void getDefaultEvidenceCodeUsage() {
        assertThat(annotationRequest.getEvidenceCodeUsage(), is(DEFAULT_EVIDENCE_CODE_USAGE));
    }

    @Test
    void setAndGetEvidenceCodeIds() {
        String[] usageIds = {"GO:0000001", "GO:0000002"};

        annotationRequest.setEvidenceCode(usageIds);

        assertThat(annotationRequest.getEvidenceCode(), is(usageIds));
    }

    @Test
    void setAndGetEvidenceCodeUsageRelationships() {
        String[] usageRelationships = {"iS_", "paRt_of"};

        annotationRequest.setEvidenceCodeUsageRelationships(usageRelationships);

        String[] expectedLowerCaseRels = Stream.of(usageRelationships)
                .map(String::toLowerCase)
                .toArray(String[]::new);

        assertThat(annotationRequest.getEvidenceCodeUsageRelationships(), arrayContaining(expectedLowerCaseRels));
    }

    @Test
    void canCreateDefaultFilterWithEcoIds() {
        String ecoId = "ECO:0000001";

        annotationRequest.setEvidenceCode(ecoId);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(DESCENDANTS_USAGE)
                .addProperty(AnnotationParameters.EVIDENCE_CODE_PARAM.getName(), ecoId.toUpperCase())
                .addProperty(EVIDENCE_CODE_USAGE_RELATIONS_PARAM.getName())
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void canCreateDefaultFilterWithEcoIdsAndEcoUsageRelationships() {
        String ecoId = "ECO:0000001";
        String relationships = "is_A";

        annotationRequest.setEvidenceCode(ecoId);
        annotationRequest.setEvidenceCodeUsageRelationships(relationships);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(DESCENDANTS_USAGE)
                .addProperty(AnnotationParameters.EVIDENCE_CODE_PARAM.getName(), ecoId.toUpperCase())
                .addProperty(EVIDENCE_CODE_USAGE_RELATIONS_PARAM.getName(), relationships.toLowerCase())
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void canCreateExactFilterWithEcoIds() {
        String ecoId = "ECO:0000001";

        annotationRequest.setEvidenceCode(ecoId);
        annotationRequest.setEvidenceCodeUsage(EXACT_USAGE);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(AnnotationFields.Searchable.EVIDENCE_CODE, ecoId.toUpperCase())
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void canCreateExactFilterWithECOIdsAndUnusedECOUsageRelationships() {
        String ecoId = "ECO:0000001";
        String relationships = "is_A";

        annotationRequest.setEvidenceCode(ecoId);
        annotationRequest.setEvidenceCodeUsage(EXACT_USAGE);
        annotationRequest.setEvidenceCodeUsageRelationships(relationships);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(AnnotationFields.Searchable.EVIDENCE_CODE, ecoId.toUpperCase())
                .build();
        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void createsFilterWithCaseInsensitiveEvidenceCodeUsageAndIds() {
        String usage = "descEndants";
        String id = "ECO:0000001";

        annotationRequest.setEvidenceCodeUsage(usage);
        annotationRequest.setEvidenceCode(id);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(usage.toLowerCase())
                .addProperty(AnnotationParameters.EVIDENCE_CODE_PARAM.getName(), id.toUpperCase())
                .addProperty(EVIDENCE_CODE_USAGE_RELATIONS_PARAM.getName())
                .build();
        assertThat(annotationRequest.createFilterRequests(),
                contains(request));
    }

    @Test
    void createsFilterWithCaseInsensitiveEvidenceCodeUsageAndIdsAndRelationships() {
        String usage = "deSCendants";
        String id = "ECO:0000001";
        String relationships = "is_A";

        annotationRequest.setEvidenceCodeUsage(usage);
        annotationRequest.setEvidenceCode(id);
        annotationRequest.setEvidenceCodeUsageRelationships(relationships);

        assertThat(annotationRequest.createFilterRequests(),
                contains(FilterRequest.newBuilder()
                        .addProperty(usage.toLowerCase())
                        .addProperty(AnnotationParameters.EVIDENCE_CODE_PARAM.getName(), id.toUpperCase())
                        .addProperty(EVIDENCE_CODE_USAGE_RELATIONS_PARAM.getName(), relationships.toLowerCase())
                        .build()));
    }

    @Test
    void cannotCreateFilterWithEvidenceCodeUsageAndNoIds() {
        annotationRequest.setEvidenceCodeUsage("descendants");
        assertThrows(ParameterException.class, () -> annotationRequest.createFilterRequests());
    }

    @Test
    void setAndGetQualifier() {
        String qualifier = "NOT";
        annotationRequest.setQualifier(qualifier);
        assertThat(annotationRequest.getQualifier(), arrayContaining(qualifier));
    }
    //-----------------

    @Test
    void setAndGetReference() {
        String ONE_GOREF = "GO_REF:123456";
        annotationRequest.setReference(ONE_GOREF);
        assertThat(annotationRequest.getReference(), arrayContaining(ONE_GOREF));
    }

    @Test
    void setAndGetECOId() {
        String ecoId = "ECO:0000256";
        annotationRequest.setEvidenceCode(ecoId);
        assertThat(annotationRequest.getEvidenceCode(), arrayContaining(ecoId));
    }

    @Test
    void setAndGetDownloadLimit() {
        int limit = 12345;
        annotationRequest.setDownloadLimit(limit);
        assertThat(annotationRequest.getDownloadLimit(), is(limit));
    }

    @Test
    void setAndGetExtension() {
        String extension = "part_of(CL:0000023),part_of(UBERON:0001305)|part_of(CL:0000501),part_of(UBERON:0001305)";

        annotationRequest.setExtension(extension);

        assertThat(annotationRequest.getExtension(), arrayContaining(extension));
    }

    @Test
    void setAndGetExtensionForWildcard() {
        String extension = "*";

        annotationRequest.setExtension(extension);

        assertThat(annotationRequest.getExtension(), arrayContaining(extension));
    }

    @Test
    void setAndGetGeneProductSubset() {
        String geneProductSubset = "TrEMBL";

        annotationRequest.setGeneProductSubset(geneProductSubset);

        assertThat(annotationRequest.getGeneProductSubset(), arrayContaining(geneProductSubset));
    }

    @Test
    void setAndGetProteome() {
        String proteome = "none";

        annotationRequest.setProteome(proteome);

        assertThat(annotationRequest.getProteome(), arrayContaining(proteome));
    }

    //-----------------
    @Test
    void setAndGetIncludeFields() {
        String field = "goName";
        annotationRequest.setIncludeFields(field);
        assertThat(annotationRequest.getIncludeFields(), arrayContaining(field));
    }

    @Test
    void setAndGetSelectedFields() {
        List<String> selectedFields = Arrays.asList("geneProductId", "symbol", "qualifier", "goId", "goName",
                                               "evidenceCode", "goEvidence","reference","withFrom","taxonId",
                                                    "taxonName", "assignedBy", "extensions", "date", "name", "synonyms",
                                                    "type", "interactingTaxonId");
        for (String field : selectedFields) {
            annotationRequest.setSelectedFields(field);
            assertThat(annotationRequest.getSelectedFields(), arrayContaining(field));
        }
    }

    @Test
    void zeroIncludedFieldResultsInZeroResultTransformationRequest() {
        assertThat(annotationRequest.createResultTransformationRequests().getRequests(), hasSize(0));
    }

    @Test
    void oneIncludedFieldResultsInOneResultTransformationRequest() {
        String field = "goName";
        annotationRequest.setIncludeFields(field);

        Set<ResultTransformationRequest> requests =
                annotationRequest.createResultTransformationRequests().getRequests();
        assertThat(requests, hasSize(1));
        assertThat(requests.iterator().next().getId(), is(field));
    }

    @Test
    void twoIncludedFieldResultsInTwoResultTransformationRequests() {
        String goName = "goName";
        String taxonName = "taxonName";
        String[] fields = {goName, taxonName};
        annotationRequest.setIncludeFields(fields);

        Set<ResultTransformationRequest> requests =
                annotationRequest.createResultTransformationRequests().getRequests();
        assertThat(requests, hasSize(2));

        List<String> requestIds =
                requests.stream().map(ResultTransformationRequest::getId).collect(Collectors.toList());
        assertThat(requestIds, containsInAnyOrder(goName, taxonName));
    }

    //// GOA-3266 and GOA-3130
    @Test
    void cannotCreateFilterWithGeneProductSubsetAndNoGeneProductType() {
        annotationRequest.setGeneProductSubset("Swiss-Prot");
        assertThrows(ParameterException.class, () -> annotationRequest.createFilterRequests());
    }

    @Test
    void cannotCreateFilterWithProteomeAndNoGeneProductType() {
        annotationRequest.setProteome("none");
        assertThrows(ParameterException.class, () -> annotationRequest.createFilterRequests());
    }

    @Test
    void cannotCreateFilterWithProteomeAndGeneProductSubsetAndGeneProductTypeValueOtherThanProtein() {
        annotationRequest.setGeneProductSubset("TrEMBL");
        annotationRequest.setProteome("complete");
        annotationRequest.setGeneProductType("miRNA");
        assertThrows(ParameterException.class, () -> annotationRequest.createFilterRequests());
    }

    @Test
    void canCreateFilterWithProteomeAndGeneProductSubsetAndWhenGeneProductTypeValueIsProtein() {
        String type = "protein";
        String proteome = "gcrpIso";
        String gpSubset = "TrEMBL";
        annotationRequest.setGeneProductSubset(gpSubset);
        annotationRequest.setProteome(proteome);
        annotationRequest.setGeneProductType(type);

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(AnnotationFields.Searchable.GENE_PRODUCT_TYPE, type)
                .addProperty(AnnotationFields.Searchable.PROTEOME, proteome)
                .addProperty(AnnotationFields.Searchable.GENE_PRODUCT_SUBSET, gpSubset)
                .build();

        List<FilterRequest> filterRequests = annotationRequest.createFilterRequests();
        assertThat(filterRequests, contains(request));
    }

    @Test
    void canSetGetAnyString_downloadFileType() {
        annotationRequest.setDownloadFileType("notValidType");
        assertThat(annotationRequest.getDownloadFileType(), equalTo("notValidType"));
    }
    //----------------- helpers
    private String getDefaultTaxonSearchField() {
      return AnnotationFields.Searchable.TAXON_ANCESTORS;
    }
}
