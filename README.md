Purpose of this excel-parser is to load any given excel file, 
then build an indexable object of data that can be used for things like Seeders to databases.

Each sheet's sheet name is derived,
Then for each cell on the first row per sheet, the headers are derived.
This will lay the template for which the data is processed in.

Then finally, for each subsequent row (excluding the first row),
the cell value is stored according to the template.

The data object is finally returned as a Map containing:
  lists, indexed by sheetname, containing:
    Maps which contains:
      the key (header) with its cell value

The data is classed as: Map<String, List<Map<String, Object>>>

A number of libraries is used to make things easy, primarily apache.poi for xml based structures:
  xmlbeans-5.3.0.jar
  commons-collections4-4.4.jar
  commons-compress-1.27.1.jar
  commons-io-2.18.0.jar
  curvesapi-1.08.jar
  log4j-api-2.23.1.jar
  log4j-core-2.23.1.jar
  poi-5.4.1.jar
  poi-ooxml-5.4.1.jar
  poi-ooxml-lite-5.4.1.jar

Along I wrote some helper methods to process getting the file initially, within certain boundaries, placed in Utility
This can ofcourse be either hardcoded or swapped out entirely based on preferred mechanics.

The main method will simply show some simple cases of how to iterate the data.

For me personally, the primary usage is to easily parse a huge load of seed data to my Java Springboot application, 
without writing a million lines of builders manually.
Especially due to the extend of what that application will encompass.
