/**
 *
 */
package uk.ac.ebi.quickgo.model.ontology.go;

import uk.ac.ebi.quickgo.model.ontology.generic.AuditRecord;
import uk.ac.ebi.quickgo.model.ontology.generic.TermOntologyHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that represents a term in the Gene Ontology
 *
 * @author tonys
 */
public class GOTerm externalnds GenericTerm {
    public GOTerm() {
        super();
    }

    public static final String GO = "GO";

    public static class ProteinComplex {
        public String db;
        public String id;
        public String symbol;
        public String name;

        public ProteinComplex(String db, String id, String symbol, String name) {
            this.db = db;
            this.id = id;
            this.symbol = symbol;
            this.name = name;
        }

        @Override
        public String toString() {
            return "ProteinComplex{" +
                    "db='" + db + '\'' +
                    ", id='" + id + '\'' +
                    ", symbol='" + symbol + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class NamedURL {
        public String title;
        public String url;

        public NamedURL(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public enum EGOAspect {
        P("Process", "Biological Process", "P", "biological_process"),
        F("Function", "Molecular Function", "F", "molecular_function"),
        C("Component", "Cellular Component", "C", "cellular_component"),
        R("Root", "Root", "R", "gene_ontology");

        public String text;
        public String description;
        public String abbreviation;
        public String namespace;

        EGOAspect(String text, String description, String abbreviation, String namespace) {
            this.text = text;
            this.description = description;
            this.abbreviation = abbreviation;
            this.namespace = namespace;
        }

        public static EGOAspect fromString(String s) throws Exception {
            if ("Process".equalsIgnoreCase(s)) {
                return P;
            } else if ("Function".equalsIgnoreCase(s)) {
                return F;
            } else if ("Component".equalsIgnoreCase(s)) {
                return C;
            } else {
                throw new Exception("Invalid ontology: " + s);
            }
        }
    }

    public enum ETermUsage {
        U("U", "Unrestricted", "This term may be used for any kind of annotation."),
        E("E", "Electronic",
                "This term should not be used for direct manual annotation. This term may be used for mapping to " +
                        "external vocabularies in order to create electronic annotations."),
        X("X", "None", "This term should not be used for direct annotation.");

        public String code;
        public String text;
        public String description;

        ETermUsage(String code, String text, String description) {
            this.code = code;
            this.text = text;
            this.description = description;
        }

        public static ETermUsage fromString(String s) throws Exception {
            if (U.text.equalsIgnoreCase(s)) {
                return U;
            } else if (E.text.equalsIgnoreCase(s)) {
                return E;
            } else if (X.text.equalsIgnoreCase(s)) {
                return X;
            } else {
                throw new Exception("Invalid usage: " + s);
            }
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    public int code;

    public List<ProteinComplex> proteinComplexes = new ArrayList<>();
    public List<TaxonConstraint> taxonConstraints = new ArrayList<>();
    public List<NamedURL> guidelines = new ArrayList<>();
    public List<NamedURL> plannedChanges = new ArrayList<>();
    public EGOAspect aspect;
    public ETermUsage usage = ETermUsage.X;
    public List<GOTermBlacklist> blacklist = new ArrayList<>();

    public GOTerm(String id, String name, String aspect, String isObsolete) {
        super(id, name, isObsolete);

        this.aspect = EGOAspect.valueOf(aspect);
        this.code = Integer.parseInt(id.substring(3));
        this.usage = ETermUsage.U;
    }

    public void associateProteinComplex(String db, String id, String symbol, String name) {
        proteinComplexes.add(new ProteinComplex(db, id, symbol, name));
    }

    public void addTaxonConstraint(TaxonConstraint constraint) {
        taxonConstraints.add(constraint);
    }

    public void addHistoryRecord(AuditRecord ar) {
        history.add(ar);
    }

    public void addGuideline(String title, String url) {
        guidelines.add(new NamedURL(title, url));
    }

    public void addPlannedChange(String title, String url) {
        plannedChanges.add(new NamedURL(title, url));
    }

    public void addBlacklist(String goId, String category, String entityType, String entityID, String taxonID, String entityName,
                             String ancestorGoID, String reason, String methodID ){
        blacklist.add(new GOTermBlacklist(goId, category, entityType, entityID, taxonID, entityName, ancestorGoID, reason, methodID));
    }

    public List<GOTermBlacklist> getBlacklist() {
        return blacklist;
    }

    public String ontology() {
        return aspect.description;
    }

    public String getOntologyText() {
        if (aspect != null) {
            return aspect.text;
        }
        return "";
    }

    public String getNamespace() {
        if (aspect != null) {
            return aspect.namespace;
        }
        return "";
    }

    public static class MinimalGOTermInfo extends MinimalTermInfo {
        public String usage;

        public MinimalGOTermInfo(String id, String name, String usage) {
            super(id, name);
            this.usage = usage;
        }

        public MinimalGOTermInfo(String id) {
            super(id);
        }
    }

    @Override
    public MinimalTermInfo getMinimalTermInfo() {
        //return new MinimalGOTermInfo(id, name, usage.code);
        return new MinimalGOTermInfo(id);
    }

    // implementation of JSONSerialise interface
    @Override
    public Map<String, Object> serialise() {
        Map<String, Object> superMap = super.serialise();
        superMap.put("aspect", aspect != null ? aspect.text : null);
        superMap.put("usage", getUsageText());
        return superMap;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ProteinComplex> getProteinComplexes() {
        return proteinComplexes;
    }

    public void setProteinComplexes(List<ProteinComplex> proteinComplexes) {
        this.proteinComplexes = proteinComplexes;
    }

    public List<TaxonConstraint> getTaxonConstraints() {
        return taxonConstraints;
    }

    public void setTaxonConstraints(List<TaxonConstraint> taxonConstraints) {
        this.taxonConstraints = taxonConstraints;
    }

    public TermOntologyHistory getHistory() {
        return history;
    }

    public void setHistory(TermOntologyHistory history) {
        this.history = history;
    }

    public List<NamedURL> getGuidelines() {
        return guidelines;
    }

    public void setGuidelines(List<NamedURL> guidelines) {
        this.guidelines = guidelines;
    }

    public List<NamedURL> getPlannedChanges() {
        return plannedChanges;
    }

    public void setPlannedChanges(List<NamedURL> plannedChanges) {
        this.plannedChanges = plannedChanges;
    }

    public EGOAspect getAspect() {
        return aspect;
    }

    public String getAspectDescription() {
        if (aspect != null) {
            return aspect.description;
        } else {
            return "";
        }
    }

    public void setAspect(EGOAspect aspect) {
        this.aspect = aspect;
    }

    public String getGonutsURL() {
        return "http://gowiki.tamu.edu/wiki/index.php/Category:" + id;
    }

    public static String getGo() {
        return GO;
    }

    public ETermUsage getUsage() {
        return usage;
    }

    public String getUsageText() {
        if (usage != null) {
            return usage.text;
        }
        return "";
    }

    public void setUsage(ETermUsage usage) {
        this.usage = usage;
    }

    @Override
    public void addQCCheck(String s) {
        switch (s) {
            case "gocheck_do_not_annotate":
                this.usage = ETermUsage.X;
                break;

            case "gocheck_do_not_manually_annotate":
                this.usage = ETermUsage.E;
                break;

            default:
                break;
        }
    }
}
