package uk.ac.ebi.quickgo.web.staticcontent.annotation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.go.AnnotationBlacklist;
import uk.ac.ebi.quickgo.ontology.go.AnnotationBlacklist.BlacklistEntryMinimal;
import uk.ac.ebi.quickgo.ontology.go.AnnotationBlacklistEntry;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;

@Service
public class AnnotationBlackListContent {

	@Autowired
	MiscellaneousService miscellaneousService;
	
	private static AnnotationBlacklist blacklist = new AnnotationBlacklist();

	public List<AnnotationBlacklistEntry> getIEAReview() {
		if (blacklist.blacklistIEAReview() == null
				|| blacklist.blacklistIEAReview().isEmpty()) {
			loadAnnotationBlackList();
		}
		return blacklist.blacklistIEAReview();
	}

	public List<AnnotationBlacklistEntry> getBlackListUniProtCaution() {
		if (blacklist.getSubsets().isEmpty()) {
			loadAnnotationBlackList();
		}
		return blacklist.blacklistUniProtCaution();
	}

	public List<AnnotationBlacklistEntry> getBlackListNotQualified() {
		if (blacklist.getSubsets().isEmpty()) {
			loadAnnotationBlackList();
		}
		return blacklist.blacklistNotQualified();
	}

	public List<BlacklistEntryMinimal> getTaxonBlackList(int taxonId) {
		if (blacklist.getSubsets().isEmpty()) {
			loadAnnotationBlackList();
		}
		return blacklist.forTaxon(taxonId);
	}

	private void loadAnnotationBlackList() {

		List<Miscellaneous> blacklists = miscellaneousService.getBlacklist();

		for (Miscellaneous miscellaneous : blacklists) {
			blacklist.add(miscellaneous.getBacklistCategory(),
					miscellaneous.getDbObjectID(),
					String.valueOf(miscellaneous.getTaxonomyId()),
					miscellaneous.getTerm(), miscellaneous.getBacklistReason(),
					miscellaneous.getBlacklistMethodID());
		}

	}
}