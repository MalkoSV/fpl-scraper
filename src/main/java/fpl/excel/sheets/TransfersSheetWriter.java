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

        writer.writeMapTable(
                0,
                baseCol , "Player",
                baseCol + 1, "In",
                data.transfersIn()
        );

        writer.writeMapTable(
                0,
                baseCol + 3, "Player",
                baseCol + 4, "Out",
                data.transfersOut()
        );

        for (int c = 0; c < 5; c++) sheet.autoSizeColumn(c);

        return sheet;
    }
}
