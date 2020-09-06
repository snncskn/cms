package com.minetec.backend.util.excel_generator;

import com.minetec.backend.repository.projection.OrderListItemProjection;
import com.minetec.backend.util.Utils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.data.domain.Page;

import javax.servlet.ServletOutputStream;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class OrderListExcel extends BaseExcel {

    public void generator(@NotNull final ServletOutputStream servletOutputStream,
                          @NotNull final Page<OrderListItemProjection> orderListItemProjections) throws IOException {

        var wb = this.workbook();
        var sheet = wb.createSheet(this.getClass().getSimpleName());
        var row = sheet.createRow((short) 0);
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:F1"));

        addHeaders(wb, row, sheet);

        short rowNum = 1;
        for (var info : orderListItemProjections) {

            short idx = 0;
            var rowX = sheet.createRow(rowNum++);

            switch (info.getStatus()) {
                case PURCHASE_REQUEST:
                    addRows(Utils.toString(info.getRequestDate()), rowX, idx++);
                    addRows(info.getRequestNumber(), rowX, idx++);
                    break;
                case ORDER:
                    addRows(Utils.toString(info.getOrderCreationDate()), rowX, idx++);
                    addRows(info.getOrderNumber(), rowX, idx++);
                    break;
                case PARTIAL:
                    addRows(Utils.toString(info.getOrderCreationDate()), rowX, idx++);
                    addRows(info.getOrderNumber(), rowX, idx++);
                    break;
                case INVOICE:
                    addRows(Utils.toString(info.getInvoiceDate()), rowX, idx++);
                    addRows(info.getInvoiceNumber(), rowX, idx++);
                    break;
                case REJECTED:
                    addRows(Utils.toString(info.getRequestDate()), rowX, idx++);
                    addRows(info.getRequestNumber(), rowX, idx++);
                default:
                    break;
            }

            addRows(info.getSupplier().get(0).getName(), rowX, idx++);
            addRows(Utils.toString(info.getTotalQuantity()), rowX, idx++);
            addRows(Utils.toString(info.getGrandTotal()), rowX, idx++);
            addRows(info.getStatus().name(), rowX, idx++);

        }

        wb.write(servletOutputStream);

    }

    @Override
    List<String> headers() {
        return Arrays.asList("Date", "Number", "Supplier", "Total Quantity", "Total Amount", "Status");
    }

}
