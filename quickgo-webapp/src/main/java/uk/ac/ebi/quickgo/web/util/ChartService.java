package uk.ac.ebi.quickgo.web.util;

import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.graphics.GraphImage;
import uk.ac.ebi.quickgo.graphics.GraphPresentation;
import uk.ac.ebi.quickgo.graphics.OntologyGraph;
import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericOntology;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTermSet;
import uk.ac.ebi.quickgo.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;


/**
 * Service to generate charts
 * @author cbonill
 * //TODO Refactor OntologyGraphController to use this class
 */
@Service
public class ChartService {

	GenericOntology genericOntology;

	GraphImage graphImage;

	RenderedImage renderableImage;

	String termsToDisplay;

	private static final EnumSet ECO_SET = EnumSet.of(RelationType.USEDIN, RelationType.ISA);

	private static final EnumSet GO_SET = EnumSet.of(RelationType.ISA, RelationType.PARTOF,
			RelationType.REGULATES, RelationType.POSITIVEREGULATES, RelationType.NEGATIVEREGULATES,
			RelationType.OCCURSIN, RelationType.CAPABLEOF, RelationType.CAPABLEOFPARTOF);

	public void createChart(String ids, String scope) {
		genericOntology = null;
		graphImage = null;
		renderableImage = null;
		termsToDisplay = "";
		EnumSet targetSet = GO_SET;

		boolean isGO = true;
		if (scope != null && scope.trim().length() > 0) {
			if(scope.equalsIgnoreCase(ECOTerm.ECO)){
				targetSet = ECO_SET;
				isGO = false;
			}
		}

		List<String> termsIdsList = new ArrayList<>();
		termsIdsList = Arrays.asList(ids.split(","));

		List<String> formattedTermsIdsList = new ArrayList<>();
		for(String id : termsIdsList){
			if ((isGO && id.matches(AnnotationParameters.GO_ID_REG_EXP
					+ "\\d{7}"))
					|| (!isGO && id.matches(AnnotationParameters.ECO_ID_REG_EXP
							+ "\\d{7}"))) {// Valid GO id
				formattedTermsIdsList.add(id);
			}
		}

		termsIdsList = formattedTermsIdsList;
		if(termsIdsList.size() > 0){
			// Get corresponding ontology
			genericOntology = TermUtil.getOntology(termsIdsList.get(0));

			// Create graph image
			graphImage = createRenderableImage(ids, targetSet);
			renderableImage = graphImage.render();

			if(termsIdsList.size() == 1){//Just one term, set id as the graph title
				termsToDisplay = "Ancestor chart for " + termsIdsList.get(0);
			} else {
				termsToDisplay = "Comparison chart for " + String.valueOf(termsIdsList.size());
			}
		}
	}

	/**
	 * Generates ancestors graph for a list of terms
	 * @param termsIds List of terms to calculate the graph for
	 * @return Graph image
	 */
	private GraphImage createRenderableImage(String termsIds, EnumSet targetSet){
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
		OntologyGraph ontologyGraph = OntologyGraph.makeGraph(termSet, targetSet, 0, 0, new GraphPresentation());
		return ontologyGraph.layout();
	}

	public GenericOntology getGenericOntology() {
		return genericOntology;
	}


	public void setGenericOntology(GenericOntology genericOntology) {
		this.genericOntology = genericOntology;
	}

	public GraphImage getGraphImage() {
		return graphImage;
	}

	public void setGraphImage(GraphImage graphImage) {
		this.graphImage = graphImage;
	}

	public RenderedImage getRenderableImage() {
		return renderableImage;
	}

	public void setRenderableImage(RenderedImage renderableImage) {
		this.renderableImage = renderableImage;
	}

	public String getTermsToDisplay() {
		return termsToDisplay;
	}

	public void setTermsToDisplay(String termsToDisplay) {
		this.termsToDisplay = termsToDisplay;
	}
}
