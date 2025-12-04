package fpl.excel.builder;

import fpl.excel.style.ExcelStyleFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public class TableSheetWriter<T> extends GenericSheetWriter<List<T>> {

    private final List<Col<T>> columns;

    public TableSheetWriter(String sheetName, List<T> data, List<Col<T>> columns) {
        super(sheetName, data);
        this.columns = columns;
    }

    @Override
    public Sheet writeSheet(Workbook wb, ExcelStyleFactory styles) {

        Sheet sheet = wb.createSheet(sheetName);

        Row header = sheet.createRow(0);
        CellStyle headerStyle = styles.header();

        for (int i = 0; i < columns.size(); i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns.get(i).title());
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (T item : data) {
            Row row = sheet.createRow(rowNum++);

            for (int col = 0; col < columns.size(); col++) {
                Object value = columns.get(col).extractor().apply(item);
                Cell cell = row.createCell(col);

                if (value instanceof Number n) {
                    cell.setCellValue(n.doubleValue());
                } else if (value != null) {
                    cell.setCellValue(value.toString());
                } else {
                    cell.setBlank();
                }
            }
        }

        for (int i = 0; i < columns.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        return sheet;
    }
}
