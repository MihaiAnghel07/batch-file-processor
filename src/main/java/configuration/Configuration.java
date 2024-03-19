package configuration;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private final Logger logger = Logger.getLogger(Configuration.class);
    private final Properties properties = new Properties();
    private final String confPath = "conf/configuration.conf";
    private String inputPath;
    private String outputPath;
    private int threadsNumber;

    private Configuration() {
    }

    public void loadConf() {
        try {
            properties.load(new FileInputStream(confPath));
            this.inputPath = properties.getProperty("inputPath");
            this.outputPath = properties.getProperty("outputPath");
            this.threadsNumber = Integer.parseInt(properties.getProperty("threadsNumber"));
            logger.info("App successfully configured");
        } catch (IOException e) {
            logger.error(String.format("An error occurred while reading: %s", confPath));
            throw new RuntimeException(e);
        }
    }

    public String getInputPath() {
        return inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    private static class SingletonHelper {
        private static final Configuration INSTANCE = new Configuration();
    }

    public static Configuration getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
