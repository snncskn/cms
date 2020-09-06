package com.minetec.backend.util.excel_generator;

import com.minetec.backend.dto.info.workshop.BreakDownListInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.data.domain.Page;

import javax.servlet.ServletOutputStream;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WorkshopListExcel extends BaseExcel {

    public void generator(@NotNull final ServletOutputStream servletOutputStream,
                          @NotNull final Page<BreakDownListInfo> infos) throws IOException {

        var wb = this.workbook();
        var sheet = wb.createSheet(this.getClass().getSimpleName());
        var row = sheet.createRow((short) 0);
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:I1"));

        addHeaders(wb, row, sheet);

        short rowNum = 1;
        for (BreakDownListInfo info : infos) {
            short idx = 0;
            Row rowX = sheet.createRow(rowNum++);
            addRows(info.getBreakDownStartDate(), rowX, idx++);
            addRows(info.getBreakDownEndDate(), rowX, idx++);
            addRows(info.getJobCardStartDate(), rowX, idx++);
            addRows(info.getJobCardEndDate(), rowX, idx++);
            addRows(info.getLastUpdateDate(), rowX, idx++);
            addRows(info.getFleetNumber(), rowX, idx++);
            addRows(info.getReportNumber(), rowX, idx++);
            addRows(info.getJobCardStatus(), rowX, idx++);
            addRows(info.getSiteName(), rowX, idx++);
        }

        wb.write(servletOutputStream);
    }

    @Override
    List<String> headers() {
        return Arrays.asList("Break Down Opened Date", "Break Down Closed Date", "Job Card Opened Date",
            "Job Card Closed Date", "Last Update Date", "Fleet Number", "Report Number", "Status", "Site");
    }

}
