package uk.ac.ebi.quickgo.geneproduct.common.common;

import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;

/**
 * Class to create mocked objects of different {@code docType}s, which are valid according to {@link OntologyDocument}.
 */
public final class GeneProductDocMocker {
    private GeneProductDocMocker() {}

    public static GeneProductDocument createDocWithId(String id) {
        GeneProductDocument doc = new GeneProductDocument();
        doc.id = id;

        return doc;
    }
}
