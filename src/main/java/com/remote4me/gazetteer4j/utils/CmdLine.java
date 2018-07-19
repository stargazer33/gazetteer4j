package com.remote4me.gazetteer4j.utils;

import com.remote4me.gazetteer4j.Location;
import com.remote4me.gazetteer4j.TextSearcher;
import com.remote4me.gazetteer4j.searcher.TextSearcherComposite;
import org.apache.commons.cli.*;

import java.io.PrintStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * TODO
 */
public class CmdLine {

    private static final Logger LOG = Logger.getLogger(CmdLine.class.getName());


    public static void main(String[] args) throws Exception {
        Option buildOpt = OptionBuilder
                .withLongOpt("build")
                .withDescription("Build Lucene indexes in the current directory")
                .create('b');

        Option searchOpt = OptionBuilder
                .withArgName("location name")
                .withLongOpt("search")
                .hasArgs()
                .withDescription("name to search the Gazetteer for")
                .create('s');

        Option helpOpt = OptionBuilder
                .withLongOpt("help")
                .withDescription("Print this message.")
                .create('h');

        Option resultCountOpt = OptionBuilder
                .withArgName("number of results")
                .withLongOpt("count").hasArgs()
                .withDescription("Number of best results to be returned for one location")
                .withType(Integer.class)
                .create('c');

        Options options = new Options();
        options.addOption(helpOpt);
        options.addOption(resultCountOpt);
        options.addOption(buildOpt);
        options.addOption(searchOpt);

        // create the parser
        CommandLineParser parser = new DefaultParser();

        // parse the command line arguments
        CommandLine line = parser.parse(options, args);


        if (line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("lucene-geo-gazetteer4j", options);
            return;
        }

        if (line.hasOption("build")) {
            new CompositeIndexBuilder().buildIndex();
            return;
        }

        if (line.hasOption("search")) {
            String countStr = line.getOptionValue("count", "1");
            int count = 1;
            if (countStr.matches("\\d+")) {
                count = Integer.parseInt(countStr);
            }

            // search and exit
            TextSearcher textSearcher = TextSearcherComposite.createDefaultCompositeSearcher();
            List<Location> result = textSearcher.search( line.getOptionValue("search"), count);
            writeResult(result, System.out);
            return;
        }
        else if (
                !line.hasOption("search") &&
                        !line.hasOption("build") &&
                        !line.hasOption("index") &&
                        !line.hasOption("help"))
        {
            System.err.println("Sub command not recognised");
            System.exit(-1);
        }
    }

    /*
    static void writeResult(List<Location> resolvedEntities, PrintStream out) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        out.println(gson.toJson(resolvedEntities) );
    }*/

    static void writeResult(List<Location> list, PrintStream out) {
        out.println("");
        list.forEach(location -> out.println(location));
    }

}
