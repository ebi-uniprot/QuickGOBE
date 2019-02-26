package uk.ac.ebi.quickgo.model.ontology.go;

import uk.ac.ebi.quickgo.model.ontology.generic.RelationType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotationExtensionRelations {
	public enum ValidationStatus { GOOD, BAD, INDETERMINATE }

	public static class EntityMatcher {
		static Pattern rePattern = Pattern.compile("\\^?([^$]*)\\$?");
		static Matcher reMatcher = rePattern.matcher("");

		Pattern pattern;
		Matcher matcher;

		public String namespace;
		public String idSyntax;
		public String regExp;

		public EntityMatcher(String namespace, String idSyntax) {
			this.namespace = namespace;
			this.idSyntax = idSyntax;

			reMatcher.reset(idSyntax);
			this.regExp = "^" + this.namespace + ":(" + (reMatcher.matches() ? reMatcher.group(1) : idSyntax) + ")$";
			this.pattern = Pattern.compile(this.regExp);
			this.matcher = pattern.matcher("");
		}

		public boolean hasScope(String namespace, String idSyntax) {
			return this.namespace.equals(namespace) && this.idSyntax.equals(idSyntax);
		}
	}

	public static class EntityMatcherSet {
		public List<EntityMatcher> entityMatchers = new ArrayList<>();

		public String compositeRegExp;
		public Pattern compositePattern;

		public EntityMatcher getMatcher(String namespace, String idSyntax) {
			for (EntityMatcher em : entityMatchers) {
				if (em.hasScope(namespace, idSyntax)) {
					return em;
				}
			}

			EntityMatcher em = new EntityMatcher(namespace, idSyntax);
			add(em);
			return em;
		}

		public void add(EntityMatcher em) {
			if (entityMatchers.indexOf(em) < 0) {
				entityMatchers.add(em);

				if (compositeRegExp == null) {
					compositeRegExp = "(" + em.regExp + ")";
				}
				else {
					compositeRegExp += "|(" + em.regExp + ")";
				}

				compositePattern = Pattern.compile(compositeRegExp);
			}
		}

		public boolean matchesComposite(String candidate) {
			return compositePattern != null && compositePattern.matcher(candidate).matches();
		}
	}

	public static class Entity {
		public String name;
		public String type;
		public GOTerm term;
		public EntityMatcherSet matchers = new EntityMatcherSet();

		public Entity(String name, String type, GOTerm term) {
			this.name = name;
			this.type = type;
			this.term = term;
		}

		public boolean isAncestorOf(GOTerm t) {
			return (term != null && t.hasAncestor(term));
		}

		public void addMatcher(EntityMatcher em) {
			matchers.add(em);
		}

		public boolean matches(String candidate) {
			return matchers.matchesComposite(candidate);
		}
	}

	public static class EntitySet {
		public List<Entity> entities = new ArrayList<>();

		public int size() {
			return entities.size();
		}

		public void add(Entity e) {
			if (entities.indexOf(e) < 0) {
				entities.add(e);
			}
		}

		public boolean containsAncestorOf(GOTerm term) {
			if (entities.size() > 0) {
				for (Entity entity : entities) {
					if (entity.isAncestorOf(term)) {
						return true;
					}
				}
				return false;
			}
			else {
				return true;
			}
		}

		public boolean matches(String candidate) {
			for (Entity e : entities) {
				if (e.matches(candidate)) {
					return true;
				}
			}

			return false;
		}

		public List<GOTerm> termList() {
			List<GOTerm> terms = new ArrayList<>();

			for (Entity e : entities) {
				if (e.term != null && terms.indexOf(e.term) < 0) {
					terms.add(e.term);
				}
			}

			return terms;
		}

		public List<EntityMatcher> optionList() {
			Set<EntityMatcher> matchers = new HashSet<>();

			for (Entity e : entities) {
				matchers.addAll(e.matchers.entityMatchers);
			}

			List<EntityMatcher> optionList = new ArrayList<>();
			optionList.addAll(matchers);
			return optionList;
		}
	}

	public static class AnnotationExtensionRelation {
		public String name;
		public String usage;
		public String domain;
		public boolean validInExtension = false;
		public boolean displayForCurators = false;

		public List<AnnotationExtensionRelation> parents = new ArrayList<>();
		public EntitySet domains = new EntitySet();
		public EntitySet ranges = new EntitySet();
		public List<String> secondaries = new ArrayList<>();
		public List<String> subsets = new ArrayList<>();

		public AnnotationExtensionRelation(String name, String usage, String domain) {
			this.name = name;
			this.usage = usage;
			this.domain = domain;
		}

		public void addParent(AnnotationExtensionRelation parent) {
			if (parents.indexOf(parent) < 0) {
				parents.add(parent);
			}
		}

		public void addSecondary(String secondary) {
			if (secondaries.indexOf(secondary) < 0) {
				secondaries.add(secondary);
			}
		}

		public boolean hasParents() {
			return parents.size() > 0;
		}

		public void addDomain(Entity entity) {
			domains.add(entity);
		}

		public boolean hasRange() {
			return ranges.size() > 0;
		}

		public void addRange(Entity entity) {
			ranges.add(entity);
		}

		public void addSubset(String subset) {
			subsets.add(subset);
		}

		public void setValidInExtension() {
			this.validInExtension = true;
		}

		public boolean getValidInExtension() {
			return validInExtension;
		}

		public void setDisplayForCurators() {
			this.displayForCurators = true;
		}

		public boolean getDisplayForCurators() {
			return displayForCurators;
		}

		public ValidationStatus isValidDomain(GOTerm term) {
			if (domains.size() > 0) {
				return domains.containsAncestorOf(term) ? ValidationStatus.GOOD : ValidationStatus.BAD;
			}
			else {
				// this relation has no explicit domain options, so check its parents
				if (hasParents()) {
					for (AnnotationExtensionRelation parent : parents) {
                        ValidationStatus status = parent.isValidDomain(term);
                        if (status != ValidationStatus.INDETERMINATE) {
                            return status;
                        }
					}
					// all parents return an indeterminate status
					return ValidationStatus.INDETERMINATE;
				}
				else {
					// no parents, no explicit domain, so pretty much anything goes...
					return ValidationStatus.INDETERMINATE;
				}
			}
		}

		public ValidationStatus isValidRange(String candidate, GOTerm term) {
			if (ranges.size() > 0) {
				if (term != null) {
					if (ranges.containsAncestorOf(term)) {
						return ValidationStatus.GOOD;
					}
					return ValidationStatus.BAD;
				}
				else {
					return ranges.matches(candidate) ? ValidationStatus.GOOD : ValidationStatus.BAD;
				}
			}
			else {
				// there are no explicit range options defined for this relation, so check against its parents
				if (hasParents()) {
					for (AnnotationExtensionRelation parent : parents) {
                        ValidationStatus status = parent.isValidRange(candidate, term);
                        if (status != ValidationStatus.INDETERMINATE) {
                            return status;
                        }
					}
					// all parents return an indeterminate status
					return ValidationStatus.INDETERMINATE;
				}
				else {
					// no parents, no explict range specified, so we need to check against defaults
					return ValidationStatus.INDETERMINATE;
				}
			}
		}

		public List<GOTerm> domainTerms() {
			return domains.termList();
		}

		public List<EntityMatcher> rangeOptions() {
			return ranges.optionList();
		}
	}

	public static class Relation {
		public AnnotationExtensionRelation child;
		public AnnotationExtensionRelation parent;
		public RelationType typeof;

		public Relation(AnnotationExtensionRelation child, AnnotationExtensionRelation parent, String typeof) {
			this.child = child;
			this.parent = parent;
			this.typeof = RelationType.byCode(typeof);
		}
	}

	public GeneOntology ontology;
	public Map<String, Entity> entities = new HashMap<>();
	public Map<String, AnnotationExtensionRelation> annExtRelations = new LinkedHashMap<>();
	public List<Relation> relations = new ArrayList<>();
	public EntityMatcherSet entityMatchers = new EntityMatcherSet();
	public EntityMatcherSet rangeDefaults = new EntityMatcherSet();

	public Entity getEntity(String id, String type) {
		Entity entity = entities.get(id);
		if (entity == null) {
			entity = new Entity(id, type, (GOTerm)ontology.getTerm(id));
			entities.put(id, entity);
		}
		return entity;
	}

	public AnnotationExtensionRelations(GeneOntology ontology) {
		this.ontology = ontology;
	}

	public static class AnnExtRelException extends Exception {
		private static final long serialVersionUID = 1L;

		public AnnExtRelException(String error) {
			super(error);
		}
	}

	// regExp to decompose a candidate string into relation, namespace and target
	private static Pattern annExtRelPattern = Pattern.compile("^([a-z_]+)\\(((.+):([^\\)]+))\\)$");
	private static Matcher annExtRelMatcher = annExtRelPattern.matcher("");

	public void validate(String go_id, String candidate) throws Exception {
		GOTerm domain = (GOTerm)ontology.getTerm(go_id);
		if (domain != null) {
			for (String sentence : candidate.split("\\|")) {
				for (String phrase : sentence.split(",")) {
					annExtRelMatcher.reset(phrase);
					if (annExtRelMatcher.matches()) {
						String relation = annExtRelMatcher.group(1);
						AnnotationExtensionRelation aer = annExtRelations.get(relation);
						if (aer != null) {
							if (aer.getValidInExtension()) {
								if (aer.isValidDomain(domain) != ValidationStatus.BAD) {
									String range = annExtRelMatcher.group(2);
									GOTerm term = (GOTerm)ontology.getTerm(range);
									ValidationStatus status = aer.isValidRange(range, term);
									if (status == ValidationStatus.BAD || (status == ValidationStatus.INDETERMINATE && !rangeDefaults.matchesComposite(range))) {
										throw new AnnExtRelException("Invalid range for " + relation + ": " + range);
									}
								}
								else {
									throw new AnnExtRelException("Invalid domain for " + relation + ": " + go_id);
								}
							}
							else {
								throw new AnnExtRelException("Relation not valid for use in annotation_extension: " + relation);
							}
						}
						else {
							throw new AnnExtRelException("Unknown/unsupported relation: " + relation);
						}
					}
					else {
						throw new AnnExtRelException("Incorrect syntax: " + phrase);
					}
				}
			}
		}
		else {
			throw new AnnExtRelException("Unknown/invalid domain: " + go_id);
		}
	}

	public static class Node {
		public String id;
		public String usage;
		public String domain;
		public String range;
		public List<String> subsets;

		public Node(AnnotationExtensionRelation aer) {
			this.id = aer.name;
			this.usage = aer.usage;

			StringBuilder sb = new StringBuilder();
			for (GOTerm t : aer.domainTerms()) {
				if (sb.length() > 0) {
					sb.append(" or ");
				}
				sb.append(t.id).append(" (").append(t.name).append(")");
			}
			this.domain = (sb.length() > 0) ? sb.toString() : "None defined";

			if (aer.hasRange()) {
				sb = new StringBuilder();
				for (Entity e : aer.ranges.entities) {
					if (sb.length() > 0) {
						sb.append(" or ");
					}
					sb.append(e.name).append(" (").append(e.type).append(")");
				}
				this.range = (sb.length() > 0) ? sb.toString() : "None defined";
			}
			else {
				this.range = "None defined";
			}

			this.subsets = aer.subsets;
		}
	}

	public static class Edge {
		public String id;
		public String source;
		public String target;
		public String type;

		public Edge(String source, String target) {
			this.id = '[' + source + " -> " + target + ']';
			this.source = source;
			this.target = target;
			this.type = "is_a";
		}
	}

	public Object toGraph() {
		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();

		for (String s : annExtRelations.keySet()) {
			AnnotationExtensionRelation aer = annExtRelations.get(s);
			// we only display relations that are flagged are in the display_for_curators subset in gorel.obo (plus our synthesized root relation)
			if (aer.getDisplayForCurators()) {
				nodes.add(new Node(aer));
				for (AnnotationExtensionRelation parent : aer.parents) {
					edges.add(new Edge(aer.name, parent.name));
				}
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nodes", nodes);
		map.put("edges", edges);
		return map;
	}

	public static class JSONCV implements Comparable<JSONCV> {
		public String code;
		public String idregex;
		public String text;

		public JSONCV(String code, String text, String idregex) {
			this.code = code;
			this.idregex = idregex;
			this.text = text;
		}

		public int compareTo(JSONCV cv) {
			return code.compareTo(cv.code);
		}
	}

	public Map<String, Object> serialise() {
		List<JSONCV> relationNames = new ArrayList<>();

		for (String s : annExtRelations.keySet()) {
			AnnotationExtensionRelation aer = annExtRelations.get(s);
			if (aer.hasParents()) { // exclude relations with no parents (i.e., the root relation)
				relationNames.add(new JSONCV(s, null, null));
			}
		}

		Collections.sort(relationNames);

		List<JSONCV> namespaces = new ArrayList<>();
		for (EntityMatcher em : rangeDefaults.entityMatchers) {
			namespaces.add(new JSONCV(em.namespace, null, em.idSyntax));
		}

		Map<String, Object> map = new HashMap<>();
		map.put("relations", relationNames);
		map.put("namespaces", namespaces);
		return map;
	}

	public static class DBRegExp {
		public String db;
		public String idRegExp;

		public DBRegExp(EntityMatcher em) {
			this.db = em.namespace;
			this.idRegExp = em.idSyntax;
		}
	}

	public static class AER {
		public String name;
		public List<DBRegExp> rangeOptions = new ArrayList<>();

		public AER(AnnotationExtensionRelation aer) {
			this.name = aer.name;

			for (EntityMatcher em : aer.rangeOptions()) {
				this.rangeOptions.add(new DBRegExp(em));
			}
		}
	}

	public static class AERSubset implements Comparable<AERSubset> {
		public String name;
		public List<AER> relations = new ArrayList<>();

		public AERSubset(String name) {
			this.name = name;
		}

		public void addRelation(AER relation) {
			if (relations.indexOf(relation) < 0) {
				relations.add(relation);
			}
		}

		public int compareTo(AERSubset other) {
			return this.name.compareTo(other.name);
		}
	}

	public Map<String, Object> forDomain(String domain) {
		Map<String, Object> map = new HashMap<>();

		GOTerm term = (GOTerm)ontology.getTerm(domain);
		if (term != null) {
			AERSubset allRelations = new AERSubset("(All relations)");
			Map<String, AERSubset> subsets = new HashMap<>();
			subsets.put(allRelations.name, allRelations);

			for (String s : annExtRelations.keySet()) {
				AnnotationExtensionRelation aer = annExtRelations.get(s);
				if (aer.validInExtension && aer.isValidDomain(term) != ValidationStatus.BAD && aer.rangeOptions().size() > 0) {
					AER relation = new AER(aer);
                    if (aer.subsets.size() > 0) {
                        allRelations.addRelation(relation);
                    }
					for (String subsetName : aer.subsets) {
						AERSubset subset = subsets.get(subsetName);
						if (subset == null) {
							subsets.put(subsetName, subset = new AERSubset(subsetName));
						}
						subset.addRelation(relation);
					}
				}
			}

			List<AERSubset> sortedSubsets = new ArrayList<>(subsets.values());
			Collections.sort(sortedSubsets);
			map.put("domain", domain);
			map.put("subsets", sortedSubsets);
		}

		return map;
	}
}
