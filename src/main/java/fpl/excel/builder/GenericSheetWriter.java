package fpl.excel.builder;

import fpl.excel.style.ExcelStyleFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Base class for all sheet writers.
 *
 * @param <T> type of data used by this sheet writer
 */
public abstract class GenericSheetWriter<T> {

    protected final String sheetName;
    protected final T data;

    protected GenericSheetWriter(String sheetName, T data) {
        this.sheetName = sheetName;
        this.data = data;
    }

    public abstract Sheet writeSheet(Workbook wb, ExcelStyleFactory styles);
}
