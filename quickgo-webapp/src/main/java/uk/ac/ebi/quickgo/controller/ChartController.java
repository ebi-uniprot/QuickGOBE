package uk.ac.ebi.quickgo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.quickgo.graphics.ImageArchive;
import uk.ac.ebi.quickgo.web.util.ChartService;
import uk.ac.ebi.quickgo.web.util.View;

import com.google.gson.Gson;

/**
 * To generate terms charts
 * @author cbonill
 *
 */
@Controller
public class ChartController {

	@Autowired
	ChartService chartService;	
	
	@RequestMapping("/chart")
	public String generateChart(
			@RequestParam(value = "ids", required = true, defaultValue = "") String ids,
			@RequestParam(value = "scope", required = false, defaultValue = "") String scope,
			Model model){
		
		chartService.createChart(ids,scope);
		
		String src = ImageArchive.store(chartService.getGraphImage());

		model.addAttribute("termsNodes", chartService.getGraphImage().getOntologyTerms());
		model.addAttribute("legendNodes", chartService.getGraphImage().legend);
		model.addAttribute("graphImageSrc", src);		
		model.addAttribute("graphImageWidth", chartService.getRenderableImage().getWidth());
		model.addAttribute("graphImageHeight", chartService.getRenderableImage().getHeight());
		model.addAttribute("termstodisplay", chartService.getTermsToDisplay());
		return View.STATICPAGES_PATH +  "/" + View.CHART;
	}
	
	@ExceptionHandler(Exception.class)
	public String handleIOException(Exception ex) {
		Map<String, String> errors = new HashMap<String, String>();
	    errors.put("error_message", "The request cannot be fulfilled due to bad syntax");
		return new Gson().toJson(errors);
	}
}