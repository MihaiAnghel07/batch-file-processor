package worker;

import configuration.Configuration;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public abstract class OutputHierarchyGenerator implements Callable<Boolean> {

    private final static Logger logger = Logger.getLogger(OutputHierarchyGenerator.class);

    private final Configuration configuration = Configuration.getInstance();
    private final char start;
    private final char end;

    public OutputHierarchyGenerator(char start, char end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Boolean call() throws Exception {
        for (char i = start; i <= end; i++) {
            for (char j = 'a'; j <= 'z'; j++) {
                Path path = Path.of(configuration.getOutputPath() + File.separator + i + File.separator + i + j);
                Files.createDirectories(path);
                Files.createFile(Path.of(path + File.separator + i + j + ".csv"));
            }
        }

        return true;
    }
}
