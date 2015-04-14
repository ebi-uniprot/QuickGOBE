package uk.ac.ebi.quickgo.controller;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.ebi.quickgo.graphics.GraphImage;
import uk.ac.ebi.quickgo.graphics.GraphPresentation;
import uk.ac.ebi.quickgo.graphics.ImageArchive;
import uk.ac.ebi.quickgo.graphics.OntologyGraph;
import uk.ac.ebi.quickgo.ontology.generic.GenericOntology;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTermSet;
import uk.ac.ebi.quickgo.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.web.util.FileService;
import uk.ac.ebi.quickgo.web.util.SlimmingUtil;
import uk.ac.ebi.quickgo.web.util.View;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;

/**
 * Class responsible for generating ontology graphs
 * @author cbonill
 *
 */

@Controller
public class OntologyGraphController {

	@Autowired
	FileService fileService;

	// Log
	private static final Logger logger = Logger.getLogger(OntologyGraphController.class);

	// Ontology
	GenericOntology genericOntology;

	// Basket terms
	private Map<String, String> basketTerms = new HashMap<String, String>();

	@RequestMapping(value = { "/","annotation"}, method = {RequestMethod.POST, RequestMethod.GET }, params = { "graphTermsIds", "relations" })
	public String generateAnnotationOntologyGraph(
			HttpSession session,
			@RequestParam(value = "graphTermsIds", required = true) String termsIds,
			@RequestParam(value = "relations",  required = false, defaultValue="ISA") String relations,
			HttpServletRequest request,
			Model model){

		List<String> termsIdsList = new ArrayList<>();
		if(termsIds.equals("allBasketTerms")){
			basketTerms = (Map<String, String>)session.getAttribute("basketTerms");
			Object[] ids = basketTerms.keySet().toArray();
			for (Object object : ids) {
				termsIdsList.add(object != null ? object.toString() : null);
			}
			termsIds = StringUtils.arrayToDelimitedString(termsIdsList.toArray(), ",");
		} else if (termsIds.equals("slimming")){
				Map<String, String> activeSlimmingGraphTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.ACTIVE_SLIMMING_GRAPH_TERMS_ATTRIBUTE, session);
				termsIdsList = new ArrayList<>(activeSlimmingGraphTerms.keySet());
				if(termsIdsList.isEmpty()){// No term selected
					model.addAttribute("graphImageSrc", null);
					return redirect(request);
				}
				termsIds = StringUtils.arrayToDelimitedString(termsIdsList.toArray(), ",");
		} else {
			termsIdsList = Arrays.asList(termsIds.split(","));
		}
		// Get corresponding ontology
		genericOntology = TermUtil.getOntology(termsIdsList.get(0));

		// Create graph image
		GraphImage graphImage = createRenderableImage(termsIds);
		String src = ImageArchive.store(graphImage);
		RenderedImage renderableImage = graphImage.render();

		if(termsIdsList.size() == 1){//Just one term, set id as the graph title
			String id = termsIdsList.get(0);
			String name = genericOntology.getTerm(id).getName();
			String title = id;
			if (id.contains(GOTerm.GO)) {// Add name for GO terms
				title = title + " " + name;
			}
			model.addAttribute("termGraphTitle", title);
		}

		model.addAttribute("termsNodes", graphImage.getOntologyTerms());
		model.addAttribute("legendNodes", graphImage.legend);
		model.addAttribute("graphImageSrc", src);
		model.addAttribute("graphImageWidth", renderableImage.getWidth());
		model.addAttribute("graphImageHeight", renderableImage.getHeight());

