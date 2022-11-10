package com.bct.HOS.App.utils;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Excel2JSON {
	JSONArray dataSet = new JSONArray();

	public JSONArray convert(String fileName) {
		try {
			/* First need to open the file. */
			FileInputStream fInputStream = new FileInputStream(fileName.trim());
			Workbook excelWorkBook = new HSSFWorkbook(fInputStream);
			// Get all excel sheet count.
			int totalSheetNumber = excelWorkBook.getNumberOfSheets();
			// Loop in all excel sheet.
			for (int i = 0; i < totalSheetNumber; i++) {
				// Get current sheet.
				Sheet sheet = excelWorkBook.getSheetAt(i);

				// Get sheet name.
				String sheetName = sheet.getSheetName();

				if (sheetName != null && sheetName.length() > 0) {
					// Get current sheet data in a list table.
					List<List<String>> sheetDataTable = getSheetDataList(sheet);

					// Generate JSON format of above sheet data and write to a JSON file.
					getJSONStringFromList(sheetDataTable);
				}
			}
			// Close excel work book object.
			excelWorkBook.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dataSet;
	}

	private List<List<String>> getSheetDataList(Sheet sheet) {
		List<List<String>> ret = new ArrayList<List<String>>();

		// Get the first and last sheet row number.
		int firstRowNum = sheet.getFirstRowNum();
		int lastRowNum = sheet.getLastRowNum();

		if (lastRowNum > 0) {
			// Loop in sheet rows.
			for (int i = firstRowNum; i < lastRowNum + 1; i++) {
				// Get current row object.
				Row row = sheet.getRow(i);

				// Get first and last cell number.
				int firstCellNum = row.getFirstCellNum();
				int lastCellNum = row.getLastCellNum();

				// Create a String list to save column data in a row.
				List<String> rowDataList = new ArrayList<String>();

				// Loop in the row cells.
				for (int j = firstCellNum; j < lastCellNum; j++) {
					// Get current cell.
					Cell cell = row.getCell(j);

					// Get cell type.
					int cellType = cell.getCellType();
					if (cellType == CellType.NUMERIC.getCode()) {
						double numberValue = cell.getNumericCellValue();
						String stringCellValue = BigDecimal.valueOf(numberValue).toPlainString();
						
						/*
						if (HSSFDateUtil.isCellDateFormatted(row.getCell(j))) {
					        //System.out.println ("Row No.: " + row.getRowNum ()+ " " + 
					            row.getCell(j).getDateCellValue());
					    }
						*/
						if(HSSFDateUtil.isCellDateFormatted(cell))
					      {
					          DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
					          Date date = cell.getDateCellValue();
					          stringCellValue = df.format(date);
					       }
						
						
						rowDataList.add(stringCellValue);
						
						
						
						
					} else if (cellType == CellType.STRING.getCode()) {
						String cellValue = cell.getStringCellValue();
						rowDataList.add(cellValue);
					} else if (cellType == CellType.BOOLEAN.getCode()) {
						boolean numberValue = cell.getBooleanCellValue();
						String stringCellValue = String.valueOf(numberValue);
						rowDataList.add(stringCellValue);
					} else if (cellType == CellType.BLANK.getCode()) {
						rowDataList.add("");
					} else {
						String cellValue = cell.getStringCellValue();
						rowDataList.add(cellValue);
					}
				}

				// Add current row data list in the return list.
				ret.add(rowDataList);
			}
		}
		return ret;
	}

	private void getJSONStringFromList(List<List<String>> dataTable) {
		try {
			if (dataTable != null) {
				int rowCount = dataTable.size();

				if (rowCount > 1) {
					// Create a JSONArray or JSONObject to store table data.
					// JSONArray tableJSONArray = new JSONArray();

					// The first row is the header row, store each column name.
					List<String> headerRow = dataTable.get(0);

					int columnCount = headerRow.size();

					// Loop in the row data list.
					for (int i = 1; i < rowCount; i++) {
						// Get current row data.
						List<String> dataRow = dataTable.get(i);

						// Create a JSONObject object to store row data.
						JSONObject rowJsonObject = new JSONObject();

						rowJsonObject.put("SNO", "" + i);
						for (int j = 0; j < columnCount; j++) {
							String columnName = headerRow.get(j);
							String columnValue = dataRow.get(j);
							rowJsonObject.put(columnName, columnValue);

						}

						// tableJsonObject.put("Row " + i, rowJsonObject);

						dataSet.add(rowJsonObject);
					}

					// Return string format data of JSONObject object.
					// ret = tableJsonObject.toString();
					// ret = tableJSONArray.toString();
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
			//JSONArray str = new Excel2JSON().convert("C:\\Users\\ap140007\\Desktop\\HOS\\price_bulk_update_template.xls");
			////System.out.println(str.toString());

	        //OffsetDateTime offsetDT3 = OffsetDateTime.now(ZoneId.of("UTC"));
	        ////System.out.println("OffsetDateTime3: " + offsetDT3);
			
			OffsetDateTime dateTime4 = OffsetDateTime.of(LocalDateTime.of(2020, 12, 14, 21, 36),
		            ZoneOffset.UTC);
		      //System.out.println(dateTime4);


	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String convertTime(long time){
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
	    return format.format(date);
	}


}
