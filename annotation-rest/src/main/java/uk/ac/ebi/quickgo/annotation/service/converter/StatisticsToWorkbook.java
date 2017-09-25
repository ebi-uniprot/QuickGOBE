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
       sheetLayoutMap.put("goId",new SheetLayout("goid", "GO IDs (by annotation)", 0,"GO IDs (by protein)",10));
//                   new SheetLayout("aspect",)
//                   new SheetLayout("evidence",)
//                   new SheetLayout("reference",)
//                   new SheetLayout("taxon",)
//                   new SheetLayout("assigned")};

   }
//    static SheetLayout[] SHEETS = new String[] {


    public Workbook convert(List<StatisticsGroup> statisticsGroups){

        Workbook wb = new HSSFWorkbook();
        CreationHelper helper = wb.getCreationHelper();

        CellStyle fixedDecimalPlaces = wb.createCellStyle();
        fixedDecimalPlaces.setDataFormat(wb.createDataFormat().getFormat("0.00"));

        Sheet summarySheet = wb.createSheet("summary");
        populateSummarySheet(summarySheet, statisticsGroups);
        populateDetailSheets(wb, statisticsGroups.get(0), statisticsGroups.get(1), fixedDecimalPlaces);
        return wb;
    }

    private void populateDetailSheets(Workbook wb, StatisticsGroup statisticsGroup1, StatisticsGroup statisticsGroup2,
            CellStyle fixedDecimalPlaces) {

        // annotation or geneProduct



        List<StatisticsByType> statisticsByType1 = statisticsGroup1.getTypes();
        List<StatisticsByType> statisticsByType2 = statisticsGroup2.getTypes();

        for (int i = 0; i < statisticsByType1.size(); i++) {

            StatisticsByType byType1 = statisticsByType1.get(i);
            StatisticsByType byType2 = null;

            //Get the matching StatisticsByType from the second group
            for (int j = 0; j < statisticsByType2.size(); j++) {
                byType2 = statisticsByType2.get(j);
                if(byType1.getType().equals(byType2.getType())){
                    break;
                }
            }

            //Find sheet to populate
            final SheetLayout sheetLayout = sheetLayoutMap.get(byType1.getType());
            if(sheetLayout == null) continue;
            Sheet sheet = wb.getSheet(sheetLayout.displayName);
            if(sheet == null) {
                sheet = wb.createSheet(sheetLayout.displayName);
            }

            //Populate Sheet
            int rowCounter = 1;

            //Headers
            Row headerRow = sheet.createRow(rowCounter);
            headerRow.createCell(sheetLayout.headerACol).setCellValue(sheetLayout.headerA);
            headerRow.createCell(sheetLayout.headerBCol).setCellValue(sheetLayout.headerB);

            //Column headers
            int colNamesCounterLeft = sheetLayout.headerACol;
            int colNamesCounterRight = sheetLayout.headerBCol;
            Row colNamesRow = sheet.createRow(++rowCounter);
            colNamesRow.createCell(colNamesCounterLeft).setCellValue("Code");
            colNamesRow.createCell(++colNamesCounterLeft).setCellValue("Percentage");
            colNamesRow.createCell(++colNamesCounterLeft).setCellValue("Count");
            colNamesRow.createCell(colNamesCounterRight).setCellValue("Code");
            colNamesRow.createCell(++colNamesCounterRight).setCellValue("Percentage");
            colNamesRow.createCell(++colNamesCounterRight).setCellValue("Count");

            //Detail line
            final List<StatisticsValue> values1 = byType1.getValues();
            final List<StatisticsValue> values2 = byType2.getValues();

            for (int j = 0; j < values1.size(); j++) {
                int colCounterLeft = sheetLayout.headerACol;
                int colCounterRight = sheetLayout.headerBCol;

                StatisticsValue statisticsValueLHS =  values1.get(j);
                Row detailRow = sheet.createRow(++rowCounter);

                //Left hand side
//                detailRow.createCell(colCounterLeft).setCellValue(statisticsValueLHS.getKey());
//                detailRow.createCell(++colCounterLeft).setCellValue(statisticsValueLHS.getPercentage()*100);
//                detailRow.createCell(++colCounterLeft).setCellValue(statisticsValueLHS.getHits());
                populateSection(statisticsValueLHS, colCounterLeft, detailRow, fixedDecimalPlaces);

                //Right hand side
//                StatisticsValue statisticsValueRHS = values2.get(j);
//                detailRow.createCell(colCounterRight).setCellValue(statisticsValueRHS.getKey());
//                populatePercentageCell(detailRow.createCell(++colCounterRight), statisticsValueRHS, fixedDecimalPlaces );
//                detailRow.createCell(++colCounterRight).setCellValue(statisticsValueRHS.getHits());
                populateSection(values2.get(j), colCounterRight, detailRow, fixedDecimalPlaces);
            }
        }

    }

    private void populateSection(StatisticsValue statisticsValue, int ColCounter, Row detailRow, CellStyle fixedDecimalPlaces){
        detailRow.createCell(ColCounter).setCellValue(statisticsValue.getKey());
        populatePercentageCell(detailRow.createCell(++ColCounter), statisticsValue, fixedDecimalPlaces );
        detailRow.createCell(++ColCounter).setCellValue(statisticsValue.getHits());
    }


    private void populatePercentageCell(Cell targetCell, StatisticsValue statisticsValue, CellStyle cellStyle){
        targetCell.setCellValue(statisticsValue.getPercentage()*100);
        targetCell.setCellType(CellType.NUMERIC);
        targetCell.setCellStyle(cellStyle);
    }

    private void populateSummarySheet(Sheet sheet, List<StatisticsGroup> statisticsGroups) {

        for (int i = 0; i < statisticsGroups.size(); i++) {
            StatisticsGroup statisticsGroup = statisticsGroups.get(i);
            if(statisticsGroup.getGroupName().equals("annotation")){
                Row row1 = sheet.createRow(1);
                row1.createCell(0).setCellValue("Summary");
                Row row2 = sheet.createRow(2);
                row2.createCell(0).setCellValue("Number of annotations:" + statisticsGroup.getTotalHits());
            }
        }
    }

    static class SheetLayout{
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
}
