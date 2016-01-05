package uk.ac.ebi.quickgo.model.ontology.go;

import java.util.HashMap;

/**
 * class that maps a combination of GO evidence code + GO_REF to the equivalent ECO id
 * 
 * @author tonys
 *
 */
public class GOEvidence2ECOMap {

	private static final String defaultKey = "default";
	private static HashMap<String, String> map = new HashMap<>();
	static{
		map.put("default", "ECO:0000247");
		map.put("RCA", "ECO:0000245");
		map.put("ISA", "ECO:0000247");
		map.put("ISS", "ECO:0000250");
		map.put("ISM", "ECO:0000255");
		map.put("ISO", "ECO:0000266");
		//map.put("EXP", "ECO:0000269");
		map.put("IEP", "ECO:0000270");
		map.put("NAS", "ECO:0000303");
		map.put("TAS", "ECO:0000304");
		map.put("IC", "ECO:0000305");
		map.put("ND", "ECO:0000307");
		map.put("IDA", "ECO:0000314");
		map.put("IMP", "ECO:0000315");
		map.put("IGI", "ECO:0000316");
		map.put("IGC", "ECO:0000317");
		map.put("IBA", "ECO:0000318");
		map.put("IBD", "ECO:0000319");
		map.put("IKR", "ECO:0000320");
		map.put("IMR", "ECO:0000320");
		map.put("IRD", "ECO:0000321");
		map.put("IPI", "ECO:0000353");
		map.put("IEA", "ECO:0000501");					
	}	
	
	public static String find(String goRef) {
		if (goRef == null) {
			return map.get(defaultKey);
		}
		else {
			String s = map.get(goRef);
			if (s == null) {
				s = map.get(defaultKey);
			}
			return s;
		}
	}
	
	/*static class GORef2ECOMap {
		private HashMap<String, String> map = new HashMap<>();
		private static final String defaultKey = "default";

		public void add(String goRef, String ecoId) {
			map.put((goRef == null || "".equals(goRef)) ? defaultKey : goRef, ecoId);
		}

		public String find(String goRef) {
			if (goRef == null) {
				return map.get(defaultKey);
			}
			else {
				String s = map.get(goRef);
				if (s == null) {
					s = map.get(defaultKey);
				}
				return s;
			}
		}
	}

	private HashMap<String, GORef2ECOMap> map = new HashMap<>();

	public GORef2ECOMap get(String code) {
		GORef2ECOMap m = map.get(code);
		if (m == null) {
			m = new GORef2ECOMap();
			map.put(code, m);
		}
		return m;
	}

	public GOEvidence2ECOMap(SourceFiles directory) throws Exception {
		for (String[] row : directory.evidence2ECO.reader(EGOEvidence2ECOTranslation.CODE, EGOEvidence2ECOTranslation.GO_REF, EGOEvidence2ECOTranslation.ECO_ID)) {
			get(row[0]).add(row[1], row[2]);
		}
	}

	public String translate(String code, String goRef) {
		GORef2ECOMap m = get(code);
		return (m != null) ? m.find(goRef) : null;
	}*/
}
