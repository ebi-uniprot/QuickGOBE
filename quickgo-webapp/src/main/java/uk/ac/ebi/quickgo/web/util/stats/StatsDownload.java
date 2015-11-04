package uk.ac.ebi.quickgo.web.util.stats;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import uk.ac.ebi.quickgo.bean.statistics.StatisticsBean;
import uk.ac.ebi.quickgo.statistic.StatsTerm;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

/**
 * Class to generate Excel file for the statistics
 *
 * @author cbonill
 *
 */
public class StatsDownload {

	SXSSFWorkbook workBook = new SXSSFWorkbook();

	/**
	 * Generate a statistics file depending on the parameters
	 *
	 * @param summary
	 *            Summary report
	 * @throws IOException
	 */
	public ByteArrayOutputStream generateFile(StatisticsBean summary,
			List<String> categories, long totalNumberAnnotations, long totalNumberProteins,
			boolean byAnnotation, boolean byProtein) throws IOException {

		// create a new file
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// Sort the options like they appear in the pop-up
		//Collections.sort(parameters, new CategoryComparator());

		// Create the sheets. The number of sheets will depend on the parameters
		createSheets(categories, workBook, byProtein);

		// Write values
		writeSheets(summary, categories, totalNumberAnnotations, totalNumberProteins, byProtein, byAnnotation);

		// write the workbook to the output stream
		workBook.write(out);
		// close our file
		out.close();
		return out;
	}

	/**
	 * Create sheets depending on the parameters
	 *
	 * @param categories
	 *            List of parameters obtained from the form
	 * @param workbook
	 *            Workbook to create
	 */
	private void createSheets(List<String> categories, Workbook workbook, boolean byProtein) {
		int index = 0;

		// Create "summary" sheet
		workbook.createSheet();
		workbook.setSheetName(index, "summary");
		index++;

		if(byProtein){// Write protein sheet
			workbook.createSheet();
			workbook.setSheetName(index, sheetName(AnnotationField.DBOBJECTID));
		}
		index++;

		// Create rest of the sheets
		for (String category : categories) {
			if(!category.trim().isEmpty()){
				AnnotationField enumCategory = AnnotationField.valueOf(category.trim().toUpperCase());// Get the corresponding Enum object
				if (enumCategory != null) {
					workbook.createSheet();
					workbook.setSheetName(index, sheetName(AnnotationField.valueOf(category.trim().toUpperCase())));
					index++;
				}
			}
		}
	}

	/**
	 * Write values is sheets
	 *
	 *            Summary
	 * @param categories
	 *            Categories to write
	 */
	private void writeSheets(StatisticsBean stats, List<String> categories, long totalNumberAnnotations, long totalNumberProteins, boolean byProtein, boolean byAnnotation) {

		// Write the summary sheet
		Sheet sheet = workBook.getSheet("summary");// Get the "summary" sheet
		writeSummarySheet(sheet, totalNumberAnnotations, totalNumberProteins);

		Sheet proteinSheet = workBook.getSheet(sheetName(AnnotationField.DBOBJECTID));// Get the "protein" sheet
		if(byProtein){// Write protein sheet
			writeSheet(proteinSheet, stats.getAnnotationsPerDBObjectID(), new HashSet<StatsTerm>(), AnnotationField.DBOBJECTID.getValue().trim(), true);// Write the values
		}

		// Write the rest of the sheets
		for (String category : categories) {
			if(!category.trim().isEmpty()){
				sheet = workBook.getSheet(sheetName(AnnotationField.valueOf(category.trim().toUpperCase())));// Get category sheet
				Set<StatsTerm> statsByAnnotation = new TreeSet<>();
				Set<StatsTerm> statsByProtein = new TreeSet<>();
				if(byAnnotation){
					statsByAnnotation = stats.getStatsByAnnotation(AnnotationField.valueOf(category.trim().toUpperCase()));
				}
				if(byProtein){
					statsByProtein = stats.getStatsByProtein(AnnotationField.valueOf(category.trim().toUpperCase()));
				}
				writeSheet(sheet, statsByAnnotation, statsByProtein, category.trim(), false);// Write the values
			}
		}
	}

