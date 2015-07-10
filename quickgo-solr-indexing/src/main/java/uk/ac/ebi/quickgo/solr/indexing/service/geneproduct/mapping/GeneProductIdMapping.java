package uk.ac.ebi.quickgo.solr.indexing.service.geneproduct.mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.solr.indexing.service.geneproduct.GeneProductIndexer;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct.SolrGeneProductDocumentType;
import uk.ac.ebi.quickgo.util.UniProtAccession;
import uk.ac.ebi.quickgo.util.XRef;

/**
 * For reading gp2protein files 
 * Rows in these files look like this: "MIM:100640	UniProtKB:P00352;UniProtKB:P00999|UniProtKB:P00300"
 * @author cbonill
 *
 */
@Service("productIdMapping")
public class GeneProductIdMapping {
	/**
	 * Gene products indexer
	 */
	@Autowired
	GeneProductIndexer geneProductIndexer;
	
	/**
	 * Reads a gp2protein file and extracts mappings between identifiers in a foreign namespace and UniProt accessions
	 *
	 * column 1 is the (fully-qualified) identifier (i.e., namespace:identifier) of a gene product
	 * column 2 is a list (pipe- or semi-colon-separated) of identifiers to which the identifier in column 1 is mapped; we are only interested in mappings to UniProt accessions
	 *
	 * @param file gp2protein file
	 * @throws IOException
	 */
	public void readAndIndexGPMappings(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		List<GeneProduct> geneProducts = new ArrayList<>();
		String line;

		while ((line = br.readLine()) != null) {
			// skip over comment lines
			if (line.startsWith("!")) {
				continue;
			}

			// if this line doesn't have (at least) two columns, skip it
			String[] columns = line.split("\t", 2);
			if (columns.length != 2) {
				continue;
			}

			// parse the foreign identifier
			XRef foreigner = XRef.parse(columns[0]);
			if (foreigner != null) {
				// does it map to any UniProt accessions?
				for (String mapping : columns[1].split(";|\\|")) {
					UniProtAccession accession = UniProtAccession.parse(mapping);
					if (accession != null) {
						GeneProduct geneProduct = new GeneProduct();
						geneProduct.setDbObjectId(accession.canonical);
						geneProduct.setXRefs(Collections.singletonList(foreigner));
						geneProducts.add(geneProduct);
					}
				}

				if (geneProducts.size() >= 200000) {
					geneProductIndexer.index(geneProducts, Collections.singletonList(SolrGeneProductDocumentType.getAsInterface(SolrGeneProductDocumentType.XREF)));
					geneProducts = new ArrayList<>();
				}
			}
		}

		// index the remainder
		if (geneProducts.size() > 0) {
			geneProductIndexer.index(geneProducts, Collections.singletonList(SolrGeneProductDocumentType.getAsInterface(SolrGeneProductDocumentType.XREF)));
		}
	}
}