package uk.ac.ebi.quickgo.graphics;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import javax.imageio.ImageIO;

import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTermSet;
import uk.ac.ebi.quickgo.ontology.go.GeneOntology;

/**
 * Tests for OntologyGraph class
 * @author cbonill
 *
 */
public class OntologyGraphTest {

	@Test
	public void makeGraph1(){
				// Create terms
				GOTerm goTermGO_0050790 = new GOTerm("GO:0050790", "GO:0050790", "P", "N");
				GOTerm goTermGO_0019222 = new GOTerm("GO:0019222", "GO:0019222", "P", "N");
				GOTerm goTermGO_0050789 = new GOTerm("GO:0050789", "GO:0050789", "P", "N");
				GOTerm goTermGO_0065007 = new GOTerm("GO:0065007", "GO:0065007", "P", "N");
				
				// Set relations
				TermRelation termRelation1 = new TermRelation(goTermGO_0050790, goTermGO_0019222, RelationType.ISA.code);
				goTermGO_0050790.parents.add(termRelation1);
				goTermGO_0019222.children.add(termRelation1);
				TermRelation termRelation2 = new TermRelation(goTermGO_0019222, goTermGO_0050789, RelationType.ISA.code);
				goTermGO_0019222.parents.add(termRelation2);
				goTermGO_0050789.children.add(termRelation2);
				TermRelation termRelation3 = new TermRelation(goTermGO_0050789, goTermGO_0065007, RelationType.ISA.code);
				goTermGO_0050789.parents.add(termRelation3);
				goTermGO_0065007.children.add(termRelation3);
			
				// Gene Ontology
				GeneOntology geneOntology = new GeneOntology();
				geneOntology.addTerm(goTermGO_0050790);
				geneOntology.addTerm(goTermGO_0019222);
				geneOntology.addTerm(goTermGO_0050789);
				geneOntology.addTerm(goTermGO_0065007);
				
	        	for (String id  : geneOntology.terms.keySet()) {
	        		GenericTerm t = geneOntology.terms.get(id); 
	        		System.out.println("Ancestors of " + t.id);
	        		for (TermRelation ancestor : t.getAncestors()) {
	        			System.out.println("\t" + ancestor);
	        		}
	            }

				// Generic ontology
				GOTermSet goTermSet = new GOTermSet(geneOntology,"go term set",2);
				goTermSet.addTerm(goTermGO_0050790);
				goTermSet.addTerm(goTermGO_0019222);
				goTermSet.addTerm(goTermGO_0050789);
				goTermSet.addTerm(goTermGO_0065007);
				
				// Graph representation
				GraphPresentation graphPresentation = new GraphPresentation();
				
				OntologyGraph ontologyGraph = OntologyGraph.makeGraph(goTermSet, EnumSet.of(RelationType.ISA), 0, 0, graphPresentation);				
				GraphImage graphImage = ontologyGraph.layout();
				
				RenderedImage renderableImage = graphImage.render();				
				
				File outputfile = new File("saved.png");
				try {
					ImageIO.write(renderableImage, "png", outputfile);
				} catch (IOException e) {					
					e.printStackTrace();
				}
	}
}
