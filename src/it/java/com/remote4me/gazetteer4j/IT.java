package com.remote4me.gazetteer4j;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by dima2 on 19.07.18.
 */
public class IT {

    @Test
    public void bigTest() throws IOException, ParseException {
        //new BuildIndex().doBuild();

        CompositeSearcher searcher=new CompositeSearcher();
        searcher.setUp();

        searcher.edgeCases();

        searcher.onePartCities();
        searcher.onePartRegions();
        searcher.onePartCountries();

        searcher.twoPartsCityCountry();
        searcher.twoPartsCityState();
        searcher.twoPartsStateCountry();

        searcher.threeParts();
    }

}
