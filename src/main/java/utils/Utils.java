package utils;

import configuration.Configuration;
import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Utils {

    public static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }

    public static synchronized void writeStringToPath(String string, Path path) {
        try {
            Files.writeString(path, string, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Path> getSubPaths(List<Path> paths) {
        return paths.stream()
                .flatMap(path -> {
                    try {
                        return Files.walk(path, 2)
                                .filter(path1 -> path1.toString().contains(".csv"))
                                .toList().stream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public static void deleteEmptyOutput() {
        try {
            List<Path> outputPaths = getSubPaths(Files.list(Path.of(Configuration.getInstance().getOutputPath())).toList());
            List<Path> emptyOutput = outputPaths.stream()
                    .filter(path -> new File(String.valueOf(path)).length() == 0)
                    .toList();
            emptyOutput.forEach(emptyFile -> {
                try {
                    Files.delete(emptyFile);
                    Files.delete(emptyFile.getParent());
                    if (isDirectoryEmpty(emptyFile.getParent().getParent())) {
                        Files.delete(emptyFile.getParent().getParent());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isDirectoryEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                return entries.findFirst().isEmpty();
            }
        }

        return false;
    }

}
