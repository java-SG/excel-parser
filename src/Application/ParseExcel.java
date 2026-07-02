package Application;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static Application.Utility.getFile;

public class ParseExcel {

    public static Map<String, List<Map<String, Object>>> parseExcel(String filename) throws Exception {

        String filePath = getFile(filename);
        System.out.println("Parsed file -> " + filePath);

        return buildData(filePath);

    }

    private static Map<String, List<Map<String, Object>>> buildData(String filePath) {
        Map<String, List<Map<String, Object>>> excelData = new LinkedHashMap<>();

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {

            DataFormatter formatter = new DataFormatter();

            for (int currentSheet = 0; currentSheet < workbook.getNumberOfSheets(); currentSheet++) {

                Sheet sheet = workbook.getSheetAt(currentSheet);
                Row headers = sheet.getRow(0);

                if (headers == null) continue;

                /* Build headers */
                List<String> keys = buildKeys(headers, formatter);

                /* Create list of entity objects and add to sheet */
                List<Map<String, Object>> entries = buildEntries(sheet, keys);

                /* Add sheet with mapped entries */
                excelData.put(sheet.getSheetName(), entries);

            }

        } catch (IOException exception) {

            throw new RuntimeException(exception);

        }

        return excelData;

    }

    private static List<String> buildKeys(Row headerRow, DataFormatter formatter) {
        List<String> keyList = new ArrayList<>();
        for (int currentCell = 0; currentCell < headerRow.getLastCellNum(); currentCell++) {
            Cell cell = headerRow.getCell(currentCell);
            keyList.add(cell != null ? formatter.formatCellValue(cell).trim() : "");
        }
        return keyList;
    }

    private static List<Map<String, Object>> buildEntries(Sheet sheet, List<String> keys) {
        List<Map<String, Object>> sheetEntries = new ArrayList<>();
        for (int row = 1; row <= sheet.getLastRowNum(); row++) {

            Row currentRow = sheet.getRow(row);
            if (currentRow == null) continue;

            Map<String, Object> entityEntry = new LinkedHashMap<>();

            if (!buildEntry(entityEntry, keys, currentRow)) {
                break; /* If entityEntry only has null values, skip .add() */
            }
            ;
            sheetEntries.add(entityEntry);
        }
        return sheetEntries;
    }

    private static boolean buildEntry(Map<String, Object> entityEntry, List<String> keys, Row row) {
        for (int currentCell = 0; currentCell < keys.size(); currentCell++) {
            String key = keys.get(currentCell);
            Object value = extractCellValue(key, row.getCell(currentCell));
            if (!key.isEmpty()) {
                entityEntry.put(key, value); /* Skip column in case of missing header */
            }
        }
        return entityEntry.values().stream().anyMatch(Objects::nonNull);
    }

    private static Object extractCellValue(String header, Cell cell) {

        if (cell == null) return null;

        /* Currently parses only:
         * Strings
         * Booleans
         * Numerics -> and Dates from Numerics
         *  */

        return switch (cell.getCellType()) {
            /* todo: Expand switch cases in case more CellTypes become relevant, see: enum list CellType in CellType.class (.getCellType() -> CellType -> enum list)) */
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate();
                    /* todo: Expand this when dates with time are relevant */
                }
                yield cell.getNumericCellValue();
            }
            default -> null;
        };
    }
}