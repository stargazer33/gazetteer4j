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
        //new DoBuildIndex().doBuild();

        DoSearch search=new DoSearch();
        search.setUp();

        search.edgeCases();

        search.onePartCities();
        search.onePartRegions();
        search.onePartCountries();

        search.twoPartsCityCountry();
        search.twoPartsCityState();
        search.twoPartsStateCountry();

        search.threeParts();
    }

}
