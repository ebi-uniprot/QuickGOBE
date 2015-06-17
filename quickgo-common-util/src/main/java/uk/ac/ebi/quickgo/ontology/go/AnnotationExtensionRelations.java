package uk.ac.ebi.quickgo.ontology.go;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ebi.quickgo.data.GOSourceFiles;
import uk.ac.ebi.quickgo.data.GOSourceFiles.*;
import uk.ac.ebi.quickgo.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.render.JSONSerialise;

public class AnnotationExtensionRelations implements JSONSerialise {
	public enum ValidationStatus { GOOD, BAD, INDETERMINATE }

	public static final String rootRelation = "_ROOT_AER_";

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

		public boolean matches(String candidate) {
			matcher.reset(candidate);
			return matcher.matches();
		}

		public boolean matches(String namespace, String target) {
			return matches(namespace + ":" + target);
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

		public boolean matches(String candidate) {
			for (EntityMatcher em : entityMatchers) {
				if (em.matches(candidate)) {
					return true;
				}
			}
			return false;
		}

		public boolean matches(String namespace, String target) {
			return matches(namespace + ":" + target);
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

		public boolean matches(String namespace, String target) {
			return matches(namespace + ":" + target);
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

		public List<String> entityTypeList() {
			List<String> entityTypes = new ArrayList<>();

			for (Entity e : entities) {
				if (e.type != null && !("".equals(e.type))) {
					if (entityTypes.indexOf(e.type) < 0) {
						entityTypes.add(e.type);
					}
				}
			}

			return entityTypes;
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

		public boolean hasDomain() {
			return domains.size() > 0;
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

		public ValidationStatus isValidDomain(GOTerm term) {
			if (domains.size() > 0) {
				return domains.containsAncestorOf(term) ? ValidationStatus.GOOD : ValidationStatus.BAD;
			}
			else {
				// this relation has no explicit domain options, so check its parents
				if (parents.size() > 0) {
					for (AnnotationExtensionRelation parent : parents) {
						switch (parent.isValidDomain(term)) {
							case GOOD:
								return ValidationStatus.GOOD;
							case BAD:
								return ValidationStatus.BAD;
							case INDETERMINATE:
								break;
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
				if (parents.size() > 0) {
					for (AnnotationExtensionRelation parent : parents) {
						switch (parent.isValidRange(candidate, term)) {
							case GOOD:
								return ValidationStatus.GOOD;
							case BAD:
								return ValidationStatus.BAD;
							case INDETERMINATE:
								break;
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

		public String getName() {
			return name;
		}

		public String getUsage() {
			return usage;
		}

		public String getDomain() {
			return domain;
		}

		public List<String> getParentsNames() {
			List<String> parentsNames = new ArrayList<>();
			for(AnnotationExtensionRelation annotationExtensionRelation : parents){
				parentsNames.add(annotationExtensionRelation.getName());
			}
			return parentsNames;
		}

		public List<AnnotationExtensionRelation> getParents() {
			return parents;
		}

		public EntitySet getDomains() {
			return domains;
		}

		public EntitySet getRanges() {
			return ranges;
		}

		public List<String> getSecondaries() {
			return secondaries;
		}

		public List<String> getSubsets() {
			return subsets;
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

	public AnnotationExtensionRelations(GeneOntology ontology, GOSourceFiles sourceFiles) throws Exception {
		this.ontology = ontology;

		for (String[] row : sourceFiles.annExtRelations.reader(EAnnExtRelation.RELATION, EAnnExtRelation.USAGE, EAnnExtRelation.DOMAIN)) {
		    annExtRelations.put(row[0], new AnnotationExtensionRelation(row[0], row[1], row[2]));
		}

		for (String[] row : sourceFiles.aerRelations.reader(EAnnExtRelRelation.CHILD, EAnnExtRelRelation.PARENT, EAnnExtRelRelation.RELATION_TYPE)) {
			AnnotationExtensionRelation child = annExtRelations.get(row[0]);
			AnnotationExtensionRelation parent = annExtRelations.get(row[1]);
			if (child != null && parent != null) {
				relations.add(new Relation(child, parent, row[2]));
				child.addParent(parent);
			}
		}

		for (String[] row : sourceFiles.aerSecondaries.reader(EAnnExtRelSecondary.RELATION, EAnnExtRelSecondary.SECONDARY_ID)) {
			AnnotationExtensionRelation rel = annExtRelations.get(row[0]);
			if (rel != null) {
				rel.addSecondary(row[1]);
			}
		}

		for (String[] row : sourceFiles.aerSubsets.reader(EAnnExtRelSubset.SUBSET, EAnnExtRelSubset.RELATION)) {
			AnnotationExtensionRelation rel = annExtRelations.get(row[1]);
			if (rel != null) {
				rel.addSubset(row[0]);
			}
		}

		for (String[] row : sourceFiles.aerDomains.reader(EAnnExtRelDomain.RELATION, EAnnExtRelDomain.ENTITY, EAnnExtRelDomain.ENTITY_TYPE)) {
			AnnotationExtensionRelation relation = annExtRelations.get(row[0]);
			if (relation != null && row[1] != null) {
				relation.addDomain(getEntity(row[1], row[2]));
			}
		}

		for (String[] row : sourceFiles.aerEntitySyntax.reader(EAnnExtRelEntitySyntax.ENTITY, EAnnExtRelEntitySyntax.ENTITY_TYPE, EAnnExtRelEntitySyntax.NAMESPACE, EAnnExtRelEntitySyntax.ID_SYNTAX)) {
			Entity entity = getEntity(row[0], row[1]);
			entity.addMatcher(entityMatchers.getMatcher(row[2], row[3]));
		}


		for (String[] row : sourceFiles.aerRanges.reader(EAnnExtRelRange.RELATION, EAnnExtRelRange.ENTITY, EAnnExtRelRange.ENTITY_TYPE)) {
			AnnotationExtensionRelation relation = annExtRelations.get(row[0]);
			if (relation != null && row[1] != null) {
				relation.addRange(getEntity(row[1], row[2]));
			}
		}

		for (String[] row : sourceFiles.aerRangeDefaults.reader(EAnnExtRelRangeDefault.NAMESPACE, EAnnExtRelRangeDefault.ID_SYNTAX)) {
			rangeDefaults.add(entityMatchers.getMatcher(row[0], row[1]));
		}
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
				sb.append(t.getId()).append(" (").append(t.getName()).append(")");
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

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUsage() {
			return usage;
		}

		public void setUsage(String usage) {
			this.usage = usage;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getRange() {
			return range;
		}

		public void setRange(String range) {
			this.range = range;
		}

		public List<String> getSubsets() {
			return subsets;
		}

		public void setSubsets(List<String> subsets) {
			this.subsets = subsets;
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
            // we only display relations that are in one or more subsets (plus the root relation)
            if (aer.subsets.size() > 0 || rootRelation.equals(aer.name)) {
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
			if (aer.parents.size() > 0) { // exclude relations with no parents (i.e., the root relation)
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
				if (aer.parents.size() > 0 && aer.subsets.size() > 0 && aer.rangeOptions().size() > 0 && aer.isValidDomain(term)  != ValidationStatus.BAD) {
					AER relation = new AER(aer);
					allRelations.addRelation(relation);
					for (String subsetName : aer.subsets) {
						if (!"all_relations".equals(subsetName)) {
							AERSubset subset = subsets.get(subsetName);
							if (subset == null) {
								subsets.put(subsetName, subset = new AERSubset(subsetName));
							}
							subset.addRelation(relation);
						}
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

	public Map<String, AnnotationExtensionRelation> getAnnExtRelations() {
		return annExtRelations;
	}
}
