package fpl.excel.sheets;

import fpl.domain.transfers.TransfersData;
import fpl.excel.builder.GenericSheetWriter;
import fpl.excel.style.ExcelStyleFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class TransfersSheetWriter
        extends GenericSheetWriter<TransfersData> {

    public TransfersSheetWriter(TransfersData data) {
        super("Transfers", data);
    }

    @Override
    public Sheet writeSheet(Workbook wb, ExcelStyleFactory styles) {

        Sheet sheet = wb.createSheet(sheetName);

        SummaryTableWriter writer = new SummaryTableWriter(sheet, styles);

        int baseCol = 0;
        int startRow = 0;

        writer.writeMapTable(
                startRow,
                baseCol++, "Player",
                baseCol++, "In",
                data.withoutFreeHitIn()
        );

        writer.writeMapTable(
                startRow,
                baseCol++, "Player",
                baseCol++, "Out",
                data.withoutFreeHitOut()
        );

        baseCol++;

        writer.writeMapTable(
                startRow,
                baseCol++, "Wildcard",
                baseCol++, "In",
                data.wildcardIn()
        );

        writer.writeMapTable(
                startRow,
                baseCol++, "Wildcard",
                baseCol++, "Out",
                data.wildcardOut()
        );

        baseCol++;

        writer.writeMapTable(
                startRow,
                baseCol++, "FreeHit",
                baseCol++, "In",
                data.freeHitIn()
        );

        writer.writeMapTable(
                startRow,
                baseCol++, "FreeHit",
                baseCol, "Out",
                data.freeHitOut()
        );

        for (int c = 0; c <= baseCol; c++) sheet.autoSizeColumn(c);

        return sheet;
    }
}
