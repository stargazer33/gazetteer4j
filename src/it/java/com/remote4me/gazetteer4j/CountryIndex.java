package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.search.AltNamesFilter;
import com.remote4me.gazetteer4j.search.TextSearcherLucene;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

/**
 * Created by dima2 on 14.07.18.
 */
public class CountryIndex {
    private TextSearcherLucene resolver;

    @Before
    public void setUp() throws IOException {
        resolver = new TextSearcherLucene(
                "geoIndex-countries",
                new StandardAnalyzer(),
                new DefaultDocFactory(),
                new AltNamesFilter()
        );
    }

    private Location runSearchReturnLocation(String geoStr) throws IOException, ParseException
    {
        List<Location> result;
        result= resolver.search(geoStr, 1);
        return result.get(0);
    }

    public void test1() throws IOException, ParseException {

        String country;
        Location loc;

        country = "Ukraine";
        loc = runSearchReturnLocation(country);
        Assert.assertEquals(country, loc.getName());

        country = "Украина";
        loc = runSearchReturnLocation(country);
        Assert.assertEquals("Ukraine", loc.getName());

        country = "USA";
        loc = runSearchReturnLocation(country);
        Assert.assertEquals("United States", loc.getName());

        country = "United States of America";
        loc = runSearchReturnLocation(country);
        Assert.assertEquals("United States", loc.getName());

        country = "Germany";
        loc = runSearchReturnLocation(country);
        Assert.assertEquals("DE", loc.getCountryCode());

        country = "Deutschland";
        loc = runSearchReturnLocation(country);
        Assert.assertEquals("DE", loc.getCountryCode());

        country = "Costa Rica";
        loc = runSearchReturnLocation(country);
        Assert.assertEquals("CR", loc.getCountryCode());

    }

}
