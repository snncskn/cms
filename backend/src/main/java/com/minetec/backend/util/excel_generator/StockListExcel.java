package com.minetec.backend.util.excel_generator;

import com.minetec.backend.dto.info.warehouse.StockHistoryInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.data.domain.Page;

import javax.servlet.ServletOutputStream;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StockListExcel extends BaseExcel {

    public void generator(@NotNull final ServletOutputStream servletOutputStream,
                          @NotNull final Page<StockHistoryInfo> stockHistoryInfos) throws IOException {

        var wb = this.workbook();
        var sheet = wb.createSheet(this.getClass().getSimpleName());
        var row = sheet.createRow((short) 0);
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:G1"));

        addHeaders(wb, row, sheet);

        short rowNum = 1;
        for (StockHistoryInfo info : stockHistoryInfos) {
            short idx = 0;
            Row rowX = sheet.createRow(rowNum++);
            addRows(info.getCreatedDate(), rowX, idx++);
            addRows(info.getStockCode(), rowX, idx++);
            addRows(info.getItemDescription(), rowX, idx++);
            addRows(info.getSiteName(), rowX, idx++);
            addRows(info.getMoveType(), rowX, idx++);
            addRows(String.valueOf(info.getQuantity()), rowX, idx++);
            addRows(info.getMoveNumber(), rowX, idx++);
        }

        wb.write(servletOutputStream);
    }

    @Override
    List<String> headers() {
        return Arrays.asList("Date", "Stock Code", "Description", "Site", "Move Source", "Quantity", "Move Number");
    }

}
