package Application;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParseExcel {

    private static Object extractCellValue(Cell cell) {

        if (cell == null) return null;

        /* Currently parses only:
        * Strings
        * Booleans
        * Numerics -> and Dates from Numerics
        *  */

        return switch (cell.getCellType()) {
            /* todo:
                Expand switch cases in case more CellTypes become relevant,
                see: enum list CellType in CellType.class (.getCellType() -> CellType -> enum list))
            */
            case STRING -> cell.getStringCellValue().trim(); /* Trim leading and trailing spaces, typical for entity name restrictions */
            /* todo:
                Implement dynamic way to apply enum typing to specific model enums, e.g.:
                case STRING -> {
                    if (header.equals("gender")) {
                        String value = cell.getStringCellValue().trim().toUpperCase();
                        yield Gender.GenderNames.valueOf(value);
                    } else { yield cell.getStringCellValue();}
                } // -> This would now apply the enum type of an enum list defined in Model "Gender" containing the enum list "GenderNames"
            */
            case BOOLEAN -> cell.getBooleanCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate();
                    /* todo:
                        Expand this when dates with time are relevant
                    */
                }
                yield cell.getNumericCellValue();
            }

            default -> null;

        };
    }

    public static Map<String, List<Map<String, Object>>> parseExcel(String filename) throws Exception {

        /* Import static getFile() if placed in separate packages, getFile() will only work when finding one and exactly one matching file */
        Path path = Path.of(Utility.getFile(filename));
        System.out.println("Parsed file -> " + path);
        InputStream stream = Files.newInputStream(path);
        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();

        try (Workbook workbook = new XSSFWorkbook(stream)) {

//            DataFormatter formatter = new DataFormatter(); // Useful but not necessary for basic use

            for (int currentSheet = 0; currentSheet < workbook.getNumberOfSheets(); currentSheet++) {

                Sheet sheet = workbook.getSheetAt(currentSheet);
                String sheetName = sheet.getSheetName();
                List<Map<String, Object>> sheetData = new ArrayList<>();
                Row headerRow = sheet.getRow(0);

                if (headerRow == null) continue; /* Continue to next sheet when no more headers are found */

                /* Build headers */
                List<String> headers = new ArrayList<>();
                for (int currentCell = 0; currentCell < headerRow.getLastCellNum(); currentCell++) {
                    Cell cell = headerRow.getCell(currentCell);
                    headers.add(cell != null ? cell.toString().trim() : "");
                }

                /* Create list of entity objects */
                for (int currentRow = 1; currentRow <= sheet.getLastRowNum(); currentRow++) {
                    Row row = sheet.getRow(currentRow);

                    if (row == null) continue;

                    /* Create entity object */
                    Map<String, Object> entityEntry = new LinkedHashMap<>();

                    for (int indexHeader = 0; indexHeader < headers.size(); indexHeader++) {
                        String header = headers.get(indexHeader); /* Get correct header as key */
                        Object cell = extractCellValue(row.getCell(indexHeader)); /* Extract cell value as Cell, then formatted as Object */
                        entityEntry.put(header, cell); /* Add each "key=value" to current entry for building an entity object */
                    }

                    sheetData.add(entityEntry); /* Add entity object to list of entity objects */

                }

                data.put(sheetName, sheetData); /* Add sheetname with list of entity objects to excel data */

            }
        }

        return data;

    }

}