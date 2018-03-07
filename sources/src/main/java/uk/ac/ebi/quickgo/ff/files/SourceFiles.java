package uk.ac.ebi.quickgo.ff.files;

import uk.ac.ebi.quickgo.ff.files.ontology.ECOSourceFiles;
import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.reader.Progress;
import uk.ac.ebi.quickgo.ff.reader.RowIterator;
import uk.ac.ebi.quickgo.ff.reader.TSVRowReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SourceFiles {
	public static final String VERSION = "100";

	File baseDirectory;

	public SourceFiles(File directory) {
		this.baseDirectory = directory;
		goSourceFiles = new GOSourceFiles(baseDirectory);
		ecoSourceFiles = new ECOSourceFiles(baseDirectory);
		annotationGuidelines = new TSVDataFile<>(baseDirectory, "ANNOTATION_GUIDELINES");
		annotationBlacklist = new TSVDataFile<>(baseDirectory, "ANNOTATION_BLACKLIST");
		xrfAbbsInfo = new TSVDataFile<>(baseDirectory, "XRF_ABBS");
		evidenceInfo = new TSVDataFile<>(baseDirectory, "CV_ECO2GO");
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public static NamedFile[] holder(NamedFile... files) {
	    return files;
	}

	public static NamedFile[] holder(NamedFile[]... files) {
	    List<NamedFile> list = new ArrayList<>();
	    for (NamedFile[] f : files) {
	    	list.addAll(Arrays.asList(f));
	    }
	    return list.toArray(new NamedFile[list.size()]);
	}

	public static class NamedFile {
		public File directory;
		public String name;

	    public NamedFile(File directory, String name) {
	    	this.directory = directory;
	        this.name = name;
	    }

	    public File file() {
	        return new File(directory, name);
	    }

	    public String getName() {
	        return name;
	    }

		public File getDirectory() {
			return directory;
		}
	}

	public static class TSVDataFile<X extends Enum<X>> extends NamedFile {

	    public TSVDataFile(File directory, String name) {
	    	super(directory, name + ".dat.gz");
	    }

	    @SuppressWarnings("unchecked")
		public RowIterator reader(X... columns) throws Exception {
	        String[] names = new String[columns.length];
	        for (int i = 0; i < columns.length; i++) {
	        	names[i] = columns[i].name();
	        }
	        return new RowIterator(Progress.monitor(name, new TSVRowReader(file(), names, true, true, null)));
	    }
	}

	// source files for the ontologies that we index
	public GOSourceFiles goSourceFiles;
	public ECOSourceFiles ecoSourceFiles;

	// Protein information

	// Controlled vocabularies
	public enum EEvidenceCode { ECO_ID, NAME, GO_EVIDENCE, SORT_ORDER }
	public TSVDataFile<EEvidenceCode> evidenceInfo;

	public enum EGORef { NAME, GO_REF }
	public TSVDataFile<EGORef> goRefInfo = new TSVDataFile<>(baseDirectory, "CV_GO_REFS");

	public enum EXrfAbbsEntry { ABBREVIATION, DATABASE, GENERIC_URL, URL_SYNTAX }
	public TSVDataFile<EXrfAbbsEntry> xrfAbbsInfo;

	public enum EProteinSet { NAME, DESCRIPTION, PROJECT_URL }
	public TSVDataFile<EProteinSet> proteinSetsInfo = new TSVDataFile<>(baseDirectory, "CV_PROTEIN_SETS");

	public enum EGOEvidence2ECOTranslation { CODE, GO_REF, ECO_ID }
	public TSVDataFile<EGOEvidence2ECOTranslation> evidence2ECO = new TSVDataFile<>(baseDirectory, "EVIDENCE2ECO");

	public enum EAnnotationBlacklistEntry { PROTEIN_AC, TAXON_ID, GO_ID, REASON, METHOD_ID, CATEGORY, ENTRY_TYPE }
	public TSVDataFile<EAnnotationBlacklistEntry> annotationBlacklist;

	public enum EAnnotationGuidelineEntry { GO_ID, TITLE, URL }
	public TSVDataFile<EAnnotationGuidelineEntry> annotationGuidelines;

	NamedFile[] controlledVocabs =
            holder(evidenceInfo, goRefInfo, xrfAbbsInfo, proteinSetsInfo, evidence2ECO, annotationBlacklist);

	// Controlled vocabularies: derived data
	public enum EGP2ProteinDB { CODE, IS_DB }
	public TSVDataFile<EGP2ProteinDB> gp2proteinDb = new TSVDataFile<>(baseDirectory, "GP2PROTEIN_DB");

	NamedFile[] controlledVocabsDerived = holder(gp2proteinDb);

	// Prerequisite file set
    protected NamedFile[] prerequisite = holder(controlledVocabs);

	// Archive file set
	protected NamedFile[] archive = holder(controlledVocabs, controlledVocabsDerived);

	public NamedFile[] requiredFiles() {
		return prerequisite;
	}

	public NamedFile[] archiveFiles() {
		return archive;
	}


	public NamedFile[] getFiles(NamedFile listFile, final String prefix) {
		List<NamedFile> list = new ArrayList<NamedFile>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(listFile.file()));
			String fileName;
			while ((fileName = reader.readLine()) != null) {
				if (!"".equals(fileName) && (prefix == null || fileName.startsWith(prefix))) {
					list.add(new NamedFile(baseDirectory, fileName));
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return list.toArray(new NamedFile[list.size()]);
	}

	// Download files
	public final static String stampName = "quickgo-stamp-v" + VERSION + ".txt";
}
