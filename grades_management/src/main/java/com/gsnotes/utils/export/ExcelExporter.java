package com.gsnotes.utils.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gsnotes.web.models.ModuleInfo;

public class ExcelExporter {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	private String[] columnNames;
	private Object[][] data;
	private String sheetName = "";

	public ExcelExporter(String[] columnNames, Object[][] data, String sheetName) {
		this.columnNames = columnNames;
		this.data = data;
		this.sheetName = sheetName;
		workbook = new XSSFWorkbook();

	}

	private void writeHeaderLine(int rowNumber) {
		sheet = workbook.createSheet(sheetName);

		Row row = sheet.createRow(rowNumber);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		int i = 0;
		for (String it : columnNames) {
			createCell(row, (i++), it, style);
		}

	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	private void writeDataLines(int rowCount, Object[][] data) {

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);

		for (int i = 0; i < data.length; i++) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;
			for (int j = 0; j < data[i].length; j++) {

				createCell(row, columnCount++, data[i][j], style);
			}
		}

	}

	public void export(HttpServletResponse response) throws IOException {

		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();

		outputStream.close();

	}

	public void test() throws IOException {
		writeHeaderLine(4);
		writeDataLines(5, data);
//		SetFormulas();

		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbook.write(outputStream);
		workbook.close();

	}

	public void fillSheet() {

		writeHeaderLine(4);
		writeDataLines(5, data);

	}

	public void SetFormulas(List<Integer> coefficients, double noteValidation, boolean isNormale) {

		if (data.length == 0) {
			return;
		}

		int validationCellNum = sheet.getRow(5).getLastCellNum();
		XSSFCell validationCell = sheet.getRow(5).createCell(validationCellNum - 1);
		XSSFCell moyenneCell = sheet.getRow(5).createCell(validationCellNum - 2);
		if (isNormale) {

			// embarquer la formulaire dans la cellule de validation
			validationCell.setCellFormula(buildValidationFormula(noteValidation, validationCellNum));

			// la fomulaire de calcul du moyenne
			String formulaAverage = buildAverageFormula(coefficients);
			moyenneCell.setCellFormula(formulaAverage);

		} else {
			// embarquer la formulaire dans la cellule de validation
			validationCell.setCellFormula(buildValidationFormulaRatt(noteValidation, validationCellNum));

			// la fomulaire de calcul du moyenne
			String formulaAverage = buildAverageFormulaRatt(coefficients, noteValidation);
			moyenneCell.setCellFormula(formulaAverage);

		}

	}

	private String buildAverageFormula(List<Integer> coeffs) {

		String averageFormula = "SUM(";
		String sumFormula = "SUM(";
		String[] columnsLetters = new String[] { "E6", "F6", "G6", "H6", "I6", "J6", "K6", "L6" };
		int i = 0;
		for (i = 0; i < coeffs.size() - 1; ++i) {
			averageFormula += coeffs.get(i) + "*" + columnsLetters[i] + ",";
			sumFormula += coeffs.get(i) + ",";
		}

		averageFormula += coeffs.get(i) + "*" + columnsLetters[i] + ")";
		sumFormula += coeffs.get(i) + ")";

		averageFormula += "/" + sumFormula;

		System.out.println(" formula :" + averageFormula);

		return averageFormula;
	}

	private String buildValidationFormula(double noteValidation, int validationCellNum) {

		String letterCol = CellReference.convertNumToColString(validationCellNum - 2);

		String formule = "IF(" + letterCol + "6 <" + noteValidation + ",\"R\",\"V\")";
		System.out.println(formule);
		return formule;

	}
	
	private String buildValidationFormulaRatt(double noteValidation, int validationCellNum) {

		String letterCol = CellReference.convertNumToColString(validationCellNum - 2);

		String formule = "IF(" + letterCol + "6 <" + noteValidation + ",\"NV\",\"V\")";
		System.out.println(formule);
		return formule;

	}

	// une methode qui permet de creer la formule du calcul du moyenne du rattrapage
	private String buildAverageFormulaRatt(List<Integer> coeffs, double noteValidation) {

		System.out.println("coffs :" + coeffs);
		String averageFormula = "MIN(SUM(";
		String sumFormula = "SUM(";
		String[] columnsLetters = new String[] { "E6", "F6", "G6", "H6", "I6", "J6", "K6", "L6" };
		int i = 0;
		int elementsNum = coeffs.size();
		for (i = 0; i < elementsNum; ++i) {
			averageFormula += coeffs.get(i) + "*" + "SUM( 0.4*" + columnsLetters[i] + ", 0.6*"
					+ columnsLetters[i + elementsNum] + "),";

			sumFormula += coeffs.get(i) + ",";
		}
		averageFormula += ")";
		sumFormula += ")";

		averageFormula += "/" + sumFormula;
		averageFormula += "," + noteValidation +")" ;
		

		System.out.println(" formula :" + averageFormula);

		return averageFormula;
	}

	// cette methode permet d'ajouter les données a le sheet du ficher excel

	public void addDataTosheet(int rowNumber, ModuleInfo moduleInfo) {

		// preparer les données
		String[][] data = new String[2][6];

		// the first row
		data[0][0] = "Module";
		data[0][1] = moduleInfo.getNom();
		data[0][2] = "Semestre";
		data[0][3] = moduleInfo.getSemestre();
		data[0][4] = "Année";
		data[0][5] = Integer.toString(moduleInfo.getAnnnee());

		// the second row of the excel header
		data[1][0] = "Enseignant";
		data[1][1] = moduleInfo.getProf();
		data[1][2] = "Session";
		data[1][3] = moduleInfo.getSession();
		data[1][4] = "Classe";
		data[1][5] = moduleInfo.getSession();

		writeDataLines(rowNumber, data);
		// inset the data into the header of the

		for (int i = 0; i < data.length; ++i) {
			for (int j = 0; j < data[0].length; ++j) {
				System.out.println(data[i][j]);
			}
			System.out.println("\n");
		}
	}

	public XSSFWorkbook getWorkbook() {
		return workbook;
	}
}
