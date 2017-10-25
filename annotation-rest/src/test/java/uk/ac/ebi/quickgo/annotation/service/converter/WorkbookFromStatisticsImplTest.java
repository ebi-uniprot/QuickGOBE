package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;

import java.util.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.service.converter.StatisticsWorkBookLayout.SECTION_TYPES;

/**
 * Test the creation of a worksheet for a known data set.
 * @author Tony Wardell
 * Date: 22/09/2017
 * Time: 12:20
 * Created with IntelliJ IDEA.
 */
public class WorkbookFromStatisticsImplTest {
    private List<StatisticsGroup> statisticsGroups;
    private static final Map<String, WorkbookFromStatisticsImpl.SheetLayout> SHEET_LAYOUT_MAP = new HashMap<>();
    private static final StatisticsWorkBookLayout.AnnotationSectionLayout SL_ANNOTATION_GOID =
            new StatisticsWorkBookLayout.AnnotationSectionLayout("GO IDs (by annotation)");
    private static final StatisticsWorkBookLayout.AnnotationSectionLayout SL_ANNOTATION_ASPECT =
            new StatisticsWorkBookLayout.AnnotationSectionLayout("Aspects (by annotation)");

    private static final StatisticsWorkBookLayout.GeneProductSectionLayout SL_GENE_PRODUCT_GOID =
            new StatisticsWorkBookLayout.GeneProductSectionLayout("GO IDs (by protein)");
    private static final StatisticsWorkBookLayout.GeneProductSectionLayout SL_GENE_PRODUCT_ASPECT =
            new StatisticsWorkBookLayout.GeneProductSectionLayout("Aspects (by protein)");
    static {
        SHEET_LAYOUT_MAP.put("TEST_GO_ID",
                new WorkbookFromStatisticsImpl.SheetLayout("goid",
                        Arrays.asList(SL_ANNOTATION_GOID,
                                SL_GENE_PRODUCT_GOID)));

        SHEET_LAYOUT_MAP.put("aspect",
                new WorkbookFromStatisticsImpl.SheetLayout("aspect",
                        Arrays.asList(SL_ANNOTATION_ASPECT,
                                SL_GENE_PRODUCT_ASPECT)));
    }


    @Before
    public void setup(){
        buildStatisticGroups();
    }

    @Test
    public void workbookMatchesInputData(){
        WorkbookFromStatisticsImpl statisticsToWorkbook = new WorkbookFromStatisticsImpl(SECTION_TYPES, SHEET_LAYOUT_MAP);

        Workbook workbook = statisticsToWorkbook.generate(statisticsGroups);

        assertThat(workbook.getNumberOfSheets(), is(3));
        assertThat(workbook.getSheetAt(0).getSheetName(), is("summary"));
        assertThat(workbook.getSheetAt(0).getPhysicalNumberOfRows(), is(2));

        assertThat(workbook.getSheetAt(1).getSheetName(), is("goid"));
        assertThat(workbook.getSheetAt(1).getPhysicalNumberOfRows(), is(5));

        testColumnHeaders(workbook, 0);
        testColumnHeaders(workbook, 10);

        // test goid sheet contents
        //by annotation
        assertThat(workbook.getSheetAt(1).getRow(3).getCell(0).getStringCellValue(),is("GO:0003824"));
        assertThat(workbook.getSheetAt(1).getRow(3).getCell(2).getNumericCellValue(),is(20.00d));
        assertThat(workbook.getSheetAt(1).getRow(3).getCell(3).getNumericCellValue(),is(1d));
        assertThat(workbook.getSheetAt(1).getRow(5).getCell(0).getStringCellValue(),is("GO:0009058"));
        assertThat(workbook.getSheetAt(1).getRow(5).getCell(2).getNumericCellValue(),is(20.00d));
        assertThat(workbook.getSheetAt(1).getRow(5).getCell(3).getNumericCellValue(),is(1d));

        //by gene product
        assertThat(workbook.getSheetAt(1).getRow(5).getCell(10).getStringCellValue(),is("GO:0009058"));
        assertThat(workbook.getSheetAt(1).getRow(5).getCell(12).getNumericCellValue(),is(100.00d));
        assertThat(workbook.getSheetAt(1).getRow(5).getCell(13).getNumericCellValue(),is(1d));

        //Check some details for the aspect sheet.
        assertThat(workbook.getSheetAt(2).getSheetName(), is("aspect"));
        assertThat(workbook.getSheetAt(2).getPhysicalNumberOfRows(), is(4));
    }


    @Test (expected = IllegalArgumentException.class)
    public void creatingStatisticsToWorkbookWithNullSectionTypesThrowsException() {
        new WorkbookFromStatisticsImpl(null, SHEET_LAYOUT_MAP);
    }

    @Test (expected = IllegalArgumentException.class)
    public void creatingStatisticsToWorkbookWithNullLayoutMapThrowsException() {
        new WorkbookFromStatisticsImpl(SECTION_TYPES, null);
    }

    private void testColumnHeaders(Workbook workbook, int startingCol) {
        assertThat(workbook.getSheetAt(1).getRow(2).getCell(startingCol).getStringCellValue(), is("Code"));
        assertThat(workbook.getSheetAt(1).getRow(2).getCell(++startingCol).getStringCellValue(), is("Name"));
        assertThat(workbook.getSheetAt(1).getRow(2).getCell(++startingCol).getStringCellValue(), is("Percentage"));
        assertThat(workbook.getSheetAt(1).getRow(2).getCell(++startingCol).getStringCellValue(), is("Count"));
    }

    private void buildStatisticGroups() {
        statisticsGroups = new ArrayList<>();
        StatisticsGroup annotationStatisticsGroup = new StatisticsGroup("annotation", 5);
        statisticsGroups.add(annotationStatisticsGroup);
        StatisticsGroup geneProductStatisticsGroup = new StatisticsGroup("geneProduct", 1);
        statisticsGroups.add(geneProductStatisticsGroup);

        //GoId
        StatisticsByType statisticsByAnnotationTypeGoId = new StatisticsByType("TEST_GO_ID");
        statisticsByAnnotationTypeGoId.addValue(new StatisticsValue("GO:0003824", 1, 5L));
        statisticsByAnnotationTypeGoId.addValue(new StatisticsValue("GO:0003870",1, 5L));
        statisticsByAnnotationTypeGoId.addValue(new StatisticsValue("GO:0009058",1, 5L));
        annotationStatisticsGroup.addStatsType(statisticsByAnnotationTypeGoId);

        StatisticsByType statisticsByGeneProductTypeGoId = new StatisticsByType("TEST_GO_ID");
        statisticsByGeneProductTypeGoId.addValue(new StatisticsValue("GO:0003824",1, 1L));
        statisticsByGeneProductTypeGoId.addValue(new StatisticsValue("GO:0003870",1, 1L));
        statisticsByGeneProductTypeGoId.addValue(new StatisticsValue("GO:0009058",1, 1L));
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
    }
}