	/**
	 * Write the "summary" sheet
	 *
	 * @param sheet
	 *            Summary sheet
	 * @param totalAnnotations
	 *            Number of total annotations to write
	 * @param totalProteins
	 *            Number of total proteins to write
	 */
	private void writeSummarySheet(Sheet sheet, long totalAnnotations,
			long totalProteins) {

		CellStyle cellStyle = boldFontStyle();
		// Title
		SXSSFRow r = (SXSSFRow) sheet.createRow(1);// Row 1
		r.setRowStyle(cellStyle);
		SXSSFCell SXSSFCell = (SXSSFCell) r.createCell(0);
		SXSSFCell.setCellValue("Summary");
		// Values
		r = (SXSSFRow) sheet.createRow(2);// Row 2
		SXSSFCell = (SXSSFCell) r.createCell(0);
		SXSSFCell.setCellValue("Number of annotations: " + totalAnnotations);
		r = (SXSSFRow) sheet.createRow(3);// Row 3
		SXSSFCell = (SXSSFCell) r.createCell(0);
		SXSSFCell.setCellValue("Number of distinct proteins: " + totalProteins);

	}

	/**
	 * Write values in a specific sheet
	 *
	 * @param sheet
	 *            Sheet to be written
	 * @param statsByProtein
	 * @param statsByAnnotation
	 * @param category
	 */
	private void writeSheet(Sheet sheet, Set<StatsTerm> statsByAnnotation, Set<StatsTerm> statsByProtein, String category, boolean proteinSheet) {

		// SXSSFCell style
		CellStyle cellStyle;
		// SXSSFRow
		SXSSFRow r = null;
		// SXSSFCell
		SXSSFCell c = null;
		// Column to start writing the values of the second count (if any)
		int offset = 10;

		// Lists of buckets for each category count
		List<StatsTerm> firstSetOfBuckets = new ArrayList<StatsTerm>();
		List<StatsTerm> secondSetOfBuckets = new ArrayList<StatsTerm>();

		// Category counts title
		String firstTitle = null, secondTitle = null;

		if (statsByAnnotation != null && !statsByAnnotation.isEmpty()) {
			// First
			if (proteinSheet) {
				firstTitle = "Annotations (by protein)";
			} else {
				firstTitle = getDescription(AnnotationField.valueOf(category.trim().toUpperCase())) + " (by annotation)";
			}
			firstSetOfBuckets.addAll(statsByAnnotation);

			if(!proteinSheet){// Not dbObjectID

				if (statsByProtein != null && !statsByProtein.isEmpty()) {
					// Second
					secondTitle = getDescription(AnnotationField.valueOf(category.trim().toUpperCase())) + " (by protein)";
					secondSetOfBuckets.addAll(statsByProtein);
				}
			}
		} else {
			// First
			firstTitle = getDescription(AnnotationField.valueOf(category.trim().toUpperCase())) + " (by protein)";
			firstSetOfBuckets.addAll(statsByProtein);
		}

		// Get the buckets and title for each one

		// If there is just one, set the offset to 0 to start writing values in
		// the first column
		if (statsByAnnotation == null || statsByAnnotation.isEmpty() || statsByProtein == null || statsByProtein.isEmpty()) {
			offset = 0;
		}

		Iterator<StatsTerm> firstBuckIterator = firstSetOfBuckets.iterator();
		Iterator<StatsTerm> secondBuckIterator = secondSetOfBuckets.iterator();

		int rownum = 1;

		cellStyle = boldFontStyle();

		// Set titles
		r = (SXSSFRow) sheet.getRow(rownum);
		if (r == null) {
			r = (SXSSFRow) sheet.createRow(rownum);
		}
		setTitle(0, r, c, cellStyle, firstTitle);
		if(secondTitle != null){
			setTitle(offset, r, c, cellStyle, secondTitle);
		}

		rownum++;

		// Set column headers
		setHeaders(sheet, r, c, 0, rownum, cellStyle);
		if(secondTitle != null){
			setHeaders(sheet, r, c, offset, rownum, cellStyle);
		}
		rownum++;

		// Write values
		writeValues(firstBuckIterator, secondBuckIterator, r, c, rownum, sheet,
				offset);
	}

