package Application;

/* Used libraries:
* xmlbeans-5.3.0.jar
* commons-collections4-4.4.jar
* commons-compress-1.27.1.jar
* commons-io-2.18.0.jar
* curvesapi-1.08.jar
* log4j-api-2.23.1.jar
* log4j-core-2.23.1.jar
* poi-5.4.1.jar
* poi-ooxml-5.4.1.jar
* poi-ooxml-lite-5.4.1.jar
*/

import java.util.List;
import java.util.Map;
import java.util.Set;

import static Application.ParseExcel.parseExcel;

public class Main {
    public static void main(String[] args) throws Exception {

        /* Building excel data as:
        * Map<String, List<Map<String, Object>>>
        *
        * This creates an indexed map for each Sheet by Sheet-name,
        * Then for each Sheet, the first Row values are parsed as Headers.
        * Each header then gets used for each Row as "entry",
        * which you could pass into something like a builder to seed a database.
        *  */

        Map<String, List<Map<String, Object>>> ExcelData = parseExcel("ExcelFile.xlsx");

        /* Print all sheets */
        System.out.println(ExcelData);

        /* Print all sheet names */
        Set<String> sheets = ExcelData.keySet();
        System.out.println(sheets);

        /* Print each sheet with its data */
        for (String sheet : sheets) {
            System.out.println("Sheet: {" + sheet + "} -> " + ExcelData.get(sheet));
        }

        /* Print each entry for each sheet and its data */
        for (String sheet : sheets) {
            System.out.println("Sheet: {" + sheet + "}");
            for (Map<String, Object> entry : ExcelData.get(sheet)) {
                System.out.println("Entry: -> " + entry);
            }
        }

        /* Print each key (header) for each entry for each sheet and its value (cell) */
        for (String sheet : sheets) {
            System.out.println("Sheet: {" + sheet + "}");
            for (Map<String, Object> entry : ExcelData.get(sheet)) {
                System.out.println("Entry: -> " + entry);
                Set<String> cells = entry.keySet();
                for (String cell : cells) {
                    Object valueCell = entry.get(cell);
                    String classCell = (valueCell != null) ? valueCell.getClass().getSimpleName() : null;
                    System.out.println("Cell: -> {" + cell + "} = {" + valueCell + "} of Class: " + classCell);
                }
            }
        }
    }
}