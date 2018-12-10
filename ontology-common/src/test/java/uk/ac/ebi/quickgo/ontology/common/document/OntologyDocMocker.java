package uk.ac.ebi.quickgo.ontology.common.document;

import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Class to create stubbed {@link OntologyDocument} instances.
 *
 * Created 03/11/15
 * @author Edd
 */
public final class OntologyDocMocker {
    private OntologyDocMocker() {}

    public static OntologyDocument createGODoc(String id, String name) {
        OntologyDocument od = createOBODoc(id, name);
        od.ontologyType = OntologyType.GO.name();
        od.usage = "Unrestricted";
        od.aspect = "Process";

        // example blacklist
        // format: goId|category|entityType|entityId|taxonId|ancestorGoId|reason|methodId
        od.blacklist = new ArrayList<>();
        od.blacklist.add(newFlatField()
                .addField(newFlatFieldLeaf("GO:0000001"))
                .addField(newFlatFieldLeaf("NOT-qualified manual"))
                .addField(newFlatFieldLeaf("protein"))
                .addField(newFlatFieldLeaf("A5I1R9"))
                .addField(newFlatFieldLeaf("441771"))
                .addField(newFlatFieldLeaf("A5I1R9_CLOBH"))
                .addField(newFlatFieldLeaf("GO:0007005"))
                .addField(newFlatFieldLeaf("1 NOT-qualified manual etc"))
                .addField(newFlatFieldLeaf("IER12345"))
                .buildString());
        od.blacklist.add(newFlatField()
                .addField(newFlatFieldLeaf("GO:0000001"))
                .addField(newFlatFieldLeaf("IS-qualified manual"))
                .addField(newFlatFieldLeaf("protein"))
                .addField(newFlatFieldLeaf("B5I1R9"))
                .addField(newFlatFieldLeaf("441771"))
                .addField(newFlatFieldLeaf("B5I1R9_CLOBH"))
                .addField(newFlatFieldLeaf("GO:0007006"))
                .addField(newFlatFieldLeaf("1 NOT-qualified manual etc"))
                .addField(newFlatFieldLeaf("IER12346"))
                .buildString());

        od.goDiscussions = new ArrayList<>();
        od.goDiscussions.add(newFlatField()
                .addField(newFlatFieldLeaf("Viral Processes"))
                .addField(newFlatFieldLeaf("http://wiki.geneontology.org/index.php/Virus_terms"))
                .buildString());
        od.goDiscussions.add(newFlatField()
                .addField(newFlatFieldLeaf("signalling"))
                .addField(newFlatFieldLeaf("http://wiki.geneontology.org/index.php/Signaling"))
                .buildString());

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
        od.definitionXrefs = Collections.singletonList(newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf("PMID"))
                .addField(FlatFieldLeaf.newFlatFieldLeaf("21494263"))
                .buildString());
        od.isObsolete = true;
        od.comment = "Note that protein targeting encompasses the transport of the protein to " +
                "the specified location, and may also include additional steps such as protein processing.";
        od.synonymNames = Arrays.asList("creatine anabolism", "crayola testarossa");
        od.secondaryIds = Arrays.asList("GO:0000003", "GO:0000004");
        od.subsets = Arrays.asList("goslim_pombe",
                "goslim_generic",
                "goslim_yeast",
                "goslim_chembl");

