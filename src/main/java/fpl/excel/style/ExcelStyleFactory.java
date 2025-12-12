package fpl.excel.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized factory for all Excel cell styles.
 * Styles are cached per workbook to avoid duplication.
 */
public class ExcelStyleFactory {

    private final Workbook workbook;
    private final Map<String, CellStyle> cache = new HashMap<>();

    public ExcelStyleFactory(Workbook workbook) {
        this.workbook = workbook;
    }

    public CellStyle header() {
        return cache.computeIfAbsent("HEADER", ignored -> {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            return style;
        });
    }

    public CellStyle centered() {
        return cache.computeIfAbsent("CENTERED", ignored -> {
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            return style;
        });
    }

    public CellStyle summaryTitle() {
        return cache.computeIfAbsent("SUMMARY_TITLE", ignored -> {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.LEFT);
            return style;
        });
    }

    public CellStyle summaryValue() {
        return cache.computeIfAbsent("SUMMARY_VALUE", ignored -> {
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.RIGHT);
            return style;
        });
    }

    public CellStyle withColor(Color color) {
        return cache.computeIfAbsent(color.name(), ignored -> {
            CellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(color.getColorIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            return style;
        });
    }

}
