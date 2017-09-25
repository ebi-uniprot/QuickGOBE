package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

/**
 * Populate an Excel Workbook instance with statistics data (a list of {@link StatisticsGroup} instances).
 * Provide a sheet for each {@link StatisticsByType}.
 *
 * @author Tony Wardell
 * Date: 22/09/2017
 * Time: 11:11
 * Created with IntelliJ IDEA.
 */
public class StatisticsToWorkbook {
    private static final Map<String, SheetLayout> SHEET_LAYOUT_MAP = new HashMap<>();

    static {
        SHEET_LAYOUT_MAP.put("goId", new SheetLayout("goid", "GO IDs (by annotation)", "GO IDs (by protein)"));
        SHEET_LAYOUT_MAP.put("aspect", new SheetLayout("aspect", "Aspects (by annotation)", "Aspects (by protein)"));
        SHEET_LAYOUT_MAP.put("evidenceCode",
                             new SheetLayout("evidence",
                                             "Evidence Codes (by annotation)",
                                             "Evidence Codes (by protein)"));
        SHEET_LAYOUT_MAP.put("reference",
                             new SheetLayout("reference",
                                             "References (by annotation)",
                                             "References " + "(by protein)"));
        SHEET_LAYOUT_MAP.put("taxonId",
                             new SheetLayout("taxon", "Taxon IDs (by annotation)", "Taxon IDs " + "(by protein)"));
        SHEET_LAYOUT_MAP.put("assignedBy",
                             new SheetLayout("assigned", "Sources (by annotation)", "Sources " + "(by protein)"));
    }

    public Workbook convert(List<StatisticsGroup> statisticsGroups) {

        Workbook wb = new HSSFWorkbook();
        //        CreationHelper helper = wb.getCreationHelper();

        CellStyle fixedDecimalPlaces = wb.createCellStyle();
        fixedDecimalPlaces.setDataFormat(wb.createDataFormat().getFormat("0.00"));

        Sheet summarySheet = wb.createSheet("summary");
        populateSummarySheet(summarySheet, statisticsGroups);

        StatisticsGroup groupByAnnotation = null;
        StatisticsGroup groupByProtein = null;
        for (StatisticsGroup statisticsGroup : statisticsGroups) {
            if (statisticsGroup.getGroupName().equals("annotation")) {
                groupByAnnotation = statisticsGroup;
            }
            if (statisticsGroup.getGroupName().equals("geneProduct")) {
                groupByProtein = statisticsGroup;
            }
        }
        populateDetailSheets(wb, groupByAnnotation, groupByProtein, fixedDecimalPlaces);
        return wb;
    }

    private void populateDetailSheets(Workbook wb, StatisticsGroup statisticsGroupByAnnotation,
            StatisticsGroup statisticsGroupByProtein, CellStyle fixedDecimalPlaces) {
        Preconditions.checkState(Objects.nonNull(statisticsGroupByAnnotation),
                                 "The statistics by annotation are " + "null");
        Preconditions.checkState(Objects.nonNull(statisticsGroupByProtein), "The statistics by gene product are null");

        List<StatisticsByType> statisticsByAnnotation = statisticsGroupByAnnotation.getTypes();
        List<StatisticsByType> statisticsByProtein = statisticsGroupByProtein.getTypes();

        for (StatisticsByType statisticsByAnnotationForType : statisticsByAnnotation) {

            final String type = statisticsByAnnotationForType.getType();
            StatisticsByType statisticsByProteinForType =
                    matchedStatisticsType(type, statisticsByProtein);

            //Find sheet to populate
            final SheetLayout sheetLayout = SHEET_LAYOUT_MAP.get(type);
            if (sheetLayout == null) {
                continue;
            }
            Sheet sheet = wb.getSheet(sheetLayout.displayName);
            if (sheet == null) {
                sheet = wb.createSheet(sheetLayout.displayName);
            }

            //Populate Sheet
            populateSheet(fixedDecimalPlaces,
                          statisticsByAnnotationForType,
                          statisticsByProteinForType,
                          sheetLayout,
                          sheet);
        }
    }

    private void populateSheet(CellStyle fixedDecimalPlaces, StatisticsByType statisticsByAnnotationForType,
            StatisticsByType statisticsByProteinForType, SheetLayout sheetLayout, Sheet sheet) {
        int rowCounter = 1;

        //Headers
        Row headerRow = sheet.createRow(rowCounter);
        headerRow.createCell(SheetLayout.BY_ANNOTATION_STARTING_COLUMN).setCellValue(sheetLayout.headerByAnnotation);
        headerRow.createCell(SheetLayout.BY_PROTEIN_STARTING_COLUMN).setCellValue(sheetLayout.headerByProtein);

        //Column header Row
        Row colNamesRow = sheet.createRow(++rowCounter);
        populateHeader(SheetLayout.BY_ANNOTATION_STARTING_COLUMN, colNamesRow);
        populateHeader(SheetLayout.BY_PROTEIN_STARTING_COLUMN, colNamesRow);

        //Detail Rows
        final List<StatisticsValue> valuesByAnnotation = statisticsByAnnotationForType.getValues();
        final List<StatisticsValue> valuesByProtein = statisticsByProteinForType.getValues();

        for (int j = 0; j < valuesByAnnotation.size(); j++) {
            Row detailRow = sheet.createRow(++rowCounter);
            populateSection(SheetLayout.BY_ANNOTATION_STARTING_COLUMN,
                            valuesByAnnotation.get(j),
                            detailRow,
                            fixedDecimalPlaces);
            populateSection(SheetLayout.BY_PROTEIN_STARTING_COLUMN,
                            valuesByProtein.get(j),
                            detailRow,
                            fixedDecimalPlaces);
        }
    }

    private StatisticsByType matchedStatisticsType(String statisticsType, List<StatisticsByType> statisticsByType) {
        for (StatisticsByType statisticByType : statisticsByType) {
            if (statisticsType.equals(statisticByType.getType())) {
                return statisticByType;
            }
        }
        throw new IllegalStateException("Failed to find statistics for type " + statisticsByType);
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

        statisticsGroups.stream()
                        .filter(statisticsGroup -> statisticsGroup.getGroupName().equals("annotation"))
                        .forEach(statisticsGroup -> {
                            Row row1 = sheet.createRow(1);
                            row1.createCell(0).setCellValue("Summary");
                            Row row2 = sheet.createRow(2);
                            row2.createCell(0).setCellValue("Number of annotations:" + statisticsGroup.getTotalHits());
                        });
    }

    private static class SheetLayout {
        private static final int BY_ANNOTATION_STARTING_COLUMN = 0;
        private static final int BY_PROTEIN_STARTING_COLUMN = 10;
        String displayName;
        String headerByAnnotation;
        String headerByProtein;

        SheetLayout(String displayName, String headerByAnnotation, String headerByProtein) {
            this.displayName = displayName;
            this.headerByAnnotation = headerByAnnotation;
            this.headerByProtein = headerByProtein;

        }
    }

    private static class SectionHeaderLayout {
        private static final String[] SECTION_COL_HEADINGS = new String[]{"Code", "Percentage", "Count"};
    }

}
