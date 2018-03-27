package uk.ac.ebi.quickgo.geneproduct.common.common;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership;

import java.util.Collections;

/**
 * Class to create mocked objects of type {@link GeneProductDocument}.
 */
public final class GeneProductDocMocker {
    private GeneProductDocMocker() {}

    public static GeneProductDocument createDocWithId(String id) {
        GeneProductDocument doc = new GeneProductDocument();
        doc.id = id;
        doc.type = "protein";
        doc.taxonId = 35758;
        doc.taxonName = "Streptomyces ghanaensis";
        doc.symbol = "Streptomyces ghanaensis - symbol";
        doc.parentId = "UniProtKB:OK0206";
        doc.database = "UniProt";
        doc.databaseSubset = "RRR";
        doc.isAnnotated = true;
        doc.isIsoform = true;
        doc.isCompleteProteome = true;
        doc.name = "moeA5";
        doc.referenceProteome = "AAAA";
        doc.synonyms = Collections.singletonList("3SSW23");
        doc.targetSet = Collections.singletonList("KRUK");
        doc.proteomeMembership = ProteomeMembership.COMPLETE.toString();
        return doc;
    }
}
