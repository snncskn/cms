package com.minetec.backend.util.excel_generator;

import com.minetec.backend.entity.warehouse.Transfer;
import com.minetec.backend.util.Utils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.ServletOutputStream;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TransferListExcel extends BaseExcel {

    public void generator(@NotNull final ServletOutputStream servletOutputStream,
                          @NotNull final List<Transfer> transfers) throws IOException {

        var wb = this.workbook();
        var sheet = wb.createSheet(this.getClass().getSimpleName());
        var row = sheet.createRow((short) 0);
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:G1"));

        addHeaders(wb, row, sheet);

        short rowNum = 1;
        for (Transfer info : transfers) {
            short idx = 0;
            Row rowX = sheet.createRow(rowNum++);
            addRows(info.getTransferNumber(), rowX, idx++);
            addRows(Utils.toString(info.getTransferDate()), rowX, idx++);
            addRows(info.getSourceSite().getDescription(), rowX, idx++);
            addRows(info.getTargetSite().getDescription(), rowX, idx++);
            addRows(info.getRejectionReason(), rowX, idx++);
            addRows(info.getStatus().name(), rowX, idx++);
        }

        wb.write(servletOutputStream);
    }

    @Override
    List<String> headers() {
        return Arrays.asList("Transfer Number", "Transfer Date", "Warehouse Manager Quantity",
            "Source Site", "Target Site", "Status");
    }

}
