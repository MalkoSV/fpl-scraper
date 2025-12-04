package fpl.excel.io;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WorkbookFactory {

    public Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

}
