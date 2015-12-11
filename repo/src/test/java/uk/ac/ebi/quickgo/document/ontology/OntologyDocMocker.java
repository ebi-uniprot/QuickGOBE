package uk.ac.ebi.quickgo.document.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.newFlatFieldFromDepth;
import static uk.ac.ebi.quickgo.ff.delim.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Class to newInstance mocked objects of different {@code docType}s, which are valid according to {@link OntologyDocument}.
 *
 * Created 03/11/15
 * @author Edd
 */
public class OntologyDocMocker {

    public static OntologyDocument createGODoc(String id, String name) {
        OntologyDocument od = createOBODoc(id, name);
        od.ontologyType = OntologyType.GO.name();
        od.usage = "some usage";
        od.aspect = Arrays.asList("aspect1", "aspect2");

        return od;
    }

    public static OntologyDocument createECODoc(String id, String name) {
        OntologyDocument od = createOBODoc(id, name);
        od.ontologyType = OntologyType.ECO.name();

        return od;
    }

    public static OntologyDocument createOBODoc(String id, String name) {
        OntologyDocument od = new OntologyDocument();
        od.id = id;
        od.name = name;
        od.definition = "The chemical reactions and pathways involving creatine (N-(aminoiminomethyl)" +
                "-N-methylglycine), a compound synthesized from the amino acids arginine, glycine, and methionine " +
                "that occurs in muscle.";
        od.subsets = Arrays.asList("goslim_pombe",
                "goslim_generic",
                "goslim_yeast",
                "goslim_chembl");
        od.isObsolete = true;
        od.replacedBy = "GO:0000002";
        od.considers = Arrays.asList("GO:0000003", "GO:0000004");
        od.comment = "Note that protein targeting encompasses the transport of the protein to " +
                "the specified location, and may also include additional steps such as protein processing.";
        od.children = Arrays.asList("GO:0000011", "GO:0000012");
        od.synonymNames = Arrays.asList("creatine anabolism", "crayola testarossa");
        od.secondaryIds = Arrays.asList("GO:0000003", "GO:0000004");

        // ------ nested, stored fields, which require reconstructing -------
        // example synonyms
        od.synonyms = new ArrayList<>();
        od.synonyms.add(
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf("creatine anabolism"))
                        .addField(newFlatFieldLeaf("exact"))
                        .buildString()
        );
        od.synonyms.add(
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf("crayola testarossa"))
                        .addField(newFlatFieldLeaf("inprecise"))
                        .buildString()
        );

        // example history
        od.history = new ArrayList<>();
        od.history.add(
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf("Gonna do something like it's ... "))
                        .addField(newFlatFieldLeaf("11:59, 31 Dec, 1999"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Textual description"))
                        .buildString()
        );
        od.history.add(
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf("History name"))
                        .addField(newFlatFieldLeaf("Tuesday next week"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Okay"))
                        .buildString()
        );

        // example xrefs
        od.xrefs = new ArrayList<>();
        od.xrefs.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("InterPro"))
                .addField(newFlatFieldLeaf("IPR031034"))
                .addField(newFlatFieldLeaf("Creatinine amidohydrolase"))
                .buildString());
        od.xrefs.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("AnotherXref"))
                .addField(newFlatFieldLeaf("IPR031035"))
                .addField(newFlatFieldLeaf("Pickled Onions"))
                .buildString());

        // example taxonomy constraints
        // format: ancestorId|ancestorName|relationship|taxId|taxIdType|taxName|pubMedId1&pubMedId2|blacklist
        od.taxonConstraints = new ArrayList<>();
        od.taxonConstraints.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("GO:0005623"))
                .addField(newFlatFieldLeaf("cell"))
                .addField(newFlatFieldLeaf("only_in_taxon"))
                .addField(newFlatFieldLeaf("131567"))
                .addField(newFlatFieldLeaf("NCBITaxon"))
                .addField(newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatFieldFromDepth(3)
                        .addField(newFlatFieldLeaf("PMID:00000001"))
                        .addField(newFlatFieldLeaf("PMID:00000002")))
                .buildString());
        od.taxonConstraints.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("GO:0005624"))
                .addField(newFlatFieldLeaf("cell"))
                .addField(newFlatFieldLeaf("only_in_taxon"))
                .addField(newFlatFieldLeaf("131568"))
                .addField(newFlatFieldLeaf("NCBITaxon"))
                .addField(newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatFieldFromDepth(3)
                        .addField(newFlatFieldLeaf("PMID:00000003"))
                        .addField(newFlatFieldLeaf("PMID:00000004")))
                .buildString());

        // example blacklist
        // format: geneProductId|geneProductDB|reason|category|method
        od.blacklist = new ArrayList<>();
        od.blacklist.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("GP:00001"))
                .addField(newFlatFieldLeaf("GP"))
                .addField(newFlatFieldLeaf("because it's bad"))
                .addField(newFlatFieldLeaf("category 1"))
                .addField(newFlatFieldLeaf("automatic"))
                .buildString());
        od.blacklist.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("XX:00002"))
                .addField(newFlatFieldLeaf("XX"))
                .addField(newFlatFieldLeaf("because it's also bad"))
                .addField(newFlatFieldLeaf("category 2"))
                .addField(newFlatFieldLeaf()) // no parameter means it's got no value
                .buildString());

        // example xontology relations
        // format: xId|xTerm|xNamespace|xUrl|xRelation
        od.xRelations = new ArrayList<>();
        od.xRelations.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("CHEBI:16919"))
                .addField(newFlatFieldLeaf("creatine"))
                .addField(newFlatFieldLeaf("CHEBI"))
                .addField(newFlatFieldLeaf("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16919"))
                .addField(newFlatFieldLeaf("has_participant"))
                .buildString());
        od.xRelations.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("CHEBI:16920"))
                .addField(newFlatFieldLeaf("creatiney"))
                .addField(newFlatFieldLeaf("CHEBI"))
                .addField(newFlatFieldLeaf("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16920"))
                .addField(newFlatFieldLeaf("has_participant"))
                .buildString());

        // annotation guidelines
        // format: description|url
        od.annotationGuidelines = new ArrayList<>();
        od.annotationGuidelines.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("description 0"))
                .addField(newFlatFieldLeaf("http://www.guardian.co.uk"))
                .buildString()
        );
        od.annotationGuidelines.add(newFlatFieldFromDepth(2)
                .addField(newFlatFieldLeaf("description 1"))
                .addField(newFlatFieldLeaf("http://www.pinkun.com"))
                .buildString()
        );

        return od;
    }

    public static String createOBODelimitedStr(OntologyDocument document) {
        String sep1 = "|||";
        String sep0 = "\t";

        return emptyOrString(document.id) + sep0 +
                emptyOrString(document.name) + sep0 +
                document.isObsolete + sep0 +
                emptyOrString(document.definition) + sep0 +
                emptyOrString(document.comment) + sep0 +
                emptyOrString(document.secondaryIds, sep1) + sep0 +
                emptyOrString(document.usage) + sep0 +
                emptyOrString(document.synonyms, sep1) + sep0 +
                //emptyOrString(document.synonymNames, sep1) + sep0 +
                emptyOrString(document.subsets, sep1) + sep0 +
                emptyOrString(document.replacedBy) + sep0 +
                emptyOrString(document.considers, sep1) + sep0 +
                emptyOrString(document.children, sep1) + sep0 +
                emptyOrString(document.ancestors, sep1) + sep0 +
                emptyOrString(document.aspect, sep1) + sep0 +
                emptyOrString(document.history, sep1) + sep0 +
                emptyOrString(document.xrefs, sep1) + sep0 +
                emptyOrString(document.taxonConstraints, sep1) + sep0 +
                emptyOrString(document.blacklist, sep1) + sep0 +
                emptyOrString(document.annotationGuidelines, sep1) + sep0 +
                emptyOrString(document.xRelations, sep1);
    }

    private static String emptyOrString(String value) {
        return value == null ? "" : value;
    }

    private static String emptyOrString(List<String> values, String separator) {
        return values == null ? "" : values.stream().collect(Collectors.joining(separator));
    }

}
