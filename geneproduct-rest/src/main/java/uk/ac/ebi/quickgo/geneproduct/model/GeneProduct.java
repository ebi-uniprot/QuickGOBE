package uk.ac.ebi.quickgo.geneproduct.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import uk.ac.ebi.quickgo.common.FieldType;

import java.util.List;
import java.util.Optional;

/**
 * @Author Tony Wardell
 * Date: 22/03/2016
 * Time: 14:10
 * Created with IntelliJ IDEA.
 * See http://geneontology.org/page/gene-product-information-gpi-format
 */
public class GeneProduct {

	//UniProt, RNA Central, Intact
	public String database;

	//e.g. A0A000 http://www.uniprot.org/uniprot/A0A000
	public String identifier;

	//A (unique and valid) symbol to which DB object ID is matched this field is mandatory, cardinality 1
	public String symbol;

	//name of gene or gene product this field is not mandatory, cardinality 0, 1 [white space allowed]
	public Optional<String> name;

	//Gene symbol [or other text]
	public List<Synonym> synonyms;

	//A description of the type of the gene or gene product being annotated. one of the following: protein_complex;
	// protein; transcript; ncRNA; rRNA; tRNA; snRNA; snoRNA; any subtype of ncRNA in the Sequence Ontology.
	public String type;

	//taxonomic identifier(s) The NCBI taxon ID of the species encoding the gene product. this field is mandatory,
	public Taxonomy taxonomy;

	public String databaseSubset;

	//UPID
	public String referenceProteome;

	public boolean isIsoform;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class Synonym implements FieldType {
		public String synonymName;
		public String synonymType;
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class Taxonomy implements FieldType {

		//Numeric id e.g. 35758
		public String identifier;

		//Organism name e.g. Streptomyces ghanaensis
		public String name;
	}
}