        // ------ nested, stored fields, which require reconstructing -------
        // example synonyms
        od.synonyms = new ArrayList<>();
        od.synonyms.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("creatine anabolism"))
                        .addField(newFlatFieldLeaf("exact"))
                        .buildString()
        );
        od.synonyms.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("crayola testarossa"))
                        .addField(newFlatFieldLeaf("inprecise"))
                        .buildString()
        );

        // example history
        od.history = new ArrayList<>();
        od.history.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("Gonna do something like it's ... "))
                        .addField(newFlatFieldLeaf("11:59, 31 Dec, 1999"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Textual description"))
                        .buildString()
        );
        od.history.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("History name"))
                        .addField(newFlatFieldLeaf("Tuesday next week"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Okay"))
                        .buildString()
        );

        // example xrefs
        od.xrefs = new ArrayList<>();
        od.xrefs.add(newFlatField()
                .addField(newFlatFieldLeaf("InterPro"))
                .addField(newFlatFieldLeaf("IPR031034"))
                .addField(newFlatFieldLeaf("Creatinine amidohydrolase"))
                .buildString());
        od.xrefs.add(newFlatField()
                .addField(newFlatFieldLeaf("AnotherXref"))
                .addField(newFlatFieldLeaf("IPR031035"))
                .addField(newFlatFieldLeaf("Pickled Onions"))
                .buildString());

        // example taxonomy constraints
        // format: ancestorId|ancestorName|relationship|taxId|taxIdType|taxName|pubMedId1&pubMedId2|blacklist
        od.taxonConstraints = new ArrayList<>();
        od.taxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf("GO:0005623"))
                .addField(newFlatFieldLeaf("cell"))
                .addField(newFlatFieldLeaf("only_in_taxon"))
                .addField(newFlatFieldLeaf("131567"))
                .addField(newFlatFieldLeaf("NCBITaxon"))
                .addField(newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("PMID:00000001"))
                        .addField(newFlatFieldLeaf("PMID:00000002")))
                .buildString());
        od.taxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf("GO:0005624"))
                .addField(newFlatFieldLeaf("cell"))
                .addField(newFlatFieldLeaf("only_in_taxon"))
                .addField(newFlatFieldLeaf("131568"))
                .addField(newFlatFieldLeaf("NCBITaxon"))
                .addField(newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("PMID:00000003"))
                        .addField(newFlatFieldLeaf("PMID:00000004")))
                .buildString());

        // example xontology relations
        // format: xId|xTerm|xNamespace|xUrl|xRelation
        od.xRelations = new ArrayList<>();
        od.xRelations.add(newFlatField()
                .addField(newFlatFieldLeaf("CHEBI:16919"))
                .addField(newFlatFieldLeaf("creatine"))
                .addField(newFlatFieldLeaf("CHEBI"))
                .addField(newFlatFieldLeaf("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16919"))
                .addField(newFlatFieldLeaf("has_participant"))
                .buildString());
        od.xRelations.add(newFlatField()
                .addField(newFlatFieldLeaf("CHEBI:16920"))
                .addField(newFlatFieldLeaf("creatiney"))
                .addField(newFlatFieldLeaf("CHEBI"))
                .addField(newFlatFieldLeaf("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16920"))
                .addField(newFlatFieldLeaf("has_participant"))
                .buildString());

        // annotation guidelines
        // format: description|url
        od.annotationGuidelines = new ArrayList<>();
        od.annotationGuidelines.add(newFlatField()
                .addField(newFlatFieldLeaf("description 0"))
                .addField(newFlatFieldLeaf("http://www.guardian.co.uk"))
                .buildString()
        );
        od.annotationGuidelines.add(newFlatField()
                .addField(newFlatFieldLeaf("description 1"))
                .addField(newFlatFieldLeaf("http://www.pinkun.com"))
                .buildString()
        );

        // replaces
        //format: goTermId|relationType
        od.replaces = new ArrayList<>();
        od.replaces.add(createFlatRelation("GO:1111111", "replaced_by"));

        od.replacements = new ArrayList<>();
        od.replacements.add(createFlatRelation("GO:0000002", "replaced_by"));
        od.replacements.add(createFlatRelation("GO:0000003", "consider"));
        od.replacements.add(createFlatRelation("GO:0000004", "consider"));

        od.credits = new ArrayList<>();
        od.credits.add(newFlatField()
                .addField(newFlatFieldLeaf("BHF"))
                .addField(newFlatFieldLeaf("http://www.ucl.ac.uk/cardiovasculargeneontology/"))
                .buildString());

        return od;
    }

    private static String createFlatRelation(String id, String relationType) {
        return newFlatField()
                .addField(newFlatFieldLeaf(id))
                .addField(newFlatFieldLeaf(relationType))
                .buildString();
    }
}