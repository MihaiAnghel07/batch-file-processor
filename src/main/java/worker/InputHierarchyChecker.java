package worker;

import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class InputHierarchyChecker implements Callable<Boolean> {

    private final static Logger logger = Logger.getLogger(InputHierarchyChecker.class);

    private final Path path;

    public InputHierarchyChecker(Path path) {
        this.path = path;
    }

    @Override
    public Boolean call() throws Exception {
        List<Path> allPaths = Files.list(path).toList();
        List<Path> expectedPaths = Files.list(path)
                .filter(pathX -> pathX.toString().endsWith(".csv"))
                .toList();

        return allPaths.size() == expectedPaths.size();
    }
}
