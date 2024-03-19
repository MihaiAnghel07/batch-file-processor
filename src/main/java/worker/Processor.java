package worker;

import configuration.Configuration;
import entity.Person;
import entity.datatypes.Sex;
import org.codehaus.plexus.util.StringUtils;
import status.Status;
import utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Processor implements Callable<Boolean> {

    private static final int NAME_INDEX = 0;
    private static final int SURNAME_INDEX = 1;
    private static final int BIRTHDATE_INDEX = 2;
    private static final int SEX_INDEX = 3;
    private static final int CITY_INDEX = 4;
    private static final int ID_INDEX = 5;
    private static final int DOMAIN_INDEX = 6;

    private final Status status = Status.getInstance();
    private final Configuration configuration = Configuration.getInstance();
    private final Path batchPath;

    public Processor(Path batchPath) {
        this.batchPath = batchPath;
    }

    @Override
    public Boolean call() throws IOException {

        List<Path> outputPaths = Utils.getSubPaths(Files.list(Path.of(configuration.getOutputPath())).toList());
        List<Path> inputPaths = Files.list(batchPath).toList();
        inputPaths.forEach(path -> {
            try {
                File file = new File(path.toString());
                Scanner reader = new Scanner(file);
                while (reader.hasNextLine()) {
                    String[] record = reader.nextLine().split(";");
                    Person person = getPerson(record);

                    // put the person in the corresponding output file
                    String outputFileName = String.valueOf(person.getName().charAt(0)) + person.getSurname().charAt(0) + ".csv";
                    Optional<Path> outputPath = outputPaths.stream()
                            .filter(path1 -> path1.toString().contains(outputFileName.toLowerCase(Locale.ROOT)))
                            .findAny();

                    outputPath.ifPresent(localOutputPath -> {
                        Utils.writeStringToPath(person.toCSV(), localOutputPath);
                    });
                }
                reader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while reading batch: " + batchPath);
            }
        });

        return true;
    }

    private static Person getPerson(String[] record) {
        return new Person(record[NAME_INDEX], record[SURNAME_INDEX], record[BIRTHDATE_INDEX],
                Sex.getSex(record[SEX_INDEX]), record[CITY_INDEX], record[ID_INDEX], record[DOMAIN_INDEX]);
    }
}
