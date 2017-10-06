package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Populate an Excel Workbook instance with statistics data (a list of {@link StatisticsGroup} instances).
 * Provide a sheet for each {@link StatisticsByType} where a format definition is available.
 *
 * @author Tony Wardell
 * Date: 22/09/2017
 * Time: 11:11
 * Created with IntelliJ IDEA.
 */
public class StatisticsToWorkbook implements StatisticsConverter{

    private static final String PERCENTAGE_CELL_FORMAT = "0.00";
    private static final int HEADER_ROW = 1;
    private static final int COLUMN_NAMES_ROW = 2;
    private static final int DETAIL_ROW_INITIAL_VALUE = 3;
    private final String[] sectionTypes;
    private final Map<String, SheetLayout> sheetLayoutMap;

    public StatisticsToWorkbook(String[] sectionTypes, Map<String, SheetLayout> sheetLayoutMap) {
        checkArgument(Objects.nonNull(sectionTypes), "SectionTypes should not be null.");
        checkArgument(Objects.nonNull(sheetLayoutMap), "Sheet layout map should not be null.");
        this.sectionTypes = sectionTypes;
        this.sheetLayoutMap = sheetLayoutMap;
    }

    public Workbook convert(List<StatisticsGroup> statisticsGroups) {
        Workbook wb = new HSSFWorkbook();
        CellStyle percentageCellFormat = createPercentageCellFormat(wb);
        CellStyle boldCellFormat = createBoldCellFormat(wb);
        createSummarySheet(statisticsGroups, wb, boldCellFormat);
        createDetailSheets(statisticsGroups, wb, percentageCellFormat, boldCellFormat);
        return wb;
    }

    private void createDetailSheets(List<StatisticsGroup> statisticsGroups, Workbook wb, CellStyle percentageCellFormat,
            CellStyle boldCellFormat) {
        for (StatisticsGroup statisticsGroup : statisticsGroups) {
            for (String sectionType : sectionTypes) {
                if (statisticsGroup.getGroupName().equalsIgnoreCase(sectionType)) {
                    populateDetailSheetsForGroup(statisticsGroup, wb, percentageCellFormat, boldCellFormat);
                }
            }
        }
    }

    private void createSummarySheet(List<StatisticsGroup> statisticsGroups, Workbook wb, CellStyle boldCellFormat) {
        Sheet summarySheet = wb.createSheet("summary");
        populateSummarySheet(summarySheet, statisticsGroups, boldCellFormat);
    }

    private CellStyle createPercentageCellFormat(Workbook wb) {
        CellStyle percentageCellFormat = wb.createCellStyle();
        percentageCellFormat.setDataFormat(wb.createDataFormat().getFormat(PERCENTAGE_CELL_FORMAT));
        return percentageCellFormat;
    }

    private CellStyle createBoldCellFormat(Workbook wb) {
        CellStyle boldCellFormat = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        boldCellFormat.setFont(font);
        return boldCellFormat;
    }

    private void populateDetailSheetsForGroup(StatisticsGroup statisticsGroup, Workbook wb,
            CellStyle fixedDecimalPlaces, CellStyle boldCellFormat) {

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
                                                                                      boldCellFormat));
        }
    }

    private void populateSectionLayout(Sheet sheet, SectionLayout sectionLayout, StatisticsByType statisticsByType,
            CellStyle fixedDecimalPlaces, CellStyle boldCellFormat) {
        populateSectionHeader(sheet, sectionLayout, boldCellFormat);
        populateSectionColumnNames(sheet, sectionLayout);
        populateSectionDetail(sheet, sectionLayout, statisticsByType, fixedDecimalPlaces);
    }

    private void populateSectionHeader(Sheet sheet, SectionLayout sectionLayout, CellStyle boldCellFormat) {
        Row sectionHeaderRow = getRow(sheet, HEADER_ROW);
        final Cell cell = sectionHeaderRow.createCell(sectionLayout.startingColumn);
        cell.setCellStyle(boldCellFormat);
        cell.setCellValue(sectionLayout.header);
    }

    private void populateSectionColumnNames(Sheet sheet, SectionLayout sectionLayout) {
        Row sectionColumnNames = getRow(sheet, COLUMN_NAMES_ROW);

        int colCounter = sectionLayout.startingColumn;
        for (int i = 0; i < SectionLayout.SECTION_COL_HEADINGS.length; i++) {
            sectionColumnNames.createCell(colCounter++).setCellValue(SectionLayout.SECTION_COL_HEADINGS[i]);
        }
    }

    private void populateSectionDetail(Sheet sheet, SectionLayout sectionLayout, StatisticsByType statisticsByType,
            CellStyle fixedDecimalPlaces) {
        AtomicInteger rowCounter = new AtomicInteger(DETAIL_ROW_INITIAL_VALUE);
        final List<StatisticsValue> values = statisticsByType.getValues();
        for (StatisticsValue value : values) {

            Row detailRow = getRow(sheet, rowCounter.getAndIncrement());

            int colCounter = sectionLayout.startingColumn;
            detailRow.createCell(colCounter).setCellValue(value.getKey());
            detailRow.createCell(++colCounter).setCellValue(value.getName());
            populatePercentageCell(detailRow.createCell(++colCounter), value, fixedDecimalPlaces);
            detailRow.createCell(++colCounter).setCellValue(value.getHits());
        }
    }

    private Row getRow(Sheet sheet, int rowCounter) {
        Row detailRow = sheet.getRow(rowCounter);
        if (detailRow == null) {
            detailRow = sheet.createRow(rowCounter);
        }
        return detailRow;
    }

    private void populatePercentageCell(Cell targetCell, StatisticsValue statisticsValue, CellStyle cellStyle) {
        targetCell.setCellValue(statisticsValue.getPercentage() * 100);
        targetCell.setCellType(CellType.NUMERIC);
        targetCell.setCellStyle(cellStyle);
    }

    private void populateSummarySheet(Sheet sheet, List<StatisticsGroup> statisticsGroups, CellStyle boldCellFormat) {

        statisticsGroups.stream()
                        .filter(statisticsGroup -> statisticsGroup.getGroupName().equals("annotation"))
                        .forEach(statisticsGroup -> {
                            Row row1 = sheet.createRow(1);
                            final Cell header = row1.createCell(0);
                            header.setCellStyle(boldCellFormat);
                            header.setCellValue("Summary");
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
        private static final String[] SECTION_COL_HEADINGS = new String[]{"Code", "Name", "Percentage", "Count"};
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