		return redirect(request);
	}

	/**
	 * Generate ancestors graph for a given term
	 * @param termsIds Term id
	 * @param relations Relations to show
	 * @param model Model
	 * @return Term page
	 */
	public String generateTermOntologyGraph(
			String termsIds,
			String relations,
			Model model){

		List<String> termsIdsList = new ArrayList<>();
		termsIdsList = Arrays.asList(termsIds.split(","));

		// Get corresponding ontology
		genericOntology = TermUtil.getOntology(termsIdsList.get(0));

		// Create graph image
		GraphImage graphImage = createRenderableImage(termsIds);
		String src = ImageArchive.store(graphImage);
		RenderedImage renderableImage = graphImage.render();

		String id = termsIdsList.get(0);
		String name = genericOntology.getTerm(id).getName();
		String title = id;
		title = title + " " + name;
		model.addAttribute("termTermGraphTitle", title);

		model.addAttribute("termTermsNodes", graphImage.getOntologyTerms());
		model.addAttribute("termLegendNodes", graphImage.legend);
		model.addAttribute("termGraphImageSrc", src);
		model.addAttribute("termGraphImageWidth", renderableImage.getWidth());
		model.addAttribute("termGraphImageHeight", renderableImage.getHeight());

		return View.TERMS_PATH + "/" + View.TERM;
	}

	/**
	 * To retrieve graph images
	 * @param id Image id
	 * @return Graph image in base 64
	 */
	@RequestMapping(value = { "/graphs",}, method = {RequestMethod.POST, RequestMethod.GET }, params = { "id"})
	@ResponseBody
	public String getGraphImage(@RequestParam(value = "id", required = true) int id){
		String base64Image = "";
		try {
			RenderedImage image = ImageArchive.getContent().get(id).render();

			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bas);

			byte[] byteArray=bas.toByteArray();
			base64Image = new String(Base64.encodeBase64(byteArray));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return base64Image;
	}

	/**
	 * To retrieve graph images
	 * @param id Image id
	 * @return Graph image in base 64
	 */
	@RequestMapping(value = { "/jsongraphs",}, method = { RequestMethod.GET }, params = { "id"})
	@ResponseBody
	public void getJsonGraphImage(HttpServletResponse httpServletResponse,
								  @RequestParam(value = "id", required = true) int id){
		String base64Image = "";
		try {
			RenderedImage image = ImageArchive.getContent().get(id).render();

			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bas);

			byte[] byteArray=bas.toByteArray();
			base64Image = new String(Base64.encodeBase64(byteArray));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		//return base64Image;
		StringBuffer sb = new StringBuffer();
		sb.append("data:image/png;base64, ");
		try {
			fileService.generateJsonFile(base64Image,sb);

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}


	/**
	 * Generates ancestors graph for a list of terms
	 * @param termsIds List of terms to calculate the graph for
	 * @return Graph image
	 */
	public GraphImage createRenderableImage(String termsIds){
		// Check if the selected terms exist
		List<GenericTerm> terms = new ArrayList<GenericTerm>();
		List<String> termsIdsList = Arrays.asList(termsIds.split(","));
		for(String id : termsIdsList){
			GenericTerm term = genericOntology.getTerm(id);
			if(term != null){
				terms.add(term);
			}
		}

		// Build GO term set
		GenericTermSet termSet = new GenericTermSet(genericOntology, "Term Set", 0);
		for(GenericTerm term : terms){
			termSet.addTerm(term);
		}

		// Create ontology graph
		OntologyGraph ontologyGraph = OntologyGraph.makeGraph(termSet, EnumSet.of(RelationType.USEDIN, RelationType.ISA, RelationType.PARTOF, RelationType.REGULATES, /*RelationType.HASPART,*/ RelationType.OCCURSIN), 0, 0, new GraphPresentation());
		return ontologyGraph.layout();
	}

	private String redirect(HttpServletRequest request){
		if (request.getRequestURI().contains(View.TERM)) {// Request from term page
			return View.TERMS_PATH + "/" + View.TERM;
		} else if (request.getRequestURI().contains(View.ANNOTATIONS_PATH)) {
			return View.ANNOTATIONS_PATH + "/" + View.ANNOTATIONS_LIST;
		} else {
			return View.INDEX;
		}
	}

	private void writeOutJsonResponse(HttpServletResponse httpServletResponse, StringBuffer sb) throws IOException {
		InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		IOUtils.copy(in, httpServletResponse.getOutputStream());

		// Set response header and content
		httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + "json");
		httpServletResponse.setContentLength(sb.length());
		httpServletResponse.flushBuffer();
	}
}
