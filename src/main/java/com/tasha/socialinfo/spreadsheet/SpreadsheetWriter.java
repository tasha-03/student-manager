package com.tasha.socialinfo.spreadsheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SpreadsheetWriter {
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static byte[] writeExcel(List<List<String>> rows, List<String> header) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Студенты");

            int rowIndex = 0;

            if (header != null && !header.isEmpty()) {
                Row headerRow = sheet.createRow(rowIndex++);
                writeRow(headerRow, header);
            }

            for (List<String> row : rows) {
                Row r = sheet.createRow(rowIndex++);
                writeRow(r, row);
            }

            if (!rows.isEmpty()) {
                int columnCount = rows.getFirst().size();
                for (int i = 0; i < columnCount; i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();


        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to generate Excel file", e);
        }
    }

    private static void writeRow(Row r, List<String> values) {
        int col = 0;
        for (String value : values) {
            Cell c = r.createCell(col++);
            if (value == null || value.isBlank()) {
                c.setBlank();
                continue;
            }

            try {
                ZonedDateTime zdt = ZonedDateTime.parse(value, ISO_FORMATTER);
                c.setCellValue(java.util.Date.from(zdt.toInstant()));
                continue;
            } catch (Exception ignored) { }

            try {
                c.setCellValue(Double.parseDouble(value));
                continue;
            } catch (Exception ignored) { }

            c.setCellValue(value);
        }
    }

}
