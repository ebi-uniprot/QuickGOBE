package uk.ac.ebi.quickgo.web.util.annotation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.bean.annotation.AnnotationBean;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;

/**
 * Implementation of {@link SlimmingUtil}
 * @author cbonilla
 *
 */
@Service
public class SlimmingUtilImpl implements SlimmingUtil{

	@Autowired
	TermService termService;
	
	/**
	 * See {@link SlimmingUtil#calculateOriginalAndSlimmingTerm(Annotation, List, List)}
	 */
	public AnnotationBean calculateOriginalAndSlimmingTerm(GOAnnotation annotation, List<String> filterGOIds, List<String> slimValue){
		AnnotationBean annotationBean = new AnnotationBean(annotation);
		annotationBean.setTermIDSlimmingToString(annotation.getGoID());
		annotationBean.setTermNameSlimmingTo(annotation.getTermName());
		if(filterGOIds != null && filterGOIds.size() > 0)
			if (slimValue != null && slimValue.size()>0 && slimValue.get(0).equals("true")) {
				List<String> ancestorsIPO = new ArrayList<>(annotation.getAncestorsIPO());
				ancestorsIPO.retainAll(filterGOIds);
				String goIDSlimmingTo = ancestorsIPO.get(0);//.get(0);
				annotationBean.setTermIDSlimmingToString(goIDSlimmingTo);
				annotationBean.setTermNameSlimmingTo(termService.retrieveTerm(goIDSlimmingTo).getName());
			}
		return annotationBean;
	}
}
