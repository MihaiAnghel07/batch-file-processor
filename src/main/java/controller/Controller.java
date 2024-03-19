package controller;

import configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.io.FileUtils;
import worker.InputHierarchyChecker;
import worker.OutputHierarchyGenerator;
import worker.Processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public abstract class Controller {

    private static final Logger logger = Logger.getLogger(Controller.class);
    private static final String INPUT_ERROR = "Input structure does not meet the requirements";
    private static final String OUTPUT_ERROR = "Couldn't generate output hierarchy";
    private static final String OTHER_ERROR = "An error occurred: ";
    private static final Configuration configuration = Configuration.getInstance();

    private Controller() {
    }

    public static void startApp() {
        logger.info("Starting app...");

        configuration.loadConf();
        generateOutputHierarchy();
        if (!checkInputStructure()) {
            logger.error(INPUT_ERROR);
            throw new RuntimeException(INPUT_ERROR);
        }

        processBatches();

        logger.info("App is stopped");
    }

    private static void generateOutputHierarchy() {
        try {
            // if exists, delete output directory and create a new one
            FileUtils.forceDelete(new File(configuration.getOutputPath()));
            Files.createDirectory(Path.of(configuration.getOutputPath()));

            int nrThreads = Math.min(2, configuration.getThreadsNumber());
            ExecutorService executorService = Executors.newFixedThreadPool(nrThreads);
            List<Future<Boolean>> futureList = new ArrayList<>();

            futureList.add(executorService.submit(new OutputHierarchyGenerator('a', 'm') {
            }));
            futureList.add(executorService.submit(new OutputHierarchyGenerator('n', 'z') {
            }));

            awaitTermination(executorService, futureList);

        } catch (IOException | InterruptedException e) {
            logger.error(OUTPUT_ERROR);
            throw new RuntimeException(e);
        }
    }

    private static void processBatches() {
        try {
            List<Path> paths = Files.list(Path.of(configuration.getInputPath())).toList();
            List<Future<Boolean>> futureList = new ArrayList<>();
            ExecutorService executorService = Executors.newFixedThreadPool(configuration.getThreadsNumber());
            for (Path path : paths) {
                futureList.add(executorService.submit(new Processor(path) {
                }));
            }

            awaitTermination(executorService, futureList);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkInputStructure() {
        try {
            // create a mask for checking the correctness of input hierarchy
            List<String> mask = new ArrayList<>();
            for (char i = 'a'; i <= 'z'; i++) {
                mask.add(configuration.getInputPath() + File.separator + i);
            }

            List<Path> paths = Files.list(Path.of(configuration.getInputPath())).toList();
            List<String> pathsAsString = new ArrayList<>(paths.stream().map(Path::toString).toList());
            pathsAsString.retainAll(mask);

            if (pathsAsString.size() != paths.size()) {
                return false;
            }

            // add workers for in depth structure checking
            List<Future<Boolean>> futureList = new ArrayList<>();
            ExecutorService executorService = Executors.newFixedThreadPool(configuration.getThreadsNumber());
            for (Path path : paths) {
                futureList.add(executorService.submit(new InputHierarchyChecker(path) {
                }));
            }

            return awaitTermination(executorService, futureList);

        } catch (IOException | InterruptedException e) {
            logger.error(INPUT_ERROR);
            throw new RuntimeException(INPUT_ERROR);
        }
    }

    private static boolean awaitTermination(ExecutorService executorService, List<Future<Boolean>> futureList) throws InterruptedException {
        boolean result = true;
        for (Future<Boolean> future : futureList) {
            try {
                result = result && future.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error(OTHER_ERROR + e.getMessage());
            }
        }

        executorService.shutdown();
        boolean isTerminated = executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        if (!isTerminated)
            throw new InterruptedException("Executor service exceeded the timeout");

        return result;
    }
}
