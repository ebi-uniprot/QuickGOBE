package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Tests the {@link ECOController} class. All tests for ECO
 * are covered by the tests in the parent class, {@link OBOControllerIT}.
 *
 * Created 24/11/15
 * @author Edd
 */
public class ECOControllerIT extends OBOControllerIT {
    private static final String RESOURCE_URL = "/ontology/eco";
    private static final String ECO_0000001 = "ECO:0000001";
    private static final String ECO_0000002 = "ECO:0000002";
    private static final String ECO_0000003 = "ECO:0000003";
    private static final String ECO_0000004 = "ECO:0000004";

    @Override protected OntologyDocument createBasicDoc(String id, String name) {
        return OntologyDocMocker.createECODoc(id, name);
    }

    @Override
    protected List<OntologyDocument> createBasicDocs() {
        return Arrays.asList(
                OntologyDocMocker.createECODoc(ECO_0000001, "doc name 1"),
                OntologyDocMocker.createECODoc(ECO_0000002, "doc name 2"),
                OntologyDocMocker.createECODoc(ECO_0000003, "doc name 3"),
                OntologyDocMocker.createECODoc(ECO_0000004, "doc name 4"));
    }

    @Override protected List<OntologyDocument> createNDocs(int n) {
        return IntStream.range(1, n + 1)
                .mapToObj(i -> OntologyDocMocker.createECODoc(createId(i), "eco doc name " + i)).collect
                (Collectors.toList());
    }

    @Override
    protected String createId(int idNum) {
        return String.format("ECO:%07d", idNum);
    }

    @Override
    protected String getResourceURL() {
        return RESOURCE_URL;
    }

    @Override
    protected String idMissingInRepository() {
        return "ECO:0000399";
    }

    @Override
    protected String invalidId() {
        return "ECO|0000001";
    }

}
