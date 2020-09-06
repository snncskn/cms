package com.minetec.backend.util.excel_generator;

import com.minetec.backend.dto.info.workshop.RequestListInfo;
import com.minetec.backend.util.Utils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.data.domain.Page;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RequestListExcel extends BaseExcel {

    public void generator(final ServletOutputStream servletOutputStream,
                          final Page<RequestListInfo> requestListInfos) throws IOException {

        var wb = this.workbook();
        var sheet = wb.createSheet(this.getClass().getSimpleName());
        var row = sheet.createRow((short) 0);
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:J1"));

        addHeaders(wb, row, sheet);

        short rowNum = 1;
        for (RequestListInfo info : requestListInfos) {
            short idx = 0;
            var rowX = sheet.createRow(rowNum++);
            addRows(info.getReportNumber(), rowX, idx++);
            addRows(info.getFleetNumber(), rowX, idx++);
            addRows(info.getRequestDate(), rowX, idx++);
            addRows(info.getRequestUser(), rowX, idx++);
            addRows(info.getDeliveryTime(), rowX, idx++);
            addRows(info.getStockCode(), rowX, idx++);
            addRows(info.getItemDescription(), rowX, idx++);
            addRows(info.getJobCardItemStatus(), rowX, idx++);
            addRows(Utils.toString(info.getApproveQuantity()), rowX, idx++);
            addRows(Utils.toString(info.getRequestedQuantity()), rowX, idx++);
        }

        wb.write(servletOutputStream);
    }



    @Override
    List<String> headers() {
        return Arrays.asList("Job Card Num", "Fleet Number", "Request Date", "Requested User", "Delivery Time",
            "Part Number", "Desc", "Status", "Approve Qty", "Request Qty");
    }

}
