package filehandling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private FileManager() {
    }

    public static List<String> readLines(String fileName) {
        Path path = Paths.get(fileName);
        List<String> result = new ArrayList<>();
        if (!Files.exists(path)) {
            return result;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    result.add(line);
                }
            }
        } catch (IOException exception) {
            System.out.println("Unable to read " + fileName + ": " + exception.getMessage());
        }
        return result;
    }

    public static void writeLines(String fileName, List<String> lines) {
        Path path = Paths.get(fileName);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException exception) {
            System.out.println("Unable to write " + fileName + ": " + exception.getMessage());
        }
    }
}
