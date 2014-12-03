package uk.ac.ebi.quickgo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.ebi.quickgo.ontology.go.AnnotationExtensionRelations;
import uk.ac.ebi.quickgo.web.util.View;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;

import com.google.gson.Gson;

/**
 * To draw Annotation Extension Relations graph
 * @author cbonill
 *
 */
@Controller
public class AnnotationExtensionRelationsController {

	@RequestMapping("/annotationExtensionRelations")
	public String showAnnotationExtensionRelationsGraph(Model model) throws Exception{
		AnnotationExtensionRelations annotationExtensionRelations = new AnnotationExtensionRelations(TermUtil.getGOOntology(), TermUtil.getSourceFiles().goSourceFiles);
		model.addAttribute("data", new Gson().toJson(annotationExtensionRelations.toGraph()));

		return View.STATICPAGES_PATH + "/" + View.ANNOTATIONEXTENSIONRELATIONS;
	}
}