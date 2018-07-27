package com.remote4me.gazetteer4j.utils;

import com.remote4me.gazetteer4j.index.DefaultDocFactory;
import com.remote4me.gazetteer4j.index.Location;
import com.remote4me.gazetteer4j.TextSearcher;
import com.remote4me.gazetteer4j.query.AltNamesFilter;
import com.remote4me.gazetteer4j.query.BuildIndexSearcher;
import com.remote4me.gazetteer4j.query.TextSearcherLucene;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.PrintStream;
import java.util.List;
import java.util.logging.Logger;

import static com.remote4me.gazetteer4j.FileSystem.INDEX_CITIES_15000;

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
        TextSearcher textSearcher = new TextSearcherLucene(
            new BuildIndexSearcher().createSearcher(INDEX_CITIES_15000),
            new StandardAnalyzer(),
            new DefaultDocFactory(),
            new AltNamesFilter()
        );
        List<Location> result = textSearcher.search( text, 10);
        writeResult(result, System.out);
    }


    static void writeResult(List<Location> list, PrintStream out) {
        out.println("");
        list.forEach(location -> out.println(location));
    }

}
