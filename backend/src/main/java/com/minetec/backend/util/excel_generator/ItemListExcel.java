package com.minetec.backend.util.excel_generator;

import com.minetec.backend.dto.info.ItemInfo;
import com.minetec.backend.util.Utils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.data.domain.Page;

import javax.servlet.ServletOutputStream;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ItemListExcel extends BaseExcel {

    public void generator(@NotNull final ServletOutputStream servletOutputStream,
                          @NotNull final Page<ItemInfo> items) throws IOException {

        var wb = this.workbook();
        var sheet = wb.createSheet(this.getClass().getSimpleName());
        var row = sheet.createRow((short) 0);
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:G1"));

        addHeaders(wb, row, sheet);

        short rowNum = 1;
        for (var info : items) {
            short idx = 0;
            var rowX = sheet.createRow(rowNum++);
            addRows(info.getStorePartNumber(), rowX, idx++);
            addRows(info.getItemDescription(), rowX, idx++);
            addRows(info.getUnit(), rowX, idx++);
            addRows(info.getBarcode(), rowX, idx++);
            addRows(Utils.toString(info.getCurrentQuantity()), rowX, idx++);
            addRows(Utils.toString(info.getPrice()), rowX, idx++);
            addRows(Utils.toString(info.getMinStockQuantity()), rowX, idx++);
        }

        wb.write(servletOutputStream);

    }

    @Override
    List<String> headers() {
        return Arrays.asList("Stock Code", "Item Description", "Unit", "Barcode",
            "Current Quantity", "Price", "Min Stock Quantity");
    }

}
