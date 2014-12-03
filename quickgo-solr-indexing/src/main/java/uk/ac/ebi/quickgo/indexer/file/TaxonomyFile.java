package uk.ac.ebi.quickgo.indexer.file;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;

/**
 * Taxonomy file 
 * @author cbonill
 *
 */
public class TaxonomyFile {
	
	private static final int COLUMN_TAXONOMY_ID = 0;
	
	private static final int COLUMN_TAXONOMY_NAME = 1;
	
	private static final int COLUMN_TAXONOMY_CLOSURE = 2;
	
	public Miscellaneous calculateRow(String[] columns){
		String taxonomyId = columns[COLUMN_TAXONOMY_ID];
		String taxonomyName = columns[COLUMN_TAXONOMY_NAME];
		String taxonomyClosure = columns[COLUMN_TAXONOMY_CLOSURE];		
		List<Integer> taxonomyClosures = new ArrayList<>();
		String[] values = taxonomyClosure.split("\\|");
		for (String value : values) {
			taxonomyClosures.add(Integer.valueOf(value));
		}
		
		Miscellaneous miscellaneousObject = new Miscellaneous();
		miscellaneousObject.setTaxonomyId(Integer.valueOf(taxonomyId));
		miscellaneousObject.setTaxonomyName(taxonomyName);
		miscellaneousObject.setTaxonomyClosure(taxonomyClosures);
		
		return miscellaneousObject;
	}
}