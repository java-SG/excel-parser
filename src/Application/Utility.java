package Application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utility {
    public static String getRoot() {
        return String.valueOf(
                Paths
                        .get(System.getProperty("user.dir"))
                        .toAbsolutePath()
                        .normalize()
        );
    }

    /* From root, recursively resolve targetFile's full path */
    public static String getFile(String targetFile) throws IOException {
        String root = getRoot();
        try (Stream<Path> paths = Files.walk(Path.of(root))) {

            List<Path> matches = paths
                    .filter(path -> path.getFileName().toString().equals(targetFile))
                    .filter(path -> path.toString().contains(root + "\\out\\production"))
                    .toList();

            /* If no files are found */
            if (matches.isEmpty()) {
                throw new FileNotFoundException(
                        targetFile + " does not exist within " + root
                );
            }

            /* If multiple files are found */
            if (matches.size() > 1) {
                String exceptionHead = "Multiple files named '" + targetFile + "' found within " + root + ":\n";
                String filesFound = matches.stream().map(Path::toString).collect(Collectors.joining("\n"));
                throw new IllegalStateException(
                        exceptionHead + filesFound
                );
            }
            return matches.getFirst().toAbsolutePath().normalize().toString();
        }
    }
}