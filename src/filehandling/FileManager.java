package filehandling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
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
            throw new UncheckedIOException("Unable to read " + fileName, exception);
        }
        return result;
    }

    public static void writeLines(String fileName, List<String> lines) {
        Path path = Paths.get(fileName).toAbsolutePath();
        Path temporaryPath = null;
        try {
            Files.createDirectories(path.getParent());
            temporaryPath = Files.createTempFile(
                    path.getParent(), "courier-" + path.getFileName(), ".tmp");
            try (BufferedWriter writer =
                         Files.newBufferedWriter(temporaryPath, StandardCharsets.UTF_8)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            try {
                Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            if (temporaryPath != null) {
                try {
                    Files.deleteIfExists(temporaryPath);
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw new UncheckedIOException("Unable to write " + fileName, exception);
        }
    }
}
