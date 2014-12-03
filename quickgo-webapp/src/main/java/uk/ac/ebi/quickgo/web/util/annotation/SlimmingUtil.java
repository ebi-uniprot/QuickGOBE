package uk.ac.ebi.quickgo.web.util.annotation;

import java.util.List;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.bean.annotation.AnnotationBean;

/**
 * Useful class for slimming functionality
 * @author cbonill
 *
 */
public interface SlimmingUtil {

	/**
	 * Given an annotation and a list of terms to slim up to, return the
	 * corresponding annotation with the original/term ids and names
	 * @param annotation Annotation to analyze
	 * @param filterIds Ids to slim up to
	 * @param slimValue If slimming is enabled or not
	 * @return
	 */
	public AnnotationBean calculateOriginalAndSlimmingTerm(Annotation annotation, List<String> filterIds, List<String> slimValue);

}
