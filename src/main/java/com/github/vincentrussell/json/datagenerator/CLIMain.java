package com.github.vincentrussell.json.datagenerator;

import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.github.vincentrussell.json.datagenerator.impl.JsonDataGeneratorImpl;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CLIMain {

    public static Options buildOptions() {
        Options options = new Options();

        Option o = new Option("s", "sourceFile", true, "the source file.");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("d", "destinationFile", true, "the destination file.");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("f", "functionClasses", true, "additional function classes that are on the classpath " +
                "and should be loaded");
        o.setRequired(false);
        o.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(o);

        return options;
    }

    public static void main(String[] args) throws IOException, JsonDataGeneratorException, ParseException, ClassNotFoundException {
        Options options = buildOptions();


        CommandLineParser parser = new DefaultParser();
        HelpFormatter help = new HelpFormatter();
        help.setOptionComparator(new OptionComparator(Arrays.asList("s", "d", "f")));

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            help.printHelp(CLIMain.class.getName(), options, true);
            throw e;
        }

        String source = cmd.getOptionValue("s");
        String destination = cmd.getOptionValue("d");
        String[] functionClasses = cmd.getOptionValues("f");

        File sourceFile = new File(source);
        File destinationFile = new File(destination);

        if (!sourceFile.exists()) {
            throw new FileNotFoundException(source + " cannot be found");
        }

        if (destinationFile.exists()) {
            throw new IOException(destination + " already exists");
        }

        if (functionClasses!=null) {
            for (String functionClass : functionClasses) {
                FunctionRegistry.getInstance().registerClass(Class.forName(functionClass));
            }
        }

        JsonDataGenerator jsonDataGenerator = new JsonDataGeneratorImpl();
        jsonDataGenerator.generateTestDataJson(sourceFile, destinationFile);

    }


    private static class OptionComparator implements Comparator<Option> {
        private final List<String> orderList;

        public OptionComparator(List<String> orderList) {
            this.orderList = orderList;
        }


        @Override
        public int compare(Option o1, Option o2) {
            int index1 = orderList.indexOf(o1.getOpt());
            int index2 = orderList.indexOf(o2.getOpt());
            return index1 - index2;
        }
    }

}