	/**
	 * Write statistics values
	 * @param firstBuckIterator First set of values to write
	 * @param secondBuckIterator Second set of values to write (if any)
	 * @param r Row
	 * @param c Column
	 * @param rownum Row number
	 * @param sheet Sheet
	 * @param offset Offset
	 */
	private void writeValues(Iterator<StatsTerm> firstBuckIterator, Iterator<StatsTerm> secondBuckIterator, SXSSFRow r, SXSSFCell c, int rownum, Sheet sheet, int offset){
		// Write values while any of the categories counts has buckets
		while (firstBuckIterator.hasNext() || secondBuckIterator.hasNext()) {

			// Create row
			r = (SXSSFRow) sheet.getRow(rownum);
			if (r == null) {
				r = (SXSSFRow) sheet.createRow(rownum);
			}

			// Write bucket values
			if (firstBuckIterator.hasNext()) {
				writeBucketValues(sheet, r, c, 0, rownum,
						firstBuckIterator.next());
			}
			if (secondBuckIterator.hasNext()) {
				writeBucketValues(sheet, r, c, offset, rownum,
						secondBuckIterator.next());
			}
			rownum++;
		}
	}

	/**
	 * Creates a style with font bold
	 * @return Bold font style
	 */
	private CellStyle boldFontStyle() {
		CellStyle cellStyle = workBook.createCellStyle();
		// Create bold font for the titles and column headers
		Font font = workBook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		cellStyle.setFont(font);

		return cellStyle;
	}

	/**
	 * Set the title (by annotation or by protein)
	 *
	 * @param index
	 * @param r
	 *            Row
	 * @param c
	 *            Column
	 * @param cellStyle
	 *            Cell style
	 * @param title
	 *            Title to write
	 */
	private void setTitle(int index, SXSSFRow r, SXSSFCell c, CellStyle cellStyle, String title) {
		if (title != null && title.trim().length() > 0) {
			c = (SXSSFCell) r.createCell(index);
			c.setCellStyle(cellStyle);
			c.setCellValue(title);
		}
	}

	/**
	 * Write header values
	 *
	 * @param sheet
	 *            Sheet
	 * @param r
	 *            SXSSFRow
	 * @param c
	 *            Column
	 * @param rownum
	 *            SXSSFRow number
	 * @param cellStyle
	 *            SXSSFCell style
	 */
	private void setHeaders(Sheet sheet, SXSSFRow r, SXSSFCell c, int offset,	int rownum, CellStyle cellStyle) {
		r = (SXSSFRow) sheet.getRow(rownum);
		if (r == null) {
			r = (SXSSFRow) sheet.createRow(rownum);
		}
		r.setRowStyle(cellStyle);
		c = (SXSSFCell) r.createCell(offset);// First column
		c.setCellValue("Code");
		c = (SXSSFCell) r.createCell(offset + 1);// Second columns
		c.setCellValue("Name");
		c = (SXSSFCell) r.createCell(offset + 2);// Third column
		c.setCellValue("Percentage");
		c = (SXSSFCell) r.createCell(offset + 3);// Third column
		c.setCellValue("Count");
	}

	/**
	 * Write bucket values
	 *
	 * @param sheet
	 *            Sheet
	 * @param r
	 *            Row
	 * @param c
	 *            Column
	 * @param rownum
	 *            Row number
	 * @param bucket
	 *            Bucket to write
	 */
	private void writeBucketValues(Sheet sheet, SXSSFRow r, SXSSFCell c, int offset, int rownum, StatsTerm bucket) {
		if (bucket != null) {
			c = (SXSSFCell) r.createCell(offset);// First column
			c.setCellValue(bucket.getCode());
			c = (SXSSFCell) r.createCell(offset + 1);// Second columns
			c.setCellValue(bucket.getName());
			c = (SXSSFCell) r.createCell(offset + 2);// Third column
			c.setCellValue(bucket.getPercentage());
			c = (SXSSFCell) r.createCell(offset + 3);// Fourth column
			c.setCellValue(bucket.getCount());
		}
	}


	public String getDescription(AnnotationField annotationField){
		switch (annotationField){
			case GOID:
				return "Go IDs";
			case GOASPECT:
				return "Aspects";
			case GOEVIDENCE:
				return "Evidence Codes";
			case REFERENCE:
				return "References";
			case TAXONOMYID:
				return "Taxon IDs";
			case ASSIGNEDBY:
				return "Sources";
		}
		return "";
	}

	public String sheetName(AnnotationField annotationField){
		switch (annotationField){
			case DBOBJECTID:
				return "protein";
			case GOID:
				return "goid";
			case GOASPECT:
				return "aspect";
			case GOEVIDENCE:
				return "evidence";
			case REFERENCE:
				return "reference";
			case TAXONOMYID:
				return "taxon";
			case ASSIGNEDBY:
				return "assigned";
		}
		return "";
	}
}
