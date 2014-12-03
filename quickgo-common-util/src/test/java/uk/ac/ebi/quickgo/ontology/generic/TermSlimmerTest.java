package uk.ac.ebi.quickgo.ontology.generic;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GeneOntology;

/**
 * Tests for the TermSlimmer class 
 * @author cbonill
 *
 */
public class TermSlimmerTest {

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void nullOntology() throws Exception {
		final ITermContainer container = context.mock(ITermContainer.class);
		final EnumSet<RelationType> relationTypes = context.mock(EnumSet.class);

		context.checking(new Expectations() {
			{
				allowing(container).getTermCount();
				will(returnValue(0));
			}
		});

		TermSlimmer termSlimmer = new TermSlimmer(null, container, relationTypes);
		assertNull(termSlimmer.getSlimTranslate());
	}

	@Test
	public void nullItemContainer() throws Exception {
		final GenericOntology genericOntology = context.mock(GenericOntology.class);
		final EnumSet<RelationType> relationTypes = context.mock(EnumSet.class);

		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, null, relationTypes);
		assertNull(termSlimmer.getSlimTranslate());
	}
	
	@Test(expected = Exception.class)
	public void differentNamespaces() throws Exception {
		final GenericOntology genericOntology = context.mock(GenericOntology.class);
		final ITermContainer container = context.mock(ITermContainer.class);

		context.checking(new Expectations() {
			{
				allowing(container).getTermCount();
				will(returnValue(1));
				
				allowing(genericOntology).getNamespace();
				will(returnValue("namespace1"));
				
				allowing(container).getNamespace();
				will(returnValue("namespace2"));
			}
		});
		
		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, container, null);
		assertNull(termSlimmer.getSlimTranslate());
	}
	
	@Test(expected = Exception.class)
	public void sameNamespaces() throws Exception {
		final GenericOntology genericOntology = context.mock(GenericOntology.class);
		final ITermContainer container = context.mock(ITermContainer.class);

		context.checking(new Expectations() {
			{
				allowing(container).getTermCount();
				will(returnValue(1));
				
				allowing(genericOntology).getNamespace();
				will(returnValue("namespace1"));
				
				allowing(container).getNamespace();
				will(returnValue("namespace1"));
				
				allowing(container).toArray();
				will(returnValue(new GenericTerm()));
			}
		});
		
		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, container, null);
		assertNull(termSlimmer.getSlimTranslate());
	}
	
	/**
	 * Using GO:0050790 ancestors chart as example. Slimming up to GO:0019222 using IS_A relation
	 * @throws Exception
	 */
	@Test
	public void testSlimGO_0050790ToGO_0019222() throws Exception{		
		// Create terms
		GOTerm goTermGO_0050790 = new GOTerm("GO:0050790","name","P","false");
		GOTerm goTermGO_0019222 = new GOTerm("GO:0019222","name","P","false");
		
		// Set relations
		TermRelation termRelation = new TermRelation(goTermGO_0050790, goTermGO_0019222, RelationType.ISA.code);
		List<TermRelation> GO_0050790relations = Arrays.asList(termRelation);		
		goTermGO_0050790.setAncestors(GO_0050790relations);
		
		// Generic ontology
		GeneOntology genericOntology = new GeneOntology();
		genericOntology.addTerm(goTermGO_0050790);
		genericOntology.addTerm(goTermGO_0019222);
		
		// Slim terms		
		GeneOntology slimTerms = new GeneOntology();
		slimTerms.addTerm(goTermGO_0019222);		
		
		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, slimTerms, EnumSet.of(RelationType.ISA));
		Map<String, List<GenericTerm>> slimTranslate = termSlimmer.getSlimTranslate();
		assertTrue(slimTranslate.get("GO:0050790").get(0).getId().equals("GO:0019222"));
	} 
	
	/**
	 * Using GO:0050790 ancestors chart as example. Slimming up to GO:0019222 and GO:0050790 using REGULATES relation
	 * @throws Exception
	 */
	@Test
	public void testSlimGO_0050790ToGO_0019222Regulates() throws Exception{		
		// Create terms
		GOTerm goTermGO_0050790 = new GOTerm("GO:0050790","name","P","false");
		GOTerm goTermGO_0019222 = new GOTerm("GO:0019222","name","P","false");
		
		// Set relations
		TermRelation termRelation = new TermRelation(goTermGO_0050790, goTermGO_0019222, RelationType.ISA.code);
		List<TermRelation> GO_0050790relations = Arrays.asList(termRelation);		
		goTermGO_0050790.setAncestors(GO_0050790relations);
		
		// Generic ontology
		GeneOntology genericOntology = new GeneOntology();
		genericOntology.addTerm(goTermGO_0050790);
		genericOntology.addTerm(goTermGO_0019222);
		
		// Slim terms		
		GeneOntology slimTerms = new GeneOntology();
		slimTerms.addTerm(goTermGO_0050790);
		slimTerms.addTerm(goTermGO_0019222);		
		
		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, slimTerms, EnumSet.of(RelationType.REGULATES));
		Map<String, List<GenericTerm>> slimTranslate = termSlimmer.getSlimTranslate();
		assertTrue(slimTranslate.size() == 2);
		assertTrue(slimTranslate.get("GO:0050790").get(0).getId().equals("GO:0050790"));
		assertTrue(slimTranslate.get("GO:0019222").get(0).getId().equals("GO:0019222"));
	}	
	
	/**
	 * Using GO:0050790 ancestors chart as example. Slimming up to GO:0019222 and GO:0003824 using REGULATES relation
	 * @throws Exception
	 */
	@Test
	public void testSlimGO_0050790ToGO_0019222GO_0003824() throws Exception{		
		// Create terms
		GOTerm goTermGO_0050790 = new GOTerm("GO:0050790","name","P","false");
		GOTerm goTermGO_0019222 = new GOTerm("GO:0019222","name","P","false");
		GOTerm goTermGO_0003824 = new GOTerm("GO:0003824","name","P","false");
		 
		// Set relations
		TermRelation isa_termRelation = new TermRelation(goTermGO_0050790, goTermGO_0019222, RelationType.ISA.code);
		TermRelation regulates_termRelation = new TermRelation(goTermGO_0050790, goTermGO_0003824, RelationType.REGULATES.code);
		List<TermRelation> GO_0050790relations = Arrays.asList(isa_termRelation,regulates_termRelation);		
		goTermGO_0050790.setAncestors(GO_0050790relations);
		
		// Generic ontology
		GeneOntology genericOntology = new GeneOntology();
		genericOntology.addTerm(goTermGO_0050790);
		genericOntology.addTerm(goTermGO_0019222);
		genericOntology.addTerm(goTermGO_0003824);		
		
		// Slim terms		
		GeneOntology slimTerms = new GeneOntology();
		slimTerms.addTerm(goTermGO_0019222);
		slimTerms.addTerm(goTermGO_0003824);	
		
		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, slimTerms, EnumSet.of(RelationType.REGULATES));
		Map<String, List<GenericTerm>> slimTranslate = termSlimmer.getSlimTranslate();
		assertTrue(slimTranslate.get("GO:0050790").get(0).getId().equals("GO:0003824"));
	} 
	
	/**
	 * Using GO:0050790 ancestors chart as example. Slimming up to GO:0019222 and GO:0065009 using IS_A relation
	 * @throws Exception
	 */
	@Test
	public void testSlimGO_0050790ToGO_0019222GO_0065009() throws Exception{		
		// Create terms
		GOTerm goTermGO_0050790 = new GOTerm("GO:0050790","name","P","false");
		GOTerm goTermGO_0019222 = new GOTerm("GO:0019222","name","P","false");
		GOTerm goTermGO_0065009 = new GOTerm("GO:0065009","name","P","false");
		
		// Set relations
		TermRelation termRelation1 = new TermRelation(goTermGO_0050790, goTermGO_0019222, RelationType.ISA.code);
		TermRelation termRelation2 = new TermRelation(goTermGO_0050790, goTermGO_0065009, RelationType.ISA.code);
		List<TermRelation> GO_0050790relations = Arrays.asList(termRelation1,termRelation2);		
		goTermGO_0050790.setAncestors(GO_0050790relations);
		
		// Generic ontology
		GeneOntology genericOntology = new GeneOntology();
		genericOntology.addTerm(goTermGO_0050790);
		genericOntology.addTerm(goTermGO_0019222);
		genericOntology.addTerm(goTermGO_0065009);
		
		// Slim terms		
		GeneOntology slimTerms = new GeneOntology();
		slimTerms.addTerm(goTermGO_0019222);		
		slimTerms.addTerm(goTermGO_0065009);		
		
		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, slimTerms, EnumSet.of(RelationType.ISA));
		Map<String, List<GenericTerm>> slimTranslate = termSlimmer.getSlimTranslate();
		assertTrue(slimTranslate.get("GO:0050790").size() == 2);
		assertTrue(slimTranslate.get("GO:0050790").toString().contains("GO:0019222"));
		assertTrue(slimTranslate.get("GO:0050790").toString().contains("GO:0065009"));
	}
	
	/**
	 * Using GO:0050790 ancestors chart as example. Slimming up to GO:0019222 and GO:0050789 using IS_A relation
	 * @throws Exception
	 */
	@Test
	public void testSlimGO_0050790ToGO_0019222GO_0050789() throws Exception{		
		// Create terms
		GOTerm goTermGO_0050790 = new GOTerm("GO:0050790","name","P","false");
		GOTerm goTermGO_0019222 = new GOTerm("GO:0019222","name","P","false");
		GOTerm goTermGO_0050789 = new GOTerm("GO:0050789","name","P","false");
		
		// Set relations
		TermRelation termRelation1 = new TermRelation(goTermGO_0050790, goTermGO_0019222, RelationType.ISA.code);
		TermRelation termRelation2 = new TermRelation(goTermGO_0019222, goTermGO_0050789, RelationType.ISA.code);
		List<TermRelation> GO_0050790relations = Arrays.asList(termRelation1);		
		goTermGO_0050790.setAncestors(GO_0050790relations);
		List<TermRelation> GO_0019222relations = Arrays.asList(termRelation2);		
		goTermGO_0019222.setAncestors(GO_0019222relations);
		
		// Generic ontology
		GeneOntology genericOntology = new GeneOntology();
		genericOntology.addTerm(goTermGO_0050790);
		genericOntology.addTerm(goTermGO_0019222);
		genericOntology.addTerm(goTermGO_0050789);
		
		// Slim terms		
		GeneOntology slimTerms = new GeneOntology();
		slimTerms.addTerm(goTermGO_0019222);		
		slimTerms.addTerm(goTermGO_0050789);		
		
		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, slimTerms, EnumSet.of(RelationType.ISA));
		Map<String, List<GenericTerm>> slimTranslate = termSlimmer.getSlimTranslate();
		assertTrue(slimTranslate.get("GO:0050790").size() == 1);
		assertTrue(slimTranslate.get("GO:0050790").toString().contains("GO:0019222"));		
	}
	
	/**
	 * Using GO:0050790 ancestors chart as example. Slimming up to GO:0019222, GO:0050789 and GO:0065007 using IS_A relation
	 * @throws Exception
	 */
	@Test
	public void testSlimGO_0050790ToGO_0019222GO_0050789GO_0065007() throws Exception{		
		// Create terms
		GOTerm goTermGO_0050790 = new GOTerm("GO:0050790","name","P","false");
		GOTerm goTermGO_0019222 = new GOTerm("GO:0019222","name","P","false");
		GOTerm goTermGO_0050789 = new GOTerm("GO:0050789","name","P","false");
		GOTerm goTermGO_0065007 = new GOTerm("GO:0065007","name","P","false");
		
		// Set relations
		TermRelation termRelation1 = new TermRelation(goTermGO_0050790, goTermGO_0019222, RelationType.ISA.code);
		TermRelation termRelation2 = new TermRelation(goTermGO_0019222, goTermGO_0050789, RelationType.ISA.code);
		TermRelation termRelation3 = new TermRelation(goTermGO_0050789, goTermGO_0065007, RelationType.ISA.code);
		List<TermRelation> GO_0050790relations = Arrays.asList(termRelation1);		
		goTermGO_0050790.setAncestors(GO_0050790relations);
		List<TermRelation> GO_0019222relations = Arrays.asList(termRelation2);		
		goTermGO_0019222.setAncestors(GO_0019222relations);
		List<TermRelation> goTermGO_0050789relations = Arrays.asList(termRelation3);		
		goTermGO_0050789.setAncestors(goTermGO_0050789relations);
		
		// Generic ontology
		GeneOntology genericOntology = new GeneOntology();
		genericOntology.addTerm(goTermGO_0050790);
		genericOntology.addTerm(goTermGO_0019222);
		genericOntology.addTerm(goTermGO_0050789);
		genericOntology.addTerm(goTermGO_0065007);
		
		// Slim terms		
		GeneOntology slimTerms = new GeneOntology();
		slimTerms.addTerm(goTermGO_0019222);		
		slimTerms.addTerm(goTermGO_0050789);
		slimTerms.addTerm(goTermGO_0065007);		
		
		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, slimTerms, EnumSet.of(RelationType.ISA));
		Map<String, List<GenericTerm>> slimTranslate = termSlimmer.getSlimTranslate();						
		assertTrue(slimTranslate.get("GO:0050790").get(0).getId().equalsIgnoreCase("GO:0019222"));
	}
	
	/**
	 * Using GO:0050790 ancestors chart as example. Slimming up to GO:0019222,GO:0050789,GO:0065007 and GO:0065009 using IS_A relation
	 * @throws Exception
	 */
	@Test
	public void testSlimGO_0050790ToGO_0019222GO_0050789GO_0065007GO_0065009() throws Exception{		
		// Create terms
		GOTerm goTermGO_0050790 = new GOTerm("GO:0050790","name","P","false");
		GOTerm goTermGO_0019222 = new GOTerm("GO:0019222","name","P","false");
		GOTerm goTermGO_0050789 = new GOTerm("GO:0050789","name","P","false");
		GOTerm goTermGO_0065007 = new GOTerm("GO:0065007","name","P","false");
		GOTerm goTermGO_0065009 = new GOTerm("GO:0065009","name","P","false");

		// Set relations
		TermRelation termRelation1 = new TermRelation(goTermGO_0050790, goTermGO_0019222, RelationType.ISA.code);
		TermRelation termRelation2 = new TermRelation(goTermGO_0019222, goTermGO_0050789, RelationType.ISA.code);
		TermRelation termRelation3 = new TermRelation(goTermGO_0050789, goTermGO_0065007, RelationType.ISA.code);
		TermRelation termRelation4 = new TermRelation(goTermGO_0050790, goTermGO_0065009, RelationType.ISA.code);
		List<TermRelation> GO_0050790relations = Arrays.asList(termRelation1, termRelation4);		
		goTermGO_0050790.setAncestors(GO_0050790relations);
		List<TermRelation> GO_0019222relations = Arrays.asList(termRelation2);		
		goTermGO_0019222.setAncestors(GO_0019222relations);
		List<TermRelation> goTermGO_0050789relations = Arrays.asList(termRelation3);		
		goTermGO_0050789.setAncestors(goTermGO_0050789relations);
		
		// Generic ontology
		GeneOntology genericOntology = new GeneOntology();
		genericOntology.addTerm(goTermGO_0050790);
		genericOntology.addTerm(goTermGO_0019222);
		genericOntology.addTerm(goTermGO_0050789);
		genericOntology.addTerm(goTermGO_0065007);
		genericOntology.addTerm(goTermGO_0065009);
		
		// Slim terms		
		GeneOntology slimTerms = new GeneOntology();
		slimTerms.addTerm(goTermGO_0019222);		
		slimTerms.addTerm(goTermGO_0050789);
		slimTerms.addTerm(goTermGO_0065007);	
		slimTerms.addTerm(goTermGO_0065009);
		
		TermSlimmer termSlimmer = new TermSlimmer(genericOntology, slimTerms, EnumSet.of(RelationType.ISA));
		Map<String, List<GenericTerm>> slimTranslate = termSlimmer.getSlimTranslate();						
		assertTrue(slimTranslate.get("GO:0050790").size() == 2); 
		assertTrue(slimTranslate.get("GO:0050790").toString().contains("GO:0019222"));
		assertTrue(slimTranslate.get("GO:0050790").toString().contains("GO:0065009"));		
	}
}