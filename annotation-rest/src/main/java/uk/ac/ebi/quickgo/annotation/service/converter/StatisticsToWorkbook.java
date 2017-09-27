package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;


import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Populate an Excel Workbook instance with statistics data (a list of {@link StatisticsGroup} instances).
 * Provide a sheet for each {@link StatisticsByType} where a format definition is available.
 *
 * @author Tony Wardell
 * Date: 22/09/2017
 * Time: 11:11
 * Created with IntelliJ IDEA.
 */
public class StatisticsToWorkbook {

    public static final String PERCENTAGE_CELL_FORMAT = "0.00";
    private final String[] sectionTypes;
    private final Map<String, SheetLayout> sheetLayoutMap;

    public StatisticsToWorkbook(String[] sectionTypes, Map<String, SheetLayout> sheetLayoutMap) {
        this.sectionTypes = checkNotNull(sectionTypes);
        this.sheetLayoutMap = checkNotNull(sheetLayoutMap);
    }

    public Workbook convert(List<StatisticsGroup> statisticsGroups, int downloadLimit) {
        Workbook wb = new HSSFWorkbook();
        //        CreationHelper helper = wb.getCreationHelper();

        CellStyle percentageCellFormat = wb.createCellStyle();
        percentageCellFormat.setDataFormat(wb.createDataFormat().getFormat(PERCENTAGE_CELL_FORMAT));

        Sheet summarySheet = wb.createSheet("summary");
        populateSummarySheet(summarySheet, statisticsGroups);

        for (StatisticsGroup statisticsGroup : statisticsGroups) {
            for (String sectionType : sectionTypes) {
                if (statisticsGroup.getGroupName().equalsIgnoreCase(sectionType)) {
                    populateDetailSheetsForGroup(statisticsGroup, wb, percentageCellFormat, downloadLimit);
                }
            }
        }
        return wb;
    }

    private void populateDetailSheetsForGroup(StatisticsGroup statisticsGroup, Workbook wb,
            CellStyle fixedDecimalPlaces, int downloadLimit) {

        for (StatisticsByType statisticsByType : statisticsGroup.getTypes()) {

            final SheetLayout sheetLayout = sheetLayoutMap.get(statisticsByType.getType());
            if (sheetLayout == null) {
                continue;
            }

            final Sheet sheet = wb.getSheet(sheetLayout.displayName) != null ? wb.getSheet(sheetLayout.displayName)
                    : wb.createSheet(sheetLayout.displayName);

            sheetLayout.sectionLayouts.stream()
                                      .filter(sectionLayout -> sectionLayout.type.equals(statisticsGroup.getGroupName()))
                                      .forEach(sectionLayout -> populateSectionLayout(sheet,
                                                                                      sectionLayout,
                                                                                      statisticsByType,
                                                                                      fixedDecimalPlaces,
                                                                                      downloadLimit));
        }
    }

    private void populateSectionLayout(Sheet sheet, SectionLayout sectionLayout, StatisticsByType statisticsByType,
            CellStyle fixedDecimalPlaces, int downloadLimit) {
        AtomicInteger rowCounter = new AtomicInteger(1);

        populateSectionHeader(sheet, sectionLayout, rowCounter);
        populateSectionColumnNames(sheet, sectionLayout, rowCounter);
        populateSectionDetail(sheet, sectionLayout, rowCounter, statisticsByType, fixedDecimalPlaces,downloadLimit);
    }

    private void populateSectionHeader(Sheet sheet, SectionLayout sectionLayout, AtomicInteger rowCounter) {
        rowCounter.incrementAndGet();
        Row sectionHeaderRow = getRow(sheet, rowCounter);
        sectionHeaderRow.createCell(sectionLayout.startingColumn).setCellValue(sectionLayout.header);
    }

    private void populateSectionColumnNames(Sheet sheet, SectionLayout sectionLayout, AtomicInteger rowCounter) {
        rowCounter.incrementAndGet();
        Row sectionColumnNames = getRow(sheet, rowCounter);

        int colCounter = sectionLayout.startingColumn;
        for (int i = 0; i < SectionLayout.SECTION_COL_HEADINGS.length; i++) {
            sectionColumnNames.createCell(colCounter++).setCellValue(SectionLayout.SECTION_COL_HEADINGS[i]);
        }
    }

    private void populateSectionDetail(Sheet sheet, SectionLayout sectionLayout, AtomicInteger rowCounter,
            StatisticsByType statisticsByType, CellStyle fixedDecimalPlaces, int downloadLimit) {

        int detailLineCount = 0;
        final List<StatisticsValue> values = statisticsByType.getValues();
        for (StatisticsValue value : values) {

            if(downloadLimit > 0 && detailLineCount++ == downloadLimit){
                break;
            }

            rowCounter.incrementAndGet();
            Row detailRow = getRow(sheet, rowCounter);

            int colCounter = sectionLayout.startingColumn;
            detailRow.createCell(colCounter).setCellValue(value.getKey());
            populatePercentageCell(detailRow.createCell(++colCounter), value, fixedDecimalPlaces);
            detailRow.createCell(++colCounter).setCellValue(value.getHits());
        }
    }

    private Row getRow(Sheet sheet, AtomicInteger rowCounter) {
        Row detailRow = sheet.getRow(rowCounter.get());
        if (detailRow == null) {
            detailRow = sheet.createRow(rowCounter.get());
        }
        return detailRow;
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

    static class SheetLayout {
        final String displayName;
        final List<SectionLayout> sectionLayouts;

        SheetLayout(String displayName, List<SectionLayout> sectionLayouts) {
            this.displayName = displayName;
            this.sectionLayouts = sectionLayouts;
        }
    }

    static class SectionLayout {
        private static final String[] SECTION_COL_HEADINGS = new String[]{"Code", "Percentage", "Count"};
        final String type;
        private final String header;
        private final int startingColumn;

        SectionLayout(String type, String header, int startingColumn) {
            this.type = type;
            this.header = header;
            this.startingColumn = startingColumn;
        }
    }
}
