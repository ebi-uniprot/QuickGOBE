package uk.ac.ebi.quickgo.ontology.generic;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.quickgo.render.JSONSerialise;

public class GenericTermSet implements JSONSerialise, ITermContainer {
    static NumberFormat nf = new DecimalFormat("0000000");

    protected GenericOntology ontology;
    public String name;
    public int colour;

    protected Map<GenericTerm, String> contents = new TreeMap<>();

    public GenericTermSet(GenericOntology ontology, String name, int colour) {
        this.ontology = ontology;
        this.name = name;
        this.colour = colour;
    }

    public GenericTermSet(GenericOntology ontology) {
        this(ontology, null, (Integer)1);
    }

    public GenericTermSet(GenericOntology ontology, String name) {
        this(ontology, name, (Integer)1);
    }

    public GenericOntology getOntology() {
    	return ontology;
    }
    
    public int getColour() {
    	return colour;
    }
    
    public String getName() {
    	return name;
    }

    public void addRemoveAll(String[] all, boolean add) {
        if (all != null) {
	        for (String idList : all) {
	            for (String id : idList.split("[^-A-Za-z0-9#\\:]+")) {
	                addRemove(id, add);
	            }
	        }
        }
    }

    private void addRemove(String id, boolean add) {
		String[] termColour = id.split("#");
        GenericTerm t = ontology.getTerm(termColour[0]);
        if (t != null) {
	        String colour = (termColour.length > 1) ? termColour[1] : "";
            if (add) {
	            contents.put(t, colour);
            }
			else{
	            contents.remove(t);
            }
        }
    }

    public void add(GenericTerm term) {
        contents.put(term, "");
    }

    public void add(int id) {
        add(ontology.namespace + ":" + nf.format(id));
    }

    public void add(String id) {
        addRemove(id, true);
    }

    public void addAll(String[] all) {
        addRemoveAll(all, true);
    }

    public void removeAll(String[] all) {
        addRemoveAll(all, false);        
    }

    private static final String indexTable = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz[]";
	private static final String sentinel = "64$";

	public static String convert10to64(int decimalInt) {
		String s = "";

		while (decimalInt > 0) {
			s = indexTable.charAt(decimalInt % 64) + s;
			decimalInt = (int)Math.floor(decimalInt / 64);
		}

		return s;
	}

	public static int convert64to10(String num) {
		int ret = 0;

		for (int x = 1; num.length() > 0; x *= 64) {
			ret += indexTable.indexOf(num.charAt(num.length() - 1)) * x;
			num = num.substring(0, num.length() - 1);
		}

		return ret;
	}

    public void addCompressed(String code) {
        if (code != null) {
			if (code.length() >= sentinel.length() && sentinel.equals(code.substring(0, sentinel.length()))) {
				for (int i = sentinel.length(); i < code.length(); i += 4) {
					add(convert64to10(code.substring(i, i + 4)));
				}
			}
			else {
				// old-style base 36 encoding
				for (int i = 0; i < code.length(); i += 4) {
					add(Integer.parseInt(code.substring(i, i + 4), 36));
				}
			}
        }
    }

    public String getCompressed() {
        StringBuilder compressed = new StringBuilder();
        for (GenericTerm t : contents.keySet()) {
	        String h = convert10to64(t.getCode());
            compressed.append("0000".substring(h.length())).append(h);
        }
        return compressed.length() > 0 ? sentinel + compressed.toString() : "";
    }

    public String getIdList() {
        StringBuilder compressed = new StringBuilder();
        for (GenericTerm t : contents.keySet()) {
            compressed.append(t.getId()).append(" ");
        }
        return compressed.toString();
    }

    public void clear() {
        contents.clear();
    }

	public String getTermColour(GenericTerm term) {
		return contents.get(term);
	}

	// implementation of JSONSerialise interface
    public Map<String,Object> serialise() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
    	return map;
    }

    // implementation of ITermContainer interface
	@Override
	public String getNamespace() {
		return ontology.getNamespace();
	}

	@Override
	public int getTermCount() {
    	return contents.size();
	}

	@Override
	public List<GenericTerm> getTerms() {
        return new ArrayList<>(contents.keySet());
	}

	@Override
    public List<String> getTermIds() {
		List<String> ids = new ArrayList<>(contents.size());
        for (GenericTerm t : contents.keySet()) {
            ids.add(t.getId());
        }
    	return ids;
    }
	
	@Override
	public GenericTerm[] toArray() {
        return contents.keySet().toArray(new GenericTerm[contents.size()]);
	}

	@Override
	public GenericTerm getTerm(String id) {
		for (GenericTerm t : contents.keySet()) {
			if (id.equals(t.getId())) {
				return t;
			}
		}
		return null;
	}

	@Override
	public void addTerm(GenericTerm t) {
		add(t);
	}
}
