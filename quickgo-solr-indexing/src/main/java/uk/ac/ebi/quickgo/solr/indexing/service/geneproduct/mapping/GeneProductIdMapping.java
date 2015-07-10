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
import uk.ac.ebi.quickgo.util.XRef;

/**
 * For reading gp2protein files 
 * Rows in these files look like this: "MIM:100640	UniProtKB:P00352;UniProtKB:P00999|UniProtKB:P00300"
 * @author cbonill
 *
 */
@Service("productIdMapping")
public class GeneProductIdMapping {

	private final String UNIPROTKB = "UniProtKB";
	
	/**
	 * Gene products indexer
	 */
	@Autowired
	GeneProductIndexer geneProductIndexer;
	
	/**
	 * Reads a gp2protein file and returns a list of gene products objects containing the mappings
	 * @param file gp2protein file
	 * @throws IOException
	 */
	public void readAndIndexGPMappings(File file) throws IOException {
		
		List<GeneProduct> geneProducts = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("!")) {// Header lines
				continue;
			}
			// Get the 2 columns
			String[] ids = line.split("\t");

			// Get Xref DB and ID
			String xref = ids[0];
			String xrefDb = xref.split(":", 2)[0];
			String xrefId = xref.split(":", 2)[1];
			
			// Get UniProt/s accessions
			String sources = ids[1];
			String[] sourceDbs = sources.split(";|\\|");// Source databases are ";" or "|" separated
			for (String sourceDb : sourceDbs) {
				if (sourceDb.contains(UNIPROTKB)) {// We just care about UniProt
					XRef ref = new XRef(xrefDb, xrefId);
					GeneProduct geneProduct = new GeneProduct();
					geneProduct.setDbObjectId(sourceDb.split(":")[1]);// Gene product id
					geneProduct.setXRefs(Collections.singletonList(ref));
					geneProducts.add(geneProduct);
				}
			}
			if(geneProducts.size() % 200000 == 0){
				geneProductIndexer.index(geneProducts, Collections.singletonList(SolrGeneProductDocumentType.getAsInterface(SolrGeneProductDocumentType.XREF)));
				geneProducts = new ArrayList<>();
			}
		}
		geneProductIndexer.index(geneProducts, Collections.singletonList(SolrGeneProductDocumentType.getAsInterface(SolrGeneProductDocumentType.XREF)));
	}
}