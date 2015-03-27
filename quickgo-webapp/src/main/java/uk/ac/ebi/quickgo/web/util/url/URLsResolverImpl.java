package uk.ac.ebi.quickgo.web.util.url;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.bean.annotation.AnnotationBean;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.util.NamedXRef;
import uk.ac.ebi.quickgo.util.miscellaneous.MiscellaneousUtil;
import uk.ac.ebi.quickgo.web.util.NameURL;
import uk.ac.ebi.quickgo.web.util.term.XRefBean;

/**
 * Useful class to retrieve external databases URLS and set values
 * @author cbonill
 *
 */
@Service
public class URLsResolverImpl implements URLsResolver{

	private static String EXAMPLE_ID = "[example_id]";

	@Autowired
	MiscellaneousUtil miscellaneousUtil;

	/**
	 * Set databases URL for a given annotation
	 * @param annotationBean Annotation
	 */
	public void setURLs(AnnotationBean annotationBean) {
		// DB
		Miscellaneous xrefInformation = miscellaneousUtil.getDBInformation(annotationBean.getAnnotation().getDb());
		annotationBean.setDb(new NameURL(xrefInformation.getXrefAbbreviation(), xrefInformation.getXrefGenericURL()));

		// DB Object ID
		String elementURL = xrefInformation.getXrefUrlSyntax().replace(EXAMPLE_ID, annotationBean.getAnnotation().getDbObjectID());
		annotationBean.setDbObjectID(new NameURL(annotationBean.getAnnotation().getDbObjectID(), elementURL));

		// Reference
		xrefInformation = miscellaneousUtil.getDBInformation(annotationBean.getAnnotation().getReference().split(":")[0]);//Index 0 contains database ID
		elementURL = xrefInformation.getXrefUrlSyntax().replace(EXAMPLE_ID, annotationBean.getAnnotation().getReference().split(":")[1]);//Index 1 contains database element
		annotationBean.setReferences(new NameURL(annotationBean.getAnnotation().getReference(), elementURL));

		// Assigned by
		xrefInformation = miscellaneousUtil.getDBInformation(annotationBean.getAnnotation().getAssignedBy());
		annotationBean.setAssignedBy(new NameURL(annotationBean.getAnnotation().getAssignedBy(), xrefInformation.getXrefGenericURL()));

		//With
		List<NameURL> nameURLs = new ArrayList<>();
		List<String> withs = annotationBean.getAnnotation().getWith();
		String separator = "|";
		if (withs != null) {
			for (int i=0; i< withs.size(); i++) {
				if (i == (withs.size() - 1)) {// Remove separator last item
					separator = "";
				}
				xrefInformation = miscellaneousUtil.getDBInformation(withs.get(i).split(":", 2)[0]);
				if(xrefInformation.getXrefUrlSyntax() != null){
					String id = withs.get(i).split(":", 2)[1];
					elementURL = xrefInformation.getXrefUrlSyntax().replace(EXAMPLE_ID, id);// id contains the database element
					nameURLs.add(new NameURL(withs.get(i) + separator, elementURL));
				}
			}
		}
		annotationBean.setWith(nameURLs);
	}

	/**
	 * Calculate generic URL for a list of cross-references
	 * @param xRefs Cross-references
	 * @return Cross-references generic URLs
	 */
	public List<XRefBean> calculateXrefsUrls(List<NamedXRef> xRefs){
		List<XRefBean> refBeans = new ArrayList<>();
		for(NamedXRef xRef : xRefs){
			Miscellaneous xrefInformation = miscellaneousUtil.getDBInformation(xRef.getDb());
			String name = (xRef.getName() == null || xRef.getName().isEmpty())? xRef.getId() : xRef.getName();
			String url = xrefInformation.getXrefGenericURL();
			if (xrefInformation.getXrefUrlSyntax() != null) {
				url = xrefInformation.getXrefUrlSyntax().replace(EXAMPLE_ID, xRef.getId());
			}
			XRefBean xRefBean =	new XRefBean(xRef.getDb(), xRef.getId(), new NameURL(name, url));

			refBeans.add(xRefBean);
		}
		return refBeans;
	}
}
