/**
 * The MIT License (MIT)
 *
 * MSUSEL Quamoco Verifier
 * Copyright (c) 2015-2017 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.montana.gsoc.msusel.codetree.quamoco.verifier;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import edu.montana.gsoc.msusel.codetree.quamoco.verifier.config.VerifierConfiguration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The Command Line Interface for the Quality Model Verifier
 * 
 * @author Isaac Griffith
 * @version 1.1.1
 */
public class QMVerifyCLI {

    /**
     * Command line options object associated with this class.
     */
    private static final Options options;
    /**
     * Constant defining the default configuration file name
     */
    private static final String  DEFAULT_CONFIG = "verifier.json";
    /**
     * Constant defining the default output file name
     */
    private static final String  DEFAULT_OUTPUT = "verifier_output";
    /**
     * The output printwritter
     */
    private static PrintWriter   outputter;

    /**
     * Initializes the command line options object.
     */
    static
    {
        final Option help = Option.builder("h")
                .required(false)
                .longOpt("help")
                .desc("prints this message")
                .hasArg(false)
                .build();
        final Option output = Option.builder("o")
                .required(false)
                .longOpt("output")
                .argName("FILE")
                .desc("Name of the file to output results to.")
                .hasArg(true)
                .numberOfArgs(1)
                .build();
        final Option model = Option.builder("q")
                .required(false)
                .longOpt("quality-model")
                .desc("Prints the results to the console")
                .hasArg()
                .numberOfArgs(1)
                .argName("QM_FILE")
                .build();
        final Option config = Option.builder("c")
                .required(false)
                .longOpt("config")
                .desc("Path to configuration file.")
                .argName("FILE")
                .hasArg()
                .numberOfArgs(1)
                .build();
        options = new Options();
        QMVerifyCLI.options.addOption(help);
        QMVerifyCLI.options.addOption(output);
        QMVerifyCLI.options.addOption(model);
        QMVerifyCLI.options.addOption(config);
    }

    /**
     * Controls the execution of the LoCCounter given the command line object.
     *
     * @param line
     *            The parsed command line arguments.
     * @throws IOException
     *             If the file for output cannot be written to
     */
    public static void execute(final CommandLine line) throws IOException
    {
        VerifierConfiguration config = null;
        String qualityModel = null;
        String output = null;

        if (line.getOptions().length == 0 || line.hasOption('h'))
        {
            printHelp();
            return;
        }

        List<String> args = line.getArgList();
        for (String arg : args)
        {
            if (arg.startsWith("-D"))
            {

            }
        }

        if (line.hasOption('o'))
        {
            output = line.getOptionValue('o');
            outputter = new PrintWriter(
                    Files.newBufferedWriter(Paths.get(output), StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        }
        else
        {
            output = DEFAULT_OUTPUT;
        }

        if (line.hasOption('q'))
        {
            qualityModel = line.getOptionValue('q');
        }

        if (line.hasOption('c'))
        {
            config = VerifierConfiguration.load(line.getOptionValue('c'));
        }
        else
        {
            config = VerifierConfiguration.load(DEFAULT_CONFIG);
        }

        ModelVerifier verifier = new ModelVerifier(outputter);
        verifier.process(config, qualityModel, output);

        if (outputter != null)
        {
            outputter.flush();
            outputter.close();
        }
    }

    /**
     * Starting point of execution.
     *
     * @param args
     *            Raw command line arguments.
     */
    public static void main(final String... args)
    {
        final CommandLineParser parser = new DefaultParser();
        try
        {
            final CommandLine line = parser.parse(QMVerifyCLI.options, args);
            execute(line);
        }
        catch (final ParseException exp)
        {
            printHelp();
        }
        catch (IOException e)
        {

        }
    }

    /**
     * Prints the help message
     */
    private static void printHelp()
    {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                "qmverify", "\nValidate and verify a quamoco quality model.\n\n", QMVerifyCLI.options,
                "\nMontana State University, Gianforte School of Computing (C) 2015-2017", true);
    }
}
