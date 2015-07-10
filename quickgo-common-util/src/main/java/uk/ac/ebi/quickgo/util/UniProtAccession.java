package uk.ac.ebi.quickgo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that represents a UniProt accession, and that, more importantly, is able to determine whether an arbitrary string is a plausible accession
 *
 * $Date$
 * $Revision$
 * <p/>
 * $Log$
 */
public class UniProtAccession {
   	public String accession;	// the full accession, including any isoform / variant
   	public String canonical;	// the canonical accession

   	private final static Pattern uniProtAcPattern = Pattern.compile("^(?:(UniProt|UniProtKB(?:/TrEMBL|/Swiss-Prot)?):)?(([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])((-([0-9]+))|:(PRO_[0-9]{10}|VAR_[0-9]{6}))?)$");
   	private final static Matcher uniProtAcMatcher = uniProtAcPattern.matcher("");

   	public UniProtAccession(String accession, String canonical) {
   		this.accession = accession;
   		this.canonical = canonical;
   	}

   	public static UniProtAccession parse(String db, String dbObjectId) {
   		return parse(db + ":" + dbObjectId);
   	}

   	public static UniProtAccession parse(String s) {
   		uniProtAcMatcher.reset(s);
   		if (uniProtAcMatcher.matches()) {
   			// group(2) = accession + isoform/variant/chain, group(3) = canonical accession
   			return new UniProtAccession(uniProtAcMatcher.group(2), uniProtAcMatcher.group(3));
   		}
   		else {
   			return null;
   		}
   	}

	public static String getCanonicalAccession(String s) {
	   uniProtAcMatcher.reset(s);
	   return uniProtAcMatcher.matches() ? uniProtAcMatcher.group(3) : null;
	}

    public static boolean isValid(String db, String id) {
   		uniProtAcMatcher.reset(db + ":" + id);
   		return uniProtAcMatcher.matches();
   	}

    public static boolean isValid(String candidate) {
   		uniProtAcMatcher.reset(candidate);
   		return uniProtAcMatcher.matches();
   	}

	@Override
	public String toString() {
		return "UniProtAccession{" +
				"accession='" + accession + '\'' +
				", canonical='" + canonical + '\'' +
				'}';
	}
}
