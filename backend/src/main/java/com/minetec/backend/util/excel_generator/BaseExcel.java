package com.minetec.backend.util.excel_generator;

import org.apache.commons.lang3.RandomUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Sinan
 */
public abstract class BaseExcel {

    private CellStyle styleMain;

    static final int COLUMN_WIDTH = 7000;

    abstract List<String> headers();

    public CellStyle getStyleMain(final Workbook wb) {
        styleMain = wb.createCellStyle();
        styleMain.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
        styleMain.setFillPattern(CellStyle.ALIGN_GENERAL);
        styleMain.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        styleMain.setWrapText(true);
        styleMain.setBorderBottom(CellStyle.BORDER_THIN);
        styleMain.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styleMain.setBorderLeft(CellStyle.BORDER_THIN);
        styleMain.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        styleMain.setBorderRight(CellStyle.BORDER_THIN);
        styleMain.setRightBorderColor(IndexedColors.BLACK.getIndex());
        styleMain.setBorderTop(CellStyle.BORDER_THIN);
        styleMain.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return styleMain;
    }


    public void addRows(final String value, final Row rowX, final int rowNum) {
        Cell cell = rowX.createCell(rowNum);
        cell.setCellValue(new XSSFRichTextString(value));
        cell.setCellStyle(styleMain);
    }

    public XSSFWorkbook workbook() {
        return new XSSFWorkbook();
    }

    public void addHeaders(final XSSFWorkbook wb, final XSSFRow row, final Sheet sheet) {
        short headerIdx = 0;
        List<String> rowNames = this.headers();

        for (String header : rowNames) {
            Cell cell = row.createCell(headerIdx++);
            cell.setCellValue(new XSSFRichTextString(header));
            cell.setCellStyle(getStyleMain(wb));
        }
        setSizeColumnsWithHeader(sheet, COLUMN_WIDTH);
    }

    /**
     * @param name
     * @param wb
     * @return
     * @throws IOException
     */
    public File createFile(final String name, final Workbook wb) throws IOException {
        var file = new File(RandomUtils.nextInt() + ".xlsx");
        try (var out = new FileOutputStream(file)) {
            wb.write(out);
            out.flush();
        }
        return file;
    }

    void setSizeColumnsWithHeader(final Sheet sheet, final int width) {
        for (int i = 0; i < headers().size(); i++) {
            sheet.setColumnWidth(i, width);
        }
    }
}
