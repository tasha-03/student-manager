package com.tasha.socialinfo.spreadsheet;

import org.apache.poi.ss.usermodel.*;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SpreadsheetReader {
    private static final ZoneId MOSCOW_ZONE = ZoneOffset.ofHours(3);
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(MOSCOW_ZONE);
    private static final DateTimeFormatter[] POSSIBLE_INPUT_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
    };

    public static List<List<String>> readRows(MultipartFile file, boolean hasHeaderRow) {
        SpreadsheetMediaType type = SpreadsheetMediaType.validateSpreadsheetFile(file);

        return switch (type) {
            case XLSX, XLS -> readExcel(file, hasHeaderRow);
            case CSV -> readCsv(file, hasHeaderRow);
            case ODS -> readOds(file, hasHeaderRow);
        };
    }

    private static List<List<String>> readExcel(MultipartFile file, boolean hasHeaderRow) {
        List<List<String>> rows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            boolean isFirstRow = true;
            for (Row r : sheet) {
                if (isFirstRow && hasHeaderRow) {
                    isFirstRow = false;
                    continue;
                }

                List<String> rowData = new ArrayList<>();
                boolean isRowEmpty = true;

                for (Cell c : r) {
                    String value = extractCellValue(c, evaluator);

                    if (value.isBlank() && !isRowEmpty)
                        throw new IllegalArgumentException(
                            "Empty cell at row " + (r.getRowNum() + 1) +
                                    ", column " + (c.getColumnIndex() + 1)
                        );

                    if (!value.isBlank()) {
                        isRowEmpty = false;
                        rowData.add(value);
                    }
                }
                if (!isRowEmpty) rows.add(rowData);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return rows;
    }

    private static String extractCellValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? ISO_FORMATTER.format(cell.getDateCellValue().toInstant().atZone(MOSCOW_ZONE))
                    : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                CellValue cellValue = evaluator.evaluate(cell);
                yield switch (cellValue.getCellType()) {
                    case STRING -> cellValue.getStringValue();
                    case NUMERIC -> String.valueOf(cellValue.getNumberValue());
                    case BOOLEAN -> String.valueOf(cellValue.getBooleanValue());
                    default -> "";
                };
            }
            default -> "";
        };
    }

    private static List<List<String>> readCsv(MultipartFile file, boolean hasHeaderRow) {
        List<List<String>> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;

            boolean isFirstRow = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstRow && hasHeaderRow) {
                    isFirstRow = false;
                    continue;
                }
                String[] cells = line.split(",");
                List<String> rowData = new ArrayList<>();
                for (String cell : cells) {
                    rowData.add(parseCsvCell(cell));
                }
                rows.add(rowData);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return rows;
    }

    private static String parseCsvCell(String cellValue) {
        cellValue = cellValue.trim();
        if (cellValue.isEmpty()) return "";
        for (DateTimeFormatter fmt : POSSIBLE_INPUT_FORMATS) {
            try {
                LocalDateTime dt = LocalDateTime.parse(cellValue, fmt);
                ZonedDateTime zdt = dt.atZone(MOSCOW_ZONE);
                return ISO_FORMATTER.format(zdt);
            } catch (Exception ignored) {}
            try {
                LocalDate d = LocalDate.parse(cellValue, fmt);
                ZonedDateTime zdt = d.atStartOfDay(MOSCOW_ZONE);
                return ISO_FORMATTER.format(zdt);
            } catch (Exception ignored) {}
        }
        return cellValue;
    }

    private static List<List<String>> readOds(MultipartFile file, boolean hasHeaderRow) {
        List<List<String>> rows = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.loadDocument(is);
            OdfTable sheet = ods.getTableList(false).getFirst();

            boolean isFirstRow = true;
            for (OdfTableRow r : sheet.getRowList()) {
                if (isFirstRow && hasHeaderRow) {
                    isFirstRow = false;
                    continue;
                }
                List<String> rowData = new ArrayList<>();
                boolean isRowEmpty = true;

                for (int i = 0; i < r.getCellCount(); i++) {
                    OdfTableCell cell = r.getCellByIndex(i);

                    String value = "";
                    if (cell != null) {
                        if (cell.getValueType() != null && cell.getValueType().equals("date")) {
                            if (cell.getDateValue() != null) {
                                value = ISO_FORMATTER.format(cell.getDateValue().toInstant().atZone(MOSCOW_ZONE));
                            }
                        } else {
                            value = cell.getDisplayText() != null ? cell.getDisplayText().trim() : "";
                        }

                        if (value.isBlank() && !isRowEmpty)
                            throw new IllegalArgumentException(
                                    "Empty cell at row " + (r.getRowIndex() + 1) +
                                            ", column " + (cell.getColumnIndex() + 1)
                            );
                    }

                    if (!value.isBlank()) {
                        rowData.add(value);
                        isRowEmpty = false;
                    }
                }
                if (!isRowEmpty) rows.add(rowData);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return rows;
    }
}
