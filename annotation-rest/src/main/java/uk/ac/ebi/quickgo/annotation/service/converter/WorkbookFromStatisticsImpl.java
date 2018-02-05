package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;

import java.util.LinkedHashSet;
import java.util.List;
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
public class WorkbookFromStatisticsImpl implements WorkbookFromStatistics {

    static final String ANNOTATIONS_SUMMARY = "Number of annotations:";
    static final String GENE_PRODUCTS_SUMMARY = "Number of distinct proteins:";
    static final String SUMMARY_HEADER = "Summary";
    private static final String PERCENTAGE_CELL_FORMAT = "0.00";
    private static final int HEADER_ROW = 1;
    private static final int COLUMN_NAMES_ROW = 2;
    private static final int DETAIL_ROW_INITIAL_VALUE = 3;
    private static final int FIRST_COLUMN = 0;
    private static final int SUMMARY_DETAIL_ROW = 2;
    private static final String SUMMARY_SHEET_NAME = "summary";
    private final LinkedHashSet<SheetLayout> sheetLayoutSet;

    public WorkbookFromStatisticsImpl(LinkedHashSet<SheetLayout> sheetLayouts) {
        checkArgument(Objects.nonNull(sheetLayouts), "Sheet layout map should not be null.");
        this.sheetLayoutSet = sheetLayouts;
    }

    public Workbook generate(List<StatisticsGroup> statisticsGroups) {
        Workbook wb = new HSSFWorkbook();
        CellStyle percentageCellFormat = createPercentageCellFormat(wb);
        CellStyle boldCellFormat = createBoldCellFormat(wb);
        createSummarySheet(statisticsGroups, wb, boldCellFormat);
        createDetailSheets(statisticsGroups, wb, percentageCellFormat, boldCellFormat);
        return wb;
    }

    private void createDetailSheets(List<StatisticsGroup> statisticsGroups, Workbook wb, CellStyle
            percentageCellFormat, CellStyle boldCellFormat) {
        for (SheetLayout layout : sheetLayoutSet) {
            for (StatisticsGroup statisticsGroup : statisticsGroups) {
                //add group/type information to layout
                writeGroupAndTypeValuesToTab(layout, statisticsGroup, wb, percentageCellFormat, boldCellFormat);
            }
        }
    }

    private void writeGroupAndTypeValuesToTab(SheetLayout layout, StatisticsGroup statisticsGroup,
            Workbook wb, CellStyle fixedDecimalPlaces, CellStyle boldCellFormat) {
        for (StatisticsByType statisticsByType : statisticsGroup.getTypes()) {
            if (layout.typeName.equals(statisticsByType.getType())) {
                final Sheet sheet = retrieveOrCreateSheet(wb, layout);

                layout.sectionLayouts.stream()
                        .filter(sectionLayout -> sectionLayout.type.equals(statisticsGroup.getGroupName()))
                        .forEach(sectionLayout -> populateSectionLayout(sheet,
                                sectionLayout,
                                statisticsByType,
                                fixedDecimalPlaces,
                                boldCellFormat));
            }
        }
    }

    private void createSummarySheet(List<StatisticsGroup> statisticsGroups, Workbook wb, CellStyle boldCellFormat) {
        Sheet summarySheet = wb.createSheet(SUMMARY_SHEET_NAME);
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

    private Sheet retrieveOrCreateSheet(Workbook wb, SheetLayout sheetLayout) {
        return wb.getSheet(sheetLayout.displayName) != null ? wb.getSheet(sheetLayout.displayName)
                : wb.createSheet(sheetLayout.displayName);
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
        targetCell.setCellValue(statisticsValue.getPercentage());
        targetCell.setCellType(CellType.NUMERIC);
        targetCell.setCellStyle(cellStyle);
    }

    private void populateSummarySheet(Sheet sheet, List<StatisticsGroup> statisticsGroups, CellStyle boldCellFormat) {
        showSummaryHeaderText(sheet, boldCellFormat);
        showAnnotationSummary(sheet, statisticsGroups);
        showGeneProductSummary(sheet, statisticsGroups);
    }

    private void showSummaryHeaderText(Sheet sheet, CellStyle boldCellFormat) {
        Row headerRow = sheet.createRow(HEADER_ROW);
        final Cell headerCell = headerRow.createCell(FIRST_COLUMN);
        headerCell.setCellStyle(boldCellFormat);
        headerCell.setCellValue(SUMMARY_HEADER);
    }

    private void showAnnotationSummary(Sheet sheet, List<StatisticsGroup> statisticsGroups) {
        statisticsGroups.stream()
                .filter(statisticsGroup -> statisticsGroup.getGroupName().equals("annotation"))
                .forEach(statisticsGroup -> {
                    Row annotationSummaryRow = sheet.createRow(SUMMARY_DETAIL_ROW);
                    annotationSummaryRow.createCell(FIRST_COLUMN).setCellValue(
                            ANNOTATIONS_SUMMARY + statisticsGroup.getTotalHits());
                });
    }

    private void showGeneProductSummary(Sheet sheet, List<StatisticsGroup> statisticsGroups) {
        statisticsGroups.stream()
                .filter(statisticsGroup -> statisticsGroup.getGroupName().equals("geneProduct"))
                .forEach(statisticsGroup -> {
                    Row geneProductSummaryRow = sheet.createRow(SUMMARY_DETAIL_ROW + 1);
                    geneProductSummaryRow.createCell(FIRST_COLUMN)
                            .setCellValue(GENE_PRODUCTS_SUMMARY + statisticsGroup.getTotalHits());
                });
    }
}
