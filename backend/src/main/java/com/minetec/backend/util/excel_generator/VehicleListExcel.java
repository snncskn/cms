package com.minetec.backend.util.excel_generator;

import com.minetec.backend.dto.info.VehicleInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.data.domain.Page;

import javax.servlet.ServletOutputStream;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class VehicleListExcel extends BaseExcel {

    public void generator(@NotNull final ServletOutputStream servletOutputStream,
                          @NotNull final Page<VehicleInfo> vehicleInfos) throws IOException {

        var wb = this.workbook();
        var sheet = wb.createSheet(this.getClass().getSimpleName());
        var row = sheet.createRow((short) 0);
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:F1"));

        addHeaders(wb, row, sheet);

        short rowNum = 1;
        for (VehicleInfo info : vehicleInfos) {
            short idx = 0;
            Row rowX = sheet.createRow(rowNum++);
            addRows(info.getFleetNo(), rowX, idx++);
            addRows(info.getVehicleType().getName(), rowX, idx++);
            addRows(info.getCurrentMachineHours(), rowX, idx++);
            addRows(info.getServiceIntervalHours(), rowX, idx++);
            addRows(info.getLastServiceDate(), rowX, idx++);
            addRows(info.getLastServiceHours(), rowX, idx++);
        }

        wb.write(servletOutputStream);
    }

    @Override
    List<String> headers() {
        return Arrays.asList("Fleet Number", "Machine Type", "Current Machine Hours",
            "Service Interval Hours", "Last Service Day", "Last Service Hours");
    }

}
