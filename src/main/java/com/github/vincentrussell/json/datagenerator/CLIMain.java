package com.github.vincentrussell.json.datagenerator;

import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.github.vincentrussell.json.datagenerator.impl.JsonDataGeneratorImpl;
import com.github.vincentrussell.json.datagenerator.impl.NonCloseableBufferedOutputStream;
import com.github.vincentrussell.json.datagenerator.impl.TimeoutInputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Main class for command line interface
 */
public final class CLIMain {

    private static TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();

    public static final String ENTER_JSON_TEXT = "Enter input json:\n\n ";

    /**
     * default constructor
     */
    private CLIMain() {

    }

    private static Options buildOptions() {
        Options options = new Options();

        Option o = new Option("s", "sourceFile", true,
            "the source file.");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("d", "destinationFile", true,
            "the destination file.  Defaults to System.out");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("f", "functionClasses", true,
            "additional function classes that are on the classpath "
                + "and should be loaded");
        o.setRequired(false);
        o.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(o);


        o = new Option("i", "interactiveMode", false,
            "interactive mode");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("t", "timeZone", true,
            "default time zone to use when dealing with dates");
        o.setRequired(false);
        options.addOption(o);

        return options;
    }

    /**
     * main method for command line interface
     * @param args arguments for command line interface
     * @throws IOException if there is an error reading from the input or writing to the output
     * @throws JsonDataGeneratorException if there is an error running the json data converter
     * @throws ParseException if the cli arguments cannot be parsed
     * @throws ClassNotFoundException if the addition classes passed in with -f cannot be found
     */
    @SuppressWarnings("checkstyle:linelength")
    public static void main(final String[] args) throws IOException,
        JsonDataGeneratorException, ParseException, ClassNotFoundException {
        try {
            TimeZone.setDefault(DEFAULT_TIMEZONE);

            Options options = buildOptions();


            CommandLineParser parser = new DefaultParser();
            HelpFormatter help = new HelpFormatter();
            help.setOptionComparator(new OptionComparator(Arrays.asList("s", "d", "f", "i", "t")));

            CommandLine cmd = null;
            try {
                cmd = parser.parse(options, args);

                String source = cmd.getOptionValue("s");
                boolean interactiveMode = cmd.hasOption('i');

                if (interactiveMode) {
                    System.out.println(ENTER_JSON_TEXT);
                    try (InputStream inputStream = new TimeoutInputStream(System.in,
                        1, TimeUnit.SECONDS);
                         OutputStream outputStream = new NonCloseableBufferedOutputStream(
                             System.out)) {
                        IOUtils.write("\n\n\n\n\n", outputStream);
                        JsonDataGenerator jsonDataGenerator = new JsonDataGeneratorImpl();
                        jsonDataGenerator.generateTestDataJson(inputStream, outputStream);
                    }

                    System.exit(0);
                }

                String destination = cmd.getOptionValue("d");
                String[] functionClasses = cmd.getOptionValues("f");
                String timeZone = cmd.getOptionValue("t");

                if (timeZone != null) {
                    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
                }

                if (source == null) {
                    throw new ParseException("Missing required option: s or i");
                }

                File sourceFile = new File(source);
                File destinationFile = destination != null ? new File(destination) : null;

                if (!sourceFile.exists()) {
                    throw new FileNotFoundException(source + " cannot be found");
                }

                if (destination != null && destinationFile.exists()) {
                    throw new IOException(destination + " already exists");
                }

                if (functionClasses != null) {
                    for (String functionClass : functionClasses) {
                        FunctionRegistry.getInstance().registerClass(Class.forName(functionClass));
                    }
                }

                JsonDataGenerator jsonDataGenerator = new JsonDataGeneratorImpl();
                try (InputStream inputStream = new FileInputStream(sourceFile);
                     OutputStream outputStream = destinationFile != null
                             ? new FileOutputStream(destinationFile)
                         : new NonCloseableBufferedOutputStream(System.out)) {
                    jsonDataGenerator.generateTestDataJson(inputStream, outputStream);
                }

            } catch (ParseException e) {
                System.err.println(e.getMessage());
                help.printHelp(CLIMain.class.getName(), options, true);
                throw e;
            }
        } finally {
            TimeZone.setDefault(DEFAULT_TIMEZONE);
        }

    }


    /**
     * helper {@link Comparator} to wort arguments in help
     */
    private static class OptionComparator implements Comparator<Option> {
        private final List<String> orderList;

        OptionComparator(final List<String> orderList) {
            this.orderList = orderList;
        }


        @Override
        public int compare(final Option o1, final Option o2) {
            int index1 = orderList.indexOf(o1.getOpt());
            int index2 = orderList.indexOf(o2.getOpt());
            return index1 - index2;
        }
    }

}
