package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Tony Wardell
 * Date: 22/09/2017
 * Time: 12:20
 * Created with IntelliJ IDEA.
 */
public class StatisticsToWorkbookTest {

    List<StatisticsGroup> statisticsGroups;

    @Before
    public void setup(){
        statisticsGroups = new ArrayList<>();
        StatisticsGroup annotationStatisticsGroup = new StatisticsGroup("annotation", 5);
        statisticsGroups.add(annotationStatisticsGroup);
        StatisticsGroup geneProductStatisticsGroup = new StatisticsGroup("geneProduct", 1);
        statisticsGroups.add(geneProductStatisticsGroup);

        //GoId
        StatisticsByType statisticsByAnnotationTypeGoId = new StatisticsByType("goId");
        statisticsByAnnotationTypeGoId.addValue(new StatisticsValue("GO:0003824",1, 5L));
        statisticsByAnnotationTypeGoId.addValue(new StatisticsValue("GO:0003870",1, 5L));
        statisticsByAnnotationTypeGoId.addValue(new StatisticsValue("GO:0009058",1, 5L));
        statisticsByAnnotationTypeGoId.addValue(new StatisticsValue("GO:0030170",1, 5L));
        statisticsByAnnotationTypeGoId.addValue(new StatisticsValue("GO:0033014",1, 5L));
        annotationStatisticsGroup.addStatsType(statisticsByAnnotationTypeGoId);

        StatisticsByType statisticsByGeneProductTypeGoId = new StatisticsByType("goId");
        statisticsByGeneProductTypeGoId.addValue(new StatisticsValue("GO:0003824",1, 1L));
        statisticsByGeneProductTypeGoId.addValue(new StatisticsValue("GO:0003870",1, 1L));
        statisticsByGeneProductTypeGoId.addValue(new StatisticsValue("GO:0009058",1, 1L));
        statisticsByGeneProductTypeGoId.addValue(new StatisticsValue("GO:0030170",1, 1L));
        statisticsByGeneProductTypeGoId.addValue(new StatisticsValue("GO:0033014",1, 1L));
        geneProductStatisticsGroup.addStatsType(statisticsByGeneProductTypeGoId);

        //aspect
        StatisticsByType statisticsByAnnotationTypeAspect = new StatisticsByType("aspect");
        statisticsByAnnotationTypeAspect.addValue(new StatisticsValue("molecular_function",3, 5L));
        statisticsByAnnotationTypeAspect.addValue(new StatisticsValue("biological_process",2, 5L));
        annotationStatisticsGroup.addStatsType(statisticsByAnnotationTypeAspect);

        StatisticsByType statisticsByGeneProductTypeAspect = new StatisticsByType("aspect");
        statisticsByGeneProductTypeAspect.addValue(new StatisticsValue("molecular_function",1, 1L));
        statisticsByGeneProductTypeAspect.addValue(new StatisticsValue("biological_process",1, 1L));
        geneProductStatisticsGroup.addStatsType(statisticsByGeneProductTypeAspect);

        //evidence
        StatisticsByType statisticsByAnnotationTypeEvidenceCode = new StatisticsByType("evidenceCode");
        statisticsByAnnotationTypeEvidenceCode.addValue(new StatisticsValue("ECO:0000256",5, 5L));
        annotationStatisticsGroup.addStatsType(statisticsByAnnotationTypeEvidenceCode);

        StatisticsByType statisticsByGeneProductTypeEvidenceCode = new StatisticsByType("evidenceCode");
        statisticsByGeneProductTypeEvidenceCode.addValue(new StatisticsValue("ECO:0000256",1, 1L));
        geneProductStatisticsGroup.addStatsType(statisticsByGeneProductTypeEvidenceCode);

        //reference
        StatisticsByType statisticsByAnnotationTypeReference = new StatisticsByType("reference");
        statisticsByAnnotationTypeReference.addValue(new StatisticsValue("GO_REF:0000002",5, 5L));
        annotationStatisticsGroup.addStatsType(statisticsByAnnotationTypeReference);

        StatisticsByType statisticsByGeneProductTypeReference = new StatisticsByType("reference");
        statisticsByGeneProductTypeReference.addValue(new StatisticsValue("GO_REF:0000002",1, 1L));
        geneProductStatisticsGroup.addStatsType(statisticsByGeneProductTypeReference);

        //taxonId
        StatisticsByType statisticsByAnnotationTypeTaxon = new StatisticsByType("taxonId");
        statisticsByAnnotationTypeTaxon.addValue(new StatisticsValue("35758",5, 5L));
        annotationStatisticsGroup.addStatsType(statisticsByAnnotationTypeTaxon);

        StatisticsByType statisticsByGeneProductTypeTaxon = new StatisticsByType("taxonId");
        statisticsByGeneProductTypeTaxon.addValue(new StatisticsValue("35758",1, 1L));
        geneProductStatisticsGroup.addStatsType(statisticsByGeneProductTypeTaxon);

        //assignedBy
        StatisticsByType statisticsByAnnotationTypeAssignedBy = new StatisticsByType("assignedBy");
        statisticsByAnnotationTypeAssignedBy.addValue(new StatisticsValue("InterPro",5, 5L));
        annotationStatisticsGroup.addStatsType(statisticsByAnnotationTypeAssignedBy);

        StatisticsByType statisticsByGeneProductTypeAssignedBy = new StatisticsByType("assignedBy");
        statisticsByGeneProductTypeAssignedBy.addValue(new StatisticsValue("InterPro",1, 1L));
        geneProductStatisticsGroup.addStatsType(statisticsByGeneProductTypeAssignedBy);

    }

    @Test
    public void createWorkBook(){

        StatisticsToWorkbook statisticsToWorkbook = new StatisticsToWorkbook(StatisticsWorkBookLayout.SECTION_TYPES,
                                                                             StatisticsWorkBookLayout.SHEET_LAYOUT_MAP);
        Workbook workbook = statisticsToWorkbook.convert(statisticsGroups,50000);
        File outputFile = new File("C:\\Users\\twardell\\someName.xls");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
