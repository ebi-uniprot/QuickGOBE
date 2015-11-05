package uk.ac.ebi.quickgo.webservice.model;

import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;

import java.util.List;

/**
 * Container for the co-occurring stats for a given ontology term
 */
public class CooccuringStatsJson {
    private String termId;
    private List<COOccurrenceStatsTerm> allCoOccurrenceStatsTerms;
    private List<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics;

    public CooccuringStatsJson(String termId,
            List<COOccurrenceStatsTerm> allCoOccurrenceStatsTerms,
            List<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics) {
        this.termId = termId;
        this.allCoOccurrenceStatsTerms = allCoOccurrenceStatsTerms;
        this.nonIEACOOccurrenceStatistics = nonIEACOOccurrenceStatistics;
    }

    public String getTermId() {
        return termId;
    }

    public List<COOccurrenceStatsTerm> getAllCoOccurrenceStatsTerms() {
        return allCoOccurrenceStatsTerms;
    }

    public List<COOccurrenceStatsTerm> getNonIEACOOccurrenceStatistics() {
        return nonIEACOOccurrenceStatistics;
    }
}