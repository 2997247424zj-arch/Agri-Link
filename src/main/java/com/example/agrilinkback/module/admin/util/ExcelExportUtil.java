package com.example.agrilinkback.module.admin.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Excel 导出工具，将列表数据转为 xlsx 二进制下载。
 */
public final class ExcelExportUtil {

    private static final String CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private ExcelExportUtil() {
    }

    /**
     * 通用导出方法：生成 xlsx 并包装为 ResponseEntity 供直接返回。
     *
     * @param sheetName  工作表名
     * @param headers    列标题
     * @param data       数据行
     * @param extractors 每列的取值函数
     * @param fileName   下载文件名（含 .xlsx 后缀）
     */
    @SafeVarargs
    public static <T> ResponseEntity<byte[]> export(
            String sheetName,
            String[] headers,
            List<T> data,
            String fileName,
            Function<T, Object>... extractors
    ) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
                headerRow.getCell(i).setCellStyle(headerStyle);
            }

            for (int rowIdx = 0; rowIdx < data.size(); rowIdx++) {
                Row row = sheet.createRow(rowIdx + 1);
                T item = data.get(rowIdx);
                for (int col = 0; col < extractors.length; col++) {
                    Object value = extractors[col].apply(item);
                    if (value instanceof Number number) {
                        row.createCell(col).setCellValue(number.doubleValue());
                    } else {
                        row.createCell(col).setCellValue(value != null ? value.toString() : "");
                    }
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType(CONTENT_TYPE))
                    .body(out.toByteArray());
        }
    }
}
