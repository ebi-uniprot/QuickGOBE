package uk.ac.ebi.quickgo.geneproduct.model;

import uk.ac.ebi.quickgo.common.FieldType;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductType;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Represents a domain model instance of a gene product.
 *
 * See http://geneontology.org/page/gene-product-information-gpi-format
 *
 * @author Tony Wardell
 * Date: 22/03/2016
 * Time: 14:10
 * Created with IntelliJ IDEA.
 */
public class GeneProduct {

    // UniProt, RNA Central, Intact
    public String database;

    // e.g. A0A000 http://www.uniprot.org/uniprot/A0A000
    public String id;

    // A (unique and valid) symbol to which DB object ID is matched this field is mandatory, cardinality 1
    public String symbol;

    // name of gene or gene product this field is not mandatory, cardinality 0, 1 [white space allowed]
    public String name;

    // Gene symbol [or other text]
    public List<String> synonyms;

    // Protein; RNA or complex
    public GeneProductType type;

    // taxonomic id(s) The NCBI taxon ID of the species encoding the gene product. this field is mandatory,
    public Taxonomy taxonomy;

    public List<String> databaseSubset;

    // UPID
    public String referenceProteome;

    // The id of the gene product from which this gene product was derived
    public String parentId;

    public boolean isIsoform;

    public boolean isAnnotated;

    //todo maybe.. this value could be replaced with the UPID (as we have for referenceProteome)
    public boolean isCompleteProteome;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Taxonomy implements FieldType {

        // Numeric id e.g. 35758
        public int id;

        // Organism name e.g. Streptomyces ghanaensis
        public String name;

        public Taxonomy(int identifier, String name) {
            this.id = identifier;
            this.name = name;
        }
    }

    @Override
    public String toString() {
        return "GeneProduct{" +
                "database='" + database + '\'' +
                ", id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", synonyms=" + synonyms +
                ", type=" + type +
                ", taxonomy=" + taxonomy +
                ", databaseSubset=" + databaseSubset +
                ", referenceProteome='" + referenceProteome + '\'' +
                ", parentId='" + parentId + '\'' +
                ", isIsoform=" + isIsoform +
                ", isCompleteProteome=" + isCompleteProteome +
                '}';
    }
}