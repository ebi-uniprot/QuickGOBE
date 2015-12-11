package uk.ac.ebi.quickgo.model.ontology.go;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaxonConstraint implements Comparable<TaxonConstraint> {
    public enum Relationship {
        ONLY_IN_TAXON("only_in_taxon"),
        NEVER_IN_TAXON("never_in_taxon");

        public String text;

        Relationship(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public enum TaxIdType {
        TAXON("NCBITaxon"),
        TAXON_UNION("NCBITaxon_Union");

        public String text;

        TaxIdType(String text) {
            this.text = text;
        }
    }

    public static class PMID {
        public String pmid;

        public PMID(String pmid) {
            this.pmid = pmid;
        }

        public String getUrl() {
            if (pmid.length() >= 5) {
                return "http://europepmc.org/abstract/MED/" + pmid.substring(5);
            }
            return "";
        }

        public String getPmid() {
            return pmid;
        }

        public void setPmid(String pmid) {
            this.pmid = pmid;
        }
    }

    public String rule_id;
    public String go_id;
    public String name;
    Relationship relationship;
    TaxIdType taxon_id_type;
    public String taxon_id;
    public String taxon_name;
    public List<PMID> sources = new ArrayList<>();

    public String relationship() {
        return relationship.text;
    }

    public String taxIdType() {
        return taxon_id_type.text;
    }

    public TaxonConstraint(String ruleId, String goId, String name, String relationship, String taxIdType, String taxId,
            String taxonName, String sources) {
        this.rule_id = ruleId;
        this.go_id = goId;
        this.name = name;
        this.relationship =
                "never_in_taxon".equals(relationship) ? Relationship.NEVER_IN_TAXON : Relationship.ONLY_IN_TAXON;
        this.taxon_id_type = "NCBITaxon_Union".equals(taxIdType) ? TaxIdType.TAXON_UNION : TaxIdType.TAXON;
        this.taxon_id = taxId;
        this.taxon_name = taxonName;

        if (sources != null) {
            for (String pmid : sources.split(",")) {
                this.sources.add(new PMID(pmid));
            }
        }
    }

    public Map<String, Object> serialise() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rule_id", rule_id);
        map.put("go_id", go_id);
        map.put("constraint", relationship.text);
        if (taxon_id_type == TaxIdType.TAXON_UNION) {
            map.put("taxa", taxon_id.split("[^0-9]+"));
        } else {
            map.put("taxa", new String[]{taxon_id});
        }
        //map.put("sources", sources);

        return map;
    }

    /**
     * Get PubMed ids
     */
    public List<String> getSourcesIds() {
        List<String> ids = new ArrayList<>();
        for (PMID pmid : this.sources) {
            ids.add(pmid.pmid);
        }
        return ids;
    }

    @Override
    public String toString() {
        return "TaxonConstraint{" +
                "ruleId=" + rule_id +
                ", goId='" + go_id + '\'' +
                ", name='" + name + '\'' +
                ", relationship=" + relationship +
                ", taxIdType=" + taxon_id_type +
                ", taxId=" + taxon_id +
                ", taxonName='" + taxon_name + '\'' +
                ", sources='" + sources + '\'' +
                '}';
    }

    public String getRuleId() {
        return rule_id;
    }

    public void setRuleId(String ruleId) {
        this.rule_id = ruleId;
    }

    public String getGoId() {
        return go_id;
    }

    public void setGoId(String goId) {
        this.go_id = goId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public TaxIdType getTaxIdType() {
        return taxon_id_type;
    }

    public void setTaxIdType(TaxIdType taxIdType) {
        this.taxon_id_type = taxIdType;
    }

    public String getTaxId() {
        return taxon_id;
    }

    public void setTaxId(String taxId) {
        this.taxon_id = taxId;
    }

    public String getTaxonName() {
        return taxon_name;
    }

    public void setTaxonName(String taxonName) {
        this.taxon_name = taxonName;
    }

    public List<PMID> getSources() {
        return sources;
    }

    public void setSources(List<PMID> sources) {
        this.sources = sources;
    }

    public String getSourcesString() {
        return sources.stream().map(PMID::getPmid).collect(Collectors.joining(","));
    }

    @Override
    public int compareTo(TaxonConstraint o) {
        return this.getRuleId().compareTo(o.getRuleId());
    }
}