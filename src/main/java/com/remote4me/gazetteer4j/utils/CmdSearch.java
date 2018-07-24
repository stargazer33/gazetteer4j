package com.remote4me.gazetteer4j.utils;

import com.remote4me.gazetteer4j.Location;
import com.remote4me.gazetteer4j.TextSearcher;
import com.remote4me.gazetteer4j.search.TextSearcherComposite;

import java.io.PrintStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * Usage:
 *
 * <pre>{@code
 * mvn -q exec:java -Dexec.mainClass="com.remote4me.gazetteer4j.utils.CmdSearch" -Dexec.args="Springfield, VA"
 * }</pre>
 */
public class CmdSearch {

    private static final Logger LOG = Logger.getLogger(CmdSearch.class.getName());


    public static void main(String[] args) throws Exception
    {
        String text = "";
        for (String arg:args){
            text = text + " " +arg;
        }

        System.out.println("Searching for: "+text);
        TextSearcher textSearcher = TextSearcherComposite.createDefaultCompositeSearcher();
        List<Location> result = textSearcher.search( text, 10);
        writeResult(result, System.out);
    }


    static void writeResult(List<Location> list, PrintStream out) {
        out.println("");
        list.forEach(location -> out.println(location));
    }

}
