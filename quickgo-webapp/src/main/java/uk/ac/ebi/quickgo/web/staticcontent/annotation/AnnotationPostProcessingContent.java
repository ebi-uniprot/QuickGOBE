package uk.ac.ebi.quickgo.web.staticcontent.annotation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.go.PostProcessingRule;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;

@Service
public class AnnotationPostProcessingContent {

	@Autowired
	MiscellaneousService miscellaneousService;
	
	//static SourceFiles sourceFiles = null;	
	private static List<PostProcessingRule> postProcessingRules = new ArrayList<PostProcessingRule>();

	public List<PostProcessingRule> getPostProcessingRules() {

		if(postProcessingRules.isEmpty()){
			List<Miscellaneous> ppRules = miscellaneousService.getPostProcessingRules();
			for(Miscellaneous miscellaneous : ppRules){
				postProcessingRules.add(new PostProcessingRule(miscellaneous
						.getPprRuleId(), miscellaneous.getPprAncestorGoId(),
						miscellaneous.getPprAncestorTerm(), miscellaneous
								.getPprRelationship(), miscellaneous.getPprTaxonName(),
						miscellaneous.getPprOriginalGoId(), miscellaneous
								.getPprOriginalTerm(), miscellaneous.getPprCleanupAction(),
						miscellaneous.getPprAffectedTaxGroup(), miscellaneous
								.getPprSubstitutedGoId(), miscellaneous.getPprSubstitutedTerm(),
						miscellaneous.getPprCuratorNotes()));
			}		
		}
		return postProcessingRules;
	}
}
