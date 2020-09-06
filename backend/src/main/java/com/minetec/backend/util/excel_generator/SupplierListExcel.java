package com.minetec.backend.util.excel_generator;

import com.minetec.backend.dto.info.SupplierInfo;
import com.minetec.backend.dto.info.VehicleInfo;
import com.minetec.backend.entity.Supplier;
import com.minetec.backend.repository.projection.SupplierListItemProjection;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.data.domain.Page;

import javax.servlet.ServletOutputStream;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SupplierListExcel extends BaseExcel {

    public void generator(@NotNull final ServletOutputStream servletOutputStream,
                          @NotNull final Page<SupplierListItemProjection> supplierInfos) throws IOException {

        var wb = this.workbook();
        var sheet = wb.createSheet(this.getClass().getSimpleName());
        var row = sheet.createRow((short) 0);
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:F1"));

        addHeaders(wb, row, sheet);

        short rowNum = 1;
        for (SupplierListItemProjection info : supplierInfos) {
            short idx = 0;
            Row rowX = sheet.createRow(rowNum++);
            addRows(info.getName(), rowX, idx++);
            addRows(info.getAddress(), rowX, idx++);
            addRows(info.getRegisterNumber(), rowX, idx++);
            addRows(info.getTaxNumber(), rowX, idx++);
        }

        wb.write(servletOutputStream);
    }

    @Override
    List<String> headers() {
        return Arrays.asList("Name", "Address", "Register Number", "Tax Number");
    }

}
