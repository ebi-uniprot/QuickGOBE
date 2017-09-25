package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

/**
 * @author Tony Wardell
 * Date: 22/09/2017
 * Time: 11:11
 * Created with IntelliJ IDEA.
 */
public class StatisticsToWorkbook {
    static Map<String, SheetLayout> sheetLayoutMap = new HashMap<>();

    static {
        sheetLayoutMap.put("goId", new SheetLayout("goid", "GO IDs (by annotation)", 0, "GO IDs (by protein)", 10));
        //                   new SheetLayout("aspect",)
        //                   new SheetLayout("evidence",)
        //                   new SheetLayout("reference",)
        //                   new SheetLayout("taxon",)
        //                   new SheetLayout("assigned")};

    }
    //    static SheetLayout[] SHEETS = new String[] {

    public Workbook convert(List<StatisticsGroup> statisticsGroups) {

        Workbook wb = new HSSFWorkbook();
        CreationHelper helper = wb.getCreationHelper();

        CellStyle fixedDecimalPlaces = wb.createCellStyle();
        fixedDecimalPlaces.setDataFormat(wb.createDataFormat().getFormat("0.00"));

        Sheet summarySheet = wb.createSheet("summary");
        populateSummarySheet(summarySheet, statisticsGroups);
        populateDetailSheets(wb, statisticsGroups.get(0), statisticsGroups.get(1), fixedDecimalPlaces);
        return wb;
    }

    private void populateDetailSheets(Workbook wb, StatisticsGroup statisticsGroupByAnnotation,
            StatisticsGroup statisticsGroupByProtein, CellStyle fixedDecimalPlaces) {

        List<StatisticsByType> statisticsByAnnotation = statisticsGroupByAnnotation.getTypes();
        List<StatisticsByType> statisticsByProtein = statisticsGroupByProtein.getTypes();

        for (int i = 0; i < statisticsByAnnotation.size(); i++) {

            StatisticsByType statisticsByAnnotationForType = statisticsByAnnotation.get(i);
            StatisticsByType statisticsByProteinForType =
                    matchingStatisticsByProteinForType(statisticsByProtein, statisticsByAnnotationForType);

            //Find sheet to populate
            final SheetLayout sheetLayout = sheetLayoutMap.get(statisticsByAnnotationForType.getType());
            if (sheetLayout == null) {
                continue;
            }
            Sheet sheet = wb.getSheet(sheetLayout.displayName);
            if (sheet == null) {
                sheet = wb.createSheet(sheetLayout.displayName);
            }

            //Populate Sheet
            int rowCounter = 1;

            //Headers
            Row headerRow = sheet.createRow(rowCounter);
            headerRow.createCell(sheetLayout.headerACol).setCellValue(sheetLayout.headerA);
            headerRow.createCell(sheetLayout.headerBCol).setCellValue(sheetLayout.headerB);

            //Column header Row
            Row colNamesRow = sheet.createRow(++rowCounter);
            populateHeader(sheetLayout.headerACol, colNamesRow);
            populateHeader(sheetLayout.headerBCol, colNamesRow);

            //Detail Rows
            final List<StatisticsValue> valuesByAnnotation = statisticsByAnnotationForType.getValues();
            final List<StatisticsValue> valuesByProtein = statisticsByProteinForType.getValues();

            for (int j = 0; j < valuesByAnnotation.size(); j++) {
                Row detailRow = sheet.createRow(++rowCounter);
                populateSection(sheetLayout.headerACol, valuesByAnnotation.get(j), detailRow, fixedDecimalPlaces);
                populateSection(sheetLayout.headerBCol, valuesByProtein.get(j), detailRow, fixedDecimalPlaces);
            }
        }
    }

    private StatisticsByType matchingStatisticsByProteinForType(List<StatisticsByType> statisticsByProtein,
            StatisticsByType statisticsByAnnotationForType) {
        //Get the matching StatisticsByType from the second group
        for (StatisticsByType aStatisticsByProtein : statisticsByProtein) {
            if (statisticsByAnnotationForType.getType().equals(aStatisticsByProtein.getType())) {
                return aStatisticsByProtein;
            }
        }
        throw new IllegalStateException("All types should match");
    }

    private void populateHeader(int startCounter, Row colNamesRow) {
        int colCounter = startCounter;
        for (int i = 0; i < SectionHeaderLayout.SECTION_COL_HEADINGS.length; i++) {
            colNamesRow.createCell(colCounter++).setCellValue(SectionHeaderLayout.SECTION_COL_HEADINGS[i]);
        }
    }

    private void populateSection(int startingCounter, StatisticsValue statisticsValue, Row detailRow,
            CellStyle fixedDecimalPlaces) {
        int colCounter = startingCounter;
        detailRow.createCell(colCounter).setCellValue(statisticsValue.getKey());
        populatePercentageCell(detailRow.createCell(++colCounter), statisticsValue, fixedDecimalPlaces);
        detailRow.createCell(++colCounter).setCellValue(statisticsValue.getHits());
    }

    private void populatePercentageCell(Cell targetCell, StatisticsValue statisticsValue, CellStyle cellStyle) {
        targetCell.setCellValue(statisticsValue.getPercentage() * 100);
        targetCell.setCellType(CellType.NUMERIC);
        targetCell.setCellStyle(cellStyle);
    }

    private void populateSummarySheet(Sheet sheet, List<StatisticsGroup> statisticsGroups) {

        for (int i = 0; i < statisticsGroups.size(); i++) {
            StatisticsGroup statisticsGroup = statisticsGroups.get(i);
            if (statisticsGroup.getGroupName().equals("annotation")) {
                Row row1 = sheet.createRow(1);
                row1.createCell(0).setCellValue("Summary");
                Row row2 = sheet.createRow(2);
                row2.createCell(0).setCellValue("Number of annotations:" + statisticsGroup.getTotalHits());
            }
        }
    }

    static class SheetLayout {
        String displayName;
        String headerA;
        int headerACol;
        String headerB;
        int headerBCol;

        public SheetLayout(String displayName, String headerA, int headerACol, String headerB, int headerBCol) {
            this.displayName = displayName;
            this.headerA = headerA;
            this.headerACol = headerACol;
            this.headerB = headerB;
            this.headerBCol = headerBCol;

        }
    }

    static class SectionHeaderLayout {
        private static final String[] SECTION_COL_HEADINGS = new String[]{"Code", "Percentage", "Count"};
        int startingColumn;

        public SectionHeaderLayout(int startingColumn) {
            this.startingColumn = startingColumn;
        }
    }

}
