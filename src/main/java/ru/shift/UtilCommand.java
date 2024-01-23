package ru.shift;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardOpenOption.APPEND;

@Command
public class UtilCommand implements Runnable 
{
    private static final String DEFAULT_FILENAME_INT = "integers.txt";
    private static final String DEFAULT_FILENAME_FLOAT = "floats.txt";
    private static final String DEFAULT_FILENAME_STRING = "strings.txt";

    private final OpenOption[] writeOptions = new OpenOption[] {CREATE, TRUNCATE_EXISTING, WRITE};
    private final OpenOption[] appendOptions = new OpenOption[] {CREATE, APPEND};

    @Option(names = "-a", description = "Output will be appended to the existing files")
    private boolean isAddToExistingFile;

    @Option(names = "-s", description = "Short statistics will be displayed")
    private boolean isShortStat;

    @Option(names = "-f", description = "Full statistics will be displayed")
    private boolean isFullStat;

    @Option(names = "-p", description = "Output files prefix", defaultValue = "")
    private String prefix;

    @Option(names = "-o", description = "Output files path", defaultValue = "")
    private String path;

    @Parameters
    private List<File> inFiles;

    List<Long> longList; 
    List<Double> doubleList;
    List<String> stringList;

    public static void main( String[] args ) {
        int exitCode = new CommandLine(new UtilCommand()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println( "You've run UtilCommand!" );
        
        if (inFiles == null) {
            System.out.println("You have not provided files to filter.");
            return;
        }

        if (!Files.isDirectory(Paths.get(path))) {
            path = "";
            System.out.println("Provided path is not a directory. Current folder will be used.");
        }
        try {
            filterData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveData(longList, path + prefix + DEFAULT_FILENAME_INT);
        saveData(doubleList, path + prefix + DEFAULT_FILENAME_FLOAT);
        saveData(stringList, path + prefix + DEFAULT_FILENAME_STRING);
        if (isShortStat) {
            calculateShortStatistics();
        }
        if (isFullStat) {
            calculateFullStatistics();
        }
    }

    private void calculateFullStatistics() {
        if (!longList.isEmpty()) {
            long min = longList.stream().mapToLong(v -> v).min().getAsLong();
            long max = longList.stream().mapToLong(v -> v).max().getAsLong();
            double average = longList.stream().mapToLong(v -> v).average().getAsDouble();
            long sum = longList.stream().mapToLong(v -> v).sum();
            System.out.println("Integers statistics:");
            System.out.println("min: " + min + "\nmax: " + max + "\nsum: " + sum + "\naverage: " + average);
        }
        if (!doubleList.isEmpty()) {
            double min = doubleList.stream().mapToDouble(v -> v).min().getAsDouble();
            double max = doubleList.stream().mapToDouble(v -> v).max().getAsDouble();
            double average = doubleList.stream().mapToDouble(v -> v).average().getAsDouble();
            double sum = doubleList.stream().mapToDouble(v -> v).sum();
            System.out.println("Floats statistics:");
            System.out.println("min: " + min + "\nmax: " + max + "\nsum: " + sum + "\naverage: " + average);
        }
        if (!stringList.isEmpty()) {
            int min = stringList.stream().mapToInt(v -> v.length()).min().getAsInt();
            int max = stringList.stream().mapToInt(v -> v.length()).max().getAsInt();
            System.out.println("Strings statistics:");
            System.out.println("min: " + min + "\nmax: " + max);
        }
    }

    private void calculateShortStatistics() {
        System.out.println("Integers count: " + longList.size());
        System.out.println("Floats count: " + doubleList.size());
        System.out.println("Strings count: " + stringList.size());
    }

    private void filterData() throws IOException {
        this.stringList = new ArrayList<String>();
        this.longList = new ArrayList<Long>();
        this.doubleList = new ArrayList<Double>();
        
        for (File file : inFiles) {
            if (!Files.exists(file.toPath())) {
                System.out.println("File " + file + " doesn't exist.");
                continue;
            }
            System.out.println("Filtering " + file + "...");
            Scanner scanner = new Scanner(file);
            scanner.useLocale(Locale.US);
            while (scanner.hasNextLine()) {
                if (scanner.hasNextLong()) {
                    this.longList.add(scanner.nextLong());
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    continue;
                }
                if (scanner.hasNextDouble()) {
                    this.doubleList.add(scanner.nextDouble());
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    continue;
                }
                this.stringList.add(scanner.nextLine());
            }
            scanner.close();
        }
    }

    private void saveData(List<?> list, String fileName) {
        if (list.isEmpty()) {
            return;
        }
        String contentToAppend = list.stream()
            .map(n -> String.valueOf(n))
            .collect(Collectors.joining("\n"));
        contentToAppend += "\n";
        OpenOption[] options;
        if (isAddToExistingFile) {
            options = this.appendOptions;
        } else {
            options = this.writeOptions;     
        }
        try {
            Files.write(Paths.get(fileName), contentToAppend.getBytes(), options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
