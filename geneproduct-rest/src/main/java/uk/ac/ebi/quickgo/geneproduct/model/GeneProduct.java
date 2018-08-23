package uk.ac.ebi.quickgo.geneproduct.model;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;

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

    // NCBI taxon id of the species encoding the gene product.
    public int taxonId;

    public String databaseSubset;

    // The id of the gene product from which this gene product was derived
    public String parentId;

    public String proteome;

    @Override public String toString() {
        return "GeneProduct{" +
                "database='" + database + '\'' +
                ", id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", synonyms=" + synonyms +
                ", type=" + type +
                ", taxonId=" + taxonId +
                ", databaseSubset='" + databaseSubset + '\'' +
                ", parentId='" + parentId + '\'' +
                ", proteome=" + proteome +
                '}';
    }
}
